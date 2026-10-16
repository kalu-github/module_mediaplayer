#!/usr/bin/env python3
"""Build one FFmpeg DSO and a static mpv core for the two-DSO Android player.

Reuses verified dependency archives from build_native.py, in a separate prefix.
Installs headers in include/ and archives in lib/; CMake links both final DSOs.
"""
import argparse
import json
import os
from pathlib import Path
import re
import shlex
import shutil
import subprocess
import tempfile

import build_native as base
from package_aar import elf_report, TRIPLES

FFLIBS = ['avdevice', 'avfilter', 'avformat', 'avcodec', 'postproc',
          'swresample', 'swscale', 'avutil']


def exports(nm, library):
    output = subprocess.check_output([str(nm), '-D', '--defined-only', '--format=posix', str(library)], text=True)
    return {line.split()[0].split('@')[0] for line in output.splitlines()
            if len(line.split()) >= 2 and line.split()[1] != 'A'}


def install_static_inputs(prefix, abi, verified, original_manifest_sha256, revision,
                          archives, system, ffmpeg_links):
    """Replace this ABI's old installation with headers and static archives."""
    ffarchives = ['lib' + name + '.a' for name in FFLIBS]
    ffsystem = []
    for flag in ffmpeg_links:
        if flag.startswith('-L'):
            continue
        archive = prefix / 'lib' / ('lib' + flag[2:] + '.a') if flag.startswith('-l') else None
        if archive is not None and archive.is_file():
            if archive.name not in ffarchives:
                ffarchives.append(archive.name)
        elif flag not in ffsystem:
            ffsystem.append(flag)
    all_archives = sorted(set(archives + ffarchives))
    target = base.MODULE / 'src/main/cpp/ffmpeg' / abi
    target.parent.mkdir(parents=True, exist_ok=True)
    with tempfile.TemporaryDirectory(prefix='static-install-', dir=target.parent) as temporary:
        stage = Path(temporary) / abi
        stage.mkdir()
        shutil.copytree(prefix / 'include', stage / 'include')
        (stage / 'lib').mkdir()
        for name in all_archives:
            shutil.copy2(prefix / 'lib' / name, stage / 'lib' / name)
        cmake = ''
        for component, libraries, flags in [('FFMPEG', ffarchives, ffsystem),
                                             ('MPV', archives, system)]:
            cmake += 'set(' + component + '_STATIC_ARCHIVES\n' + ''.join(
                '  "${MPV_DEPS}/lib/' + name + '"\n' for name in libraries) + ')\n'
            cmake += 'set(' + component + '_SYSTEM_LINK_FLAGS\n' + ''.join(
                '  "' + flag + '"\n' for flag in flags) + ')\n'
        (stage / 'link.cmake').write_text(cmake)
        for name in ['ffmpeg.exports', 'ffmpeg-roots.rsp']:
            shutil.copy2(prefix / name, stage / name)
        metadata = ['link.cmake', 'ffmpeg.exports', 'ffmpeg-roots.rsp']
        manifest = {'layout': 'static-inputs', 'abi': abi, 'ndk': revision, 'api': 23,
                    'original_build': verified, 'original_manifest_sha256': original_manifest_sha256,
                    'recipe_sha256': base.digest(Path(__file__)),
                    'ffmpeg_export_count': len((stage / 'ffmpeg-roots.rsp').read_text().splitlines()),
                    'libraries': {},
                    'archives': {name: base.digest(stage / 'lib' / name) for name in all_archives},
                    'link_files': {name: base.digest(stage / name) for name in metadata}}
        (stage / 'manifest.json').write_text(json.dumps(manifest, indent=2) + '\n')
        if target.exists():
            shutil.rmtree(target)
        shutil.move(str(stage), target)
    print('Headers and static archives installed: ' + str(target), flush=True)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--ndk', type=Path, required=True)
    parser.add_argument('--abi', choices=list(base.ARCHES), default='arm64-v8a')
    args = parser.parse_args()
    ndk = args.ndk.resolve()
    abi = args.abi
    arch = base.ARCHES[abi]
    original = base.MODULE / 'build/native'
    original_prefix = original / 'prefix' / arch
    original_manifest = base.MODULE / 'src/main/cpp/ffmpeg' / abi / 'mpv/manifest.json'
    if original_manifest.exists():
        verified = json.loads(original_manifest.read_text())
        original_manifest_sha256 = base.digest(original_manifest)
    else:
        installed = json.loads((original_manifest.parent.parent / 'manifest.json').read_text())
        verified = installed['original_build']
        original_manifest_sha256 = installed['original_manifest_sha256']
    revision = re.search(r'Pkg.Revision\s*=\s*(\S+)', (ndk / 'source.properties').read_text()).group(1)
    if verified['ndk'] != revision or verified['abi'] != abi:
        raise RuntimeError('Build the original dependencies with the same NDK and ABI first.')
    for name, expected in verified['libraries'].items():
        if base.digest(original_prefix / 'lib' / name) != expected:
            raise RuntimeError('Original library differs from its manifest: ' + name)
    work = base.MODULE / 'build/merged-native'
    prefix = work / 'prefix' / arch
    fingerprint = {'original_manifest': original_manifest_sha256, 'ndk': str(ndk)}
    marker = work / ('config-' + abi + '.json')
    work.mkdir(parents=True, exist_ok=True)
    if marker.exists() and json.loads(marker.read_text()) != fingerprint:
        raise RuntimeError('Merged build inputs changed. Remove build/merged-native before rebuilding.')
    marker.write_text(json.dumps(fingerprint, indent=2))
    base.setup(work, ndk, revision, disable_x86_asm=verified.get('disable_x86_asm', False))
    (work / 'deps').mkdir(exist_ok=True)
    for name in ['ffmpeg', 'mpv']:
        target = work / 'deps' / name
        if target.is_symlink():
            target.unlink()
        if not target.exists():
            # Recipes use ../../scripts relative to the real working directory.
            # A source symlink would execute recipes from the old build tree.
            shutil.copytree(original / 'deps' / name, target,
                            ignore=shutil.ignore_patterns('.git', '_build*', '_merged*'))
    # Flatten the upstream /usr/local install prefix in the isolated staging tree.
    prefix.mkdir(parents=True, exist_ok=True)
    for name in ['usr', 'local']:
        if not (prefix / name).exists():
            (prefix / name).symlink_to('.', target_is_directory=True)
    shutil.copytree(original_prefix / 'include', prefix / 'include', dirs_exist_ok=True)
    (prefix / 'lib/pkgconfig').mkdir(parents=True, exist_ok=True)
    for archive in (original_prefix / 'lib').glob('*.a'):
        shutil.copy2(archive, prefix / 'lib' / archive.name)
    for pc in (original_prefix / 'lib/pkgconfig').glob('*.pc'):
        # Prevent pkg-config from searching the old shared-library prefix.
        text = pc.read_text().replace(str(original_prefix), str(prefix))
        (prefix / 'lib/pkgconfig' / pc.name).write_text(text)
    ffmpeg = work / 'scripts/ffmpeg.sh'
    ffmpeg.write_text(ffmpeg.read_text().replace('_build$ndk_suffix', '_merged$ndk_suffix').replace(
        '--disable-static --enable-shared', '--enable-static --disable-shared --enable-pic').replace(
        '--nm=llvm-nm', '--nm=llvm-nm --ar=llvm-ar --ranlib=llvm-ranlib --strip=llvm-strip'))
    mpv = work / 'scripts/mpv.sh'
    text = mpv.read_text().replace('_build$ndk_suffix', '_merged$ndk_suffix').replace(
        '--default-library shared', '--default-library static')
    text = re.sub(r'if \[ -f \$build/libmpv.a \]; then.*?\nfi\n', '', text, flags=re.S)
    mpv.write_text(text)
    toolchain = next((ndk / 'toolchains/llvm/prebuilt').iterdir())
    env = os.environ.copy()
    env['PATH'] = os.pathsep.join(map(str, [base.MODULE / 'build/host-tools/bin',
        ndk.parent.parent / 'cmake/3.22.1/bin', toolchain / 'bin'])) + os.pathsep + env['PATH']
    env['cores'] = str(min(os.cpu_count() or 4, 8))
    base.run(['bash', 'buildall.sh', '-n', '--arch', arch, 'ffmpeg'], cwd=work, env=env)
    env['PKG_CONFIG_SYSROOT_DIR'] = str(prefix)
    env['PKG_CONFIG_LIBDIR'] = str(prefix / 'lib/pkgconfig')
    env.pop('PKG_CONFIG_PATH', None)
    pc_args = shlex.split(subprocess.check_output(
        ['pkg-config', '--static', '--libs'] + ['lib' + name for name in FFLIBS], env=env, text=True))
    pc_args = [a for a in pc_args if a not in {'-l' + name for name in FFLIBS}]
    # Preserve every public FFmpeg entry point, hiding bundled third-party symbols.
    nm = toolchain / 'bin/llvm-nm'
    expected_exports = set().union(*(exports(nm, original_prefix / 'lib' / ('lib' + name + '.so'))
                                     for name in FFLIBS))
    version_script = prefix / 'ffmpeg.exports'
    version_script.write_text('FFMPEG_7 {\n global:\n' + ''.join(
        '  ' + name + ';\n' for name in sorted(expected_exports)) + ' local: *;\n};\n')
    # FFmpeg archives share a few utility objects (e.g. half2float.o). Force all
    # public APIs as roots, then let archive extraction resolve shared helpers
    # once. --whole-archive would duplicate those definitions.
    roots = prefix / 'ffmpeg-roots.rsp'
    roots.write_text(''.join('-Wl,--undefined=' + name + '\n' for name in sorted(expected_exports)))
    triple = 'armv7a-linux-androideabi' if abi == 'armeabi-v7a' else TRIPLES[abi]
    compiler = toolchain / 'bin' / (triple + '23-clang')
    merged_ffmpeg = prefix / 'lib/libffmpeg.so'
    base.run([compiler, '-shared', '-Wl,-soname,libffmpeg.so', '-Wl,--no-undefined',
              '-Wl,-z,max-page-size=16384', '-Wl,--version-script=' + str(version_script),
              '@' + str(roots), '-o', merged_ffmpeg, '-Wl,--start-group'] +
             [prefix / 'lib' / ('lib' + name + '.a') for name in FFLIBS] +
             pc_args + ['-Wl,--end-group'], env=env)
    missing_exports = expected_exports - exports(nm, merged_ffmpeg)
    if missing_exports:
        raise RuntimeError('Merged FFmpeg lost exported symbols: ' + str(sorted(missing_exports)))
    elf_report([merged_ffmpeg], toolchain, abi)
    for name in FFLIBS:
        pc = prefix / 'lib/pkgconfig' / ('lib' + name + '.pc')
        text = re.sub(r'^Libs:.*$', 'Libs: -L${libdir} -lffmpeg', pc.read_text(), flags=re.M)
        text = re.sub(r'^(Libs.private|Requires|Requires.private):.*$', r'\1:', text, flags=re.M)
        pc.write_text(text)
    base.run(['bash', 'buildall.sh', '-n', '--arch', arch, 'mpv'], cwd=work, env=env)
    links = shlex.split(subprocess.check_output(['pkg-config', '--static', '--libs', 'mpv'], env=env, text=True))
    archives, system = [], []
    for flag in links:
        if flag.startswith('-L') or flag in {'-lc++', '-lffmpeg'}:
            continue
        archive = prefix / 'lib' / ('lib' + flag[2:] + '.a') if flag.startswith('-l') else None
        if archive is not None and archive.is_file():
            if archive.name not in archives:
                archives.append(archive.name)
        elif flag not in system:
            system.append(flag)
    if 'libmpv.a' not in archives:
        raise RuntimeError('Static mpv was not produced.')
    install_static_inputs(prefix, abi, verified, original_manifest_sha256, revision,
                          archives, system, pc_args)


if __name__ == '__main__':
    main()
