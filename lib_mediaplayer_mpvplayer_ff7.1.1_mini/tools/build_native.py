#!/usr/bin/env python3
"""Build the supplied mpv source with the supplied mpv-android dependency recipes.

All downloads/builds stay under module/build/native. Only successful builds are
installed under cpp/ffmpeg/<ABI>/mpv and jniLibs. No SDK installation is performed.
"""
import argparse
import hashlib
import json
import os
from pathlib import Path
import platform
import re
import shutil
import subprocess
import sys
import tarfile
import tempfile

MODULE = Path(__file__).resolve().parents[1]
ROOT = MODULE.parent


def local_source(name):
    bundled = MODULE / 'src/main/cpp' / name
    return bundled if bundled.is_dir() else ROOT / 'repo' / name


REFERENCE = local_source('mpv-android-2026-09-17') / 'buildscripts'
MPV = local_source('mpv-0.41.0')
ARCHES = {'arm64-v8a': 'arm64', 'armeabi-v7a': 'armv7l', 'x86': 'x86', 'x86_64': 'x86_64'}
FFMPEG_TAG = 'n7.1.1'
LOCAL_FFMPEG = local_source('ffmpeg-7.1.1')
LOCAL_LIBASS = local_source('libass-0.17.4')
LOCAL_LIBPLACEBO = local_source('libplacebo-7.351.0-full')
if not LOCAL_LIBPLACEBO.is_dir():
    LOCAL_LIBPLACEBO = local_source('libplacebo-7.351.0')
PLACEBO_SUBMODULE_FILES = {
    'glad': 'glad/__init__.py',
    'jinja': 'src/jinja2/__init__.py',
    'markupsafe': 'src/markupsafe/__init__.py',
    'fast_float': 'include/fast_float/fast_float.h',
}


def run(args, cwd=None, env=None):
    print('+ ' + ' '.join(map(str, args)), flush=True)
    subprocess.run(list(map(str, args)), cwd=cwd, env=env, check=True)


def digest(path):
    h = hashlib.sha256()
    with path.open('rb') as stream:
        for chunk in iter(lambda: stream.read(1024 * 1024), b''):
            h.update(chunk)
    return h.hexdigest()


def source_digest(path):
    h = hashlib.sha256()
    for item in sorted(path.rglob('*')):
        if item.is_file() and '.git' not in item.parts:
            h.update(str(item.relative_to(path)).encode())
            h.update(bytes.fromhex(digest(item)))
    return h.hexdigest()


def validate_ffmpeg_source(source):
    source = Path(source).expanduser().resolve()
    required = ['RELEASE', 'configure', 'Makefile', 'libavcodec/avcodec.h',
                'libavformat/avformat.h', 'libavfilter/avfilter.h',
                'libavutil/avutil.h', 'libswresample/swresample.h', 'libswscale/swscale.h']
    missing = [name for name in required if not (source / name).is_file()]
    if missing:
        raise RuntimeError('Incomplete FFmpeg source at ' + str(source) + ': ' + ', '.join(missing))
    version = (source / 'RELEASE').read_text().strip()
    if version != FFMPEG_TAG.removeprefix('n'):
        raise RuntimeError(f'Expected FFmpeg {FFMPEG_TAG}, found {version} in {source}')
    return source


def validate_local_library(name, source):
    source = Path(source).expanduser().resolve()
    required = {'libass': ['meson.build', 'RELEASEVERSION', 'libass/ass.h', 'autogen.sh'],
                'libplacebo': ['meson.build', 'src/include/libplacebo/renderer.h',
                              'src/convert.cc']}[name]
    missing = [item for item in required if not (source / item).is_file()]
    if missing:
        raise RuntimeError(f'Incomplete {name} source at {source}: ' + ', '.join(missing))
    if name == 'libass':
        version = (source / 'RELEASEVERSION').read_text().strip()
        valid = version == '0.17.4'
    else:
        meson = (source / 'meson.build').read_text()
        major = re.search(r'# Major version\s+(\d+),', meson)
        fix = re.search(r'# Fix version\s+(\d+)\)', meson)
        api_section = meson.split('# API version', 1)[-1].split('# Fix version', 1)[0]
        apis = re.findall(r"^\s*'(\d+)':", api_section, re.M)
        version = f'{major.group(1)}.{len(apis)}.{fix.group(1)}' if major and fix else 'unknown'
        valid = version == '7.351.0' and set(map(int, apis)) == set(range(1, 352))
    if not valid:
        raise RuntimeError(f'Unexpected {name} version {version} at {source}')
    return source


def missing_placebo_submodules(source):
    # This build uses vendored OpenGL generator dependencies and fast_float.
    # Vulkan and demo submodules are unused by the Android recipe.
    return [name for name, file in PLACEBO_SUBMODULE_FILES.items()
            if not (source / '3rdparty' / name / file).is_file()]


def stage_local_source(work, name, source, version, source_hash):
    deps = work / 'deps'
    deps.mkdir(parents=True, exist_ok=True)
    target = deps / name
    provenance = {'path': str(source), 'version': version, 'sha256': source_hash}
    marker = target / '.local-source.json'
    if target.exists():
        previous = json.loads(marker.read_text()) if marker.exists() else {}
        if (previous.get('version'), previous.get('sha256')) != (version, source_hash):
            raise RuntimeError(f'Staged {name} differs from local source. Clean build/native before rebuilding.')
        if previous != provenance:
            marker.write_text(json.dumps(provenance, indent=2) + '\n')
        return
    # A failed copy must never masquerade as a complete dependency on the next run.
    with tempfile.TemporaryDirectory(prefix=name + '-stage-', dir=deps) as temporary:
        copied = Path(temporary) / 'source'
        shutil.copytree(source, copied, ignore=shutil.ignore_patterns('.git'))
        (copied / '.local-source.json').write_text(json.dumps(provenance, indent=2) + '\n')
        copied.rename(target)
    print(f'Using local {name} source: {source}', flush=True)


def clone(deps, name, url, tag):
    destination = deps / name
    if destination.exists():
        return
    temporary = deps / (name + '.partial')
    if temporary.exists():
        shutil.rmtree(temporary)
    run(['git', 'clone', '--depth=1', '--branch', tag, '--recurse-submodules', '--shallow-submodules', url, temporary])
    temporary.rename(destination)


def archive(deps, name, url):
    destination = deps / name
    if destination.exists():
        return
    with tempfile.TemporaryDirectory(prefix=name + '-', dir=deps) as temporary:
        temporary = Path(temporary)
        package = temporary / 'source.tar'
        run(['curl', '--fail', '--location', '--retry', '3', '--output', package, url])
        extracted = temporary / 'source'
        extracted.mkdir()
        # tar's data filter rejects path traversal and escaping links.
        with tarfile.open(package) as tar:
            if not hasattr(tarfile, 'data_filter'):
                raise RuntimeError('Python with tarfile.data_filter is required (3.12+, or security-patched 3.9+).')
            tar.extractall(extracted, filter='data')
        entries = list(extracted.iterdir())
        if len(entries) != 1 or not entries[0].is_dir():
            raise RuntimeError('Unexpected source archive layout: ' + url)
        (entries[0] / '.download.json').write_text(json.dumps({'url': url, 'sha256': digest(package)}, indent=2))
        entries[0].rename(destination)


def download(work):
    versions = dict(re.findall(r'^v_(\w+)=(\S+)', (REFERENCE / 'include/depinfo.sh').read_text(), re.M))
    deps = work / 'deps'
    deps.mkdir(exist_ok=True)
    # Pin git dependencies instead of following upstream HEAD on every build.
    clone(deps, 'ffmpeg', 'https://github.com/FFmpeg/FFmpeg', FFMPEG_TAG)
    clone(deps, 'dav1d', 'https://github.com/videolan/dav1d', '1.5.1')
    clone(deps, 'libass', 'https://github.com/libass/libass', '0.17.4')
    clone(deps, 'libplacebo', 'https://github.com/haasn/libplacebo', 'v7.351.0')
    clone(deps, 'freetype2', 'https://gitlab.freedesktop.org/freetype/freetype.git', 'VER-' + versions['freetype'].replace('.', '-'))
    urls = {
        'mbedtls': f"https://github.com/Mbed-TLS/mbedtls/releases/download/mbedtls-{versions['mbedtls']}/mbedtls-{versions['mbedtls']}.tar.bz2",
        'fribidi': f"https://github.com/fribidi/fribidi/releases/download/v{versions['fribidi']}/fribidi-{versions['fribidi']}.tar.xz",
        'harfbuzz': f"https://github.com/harfbuzz/harfbuzz/releases/download/{versions['harfbuzz']}/harfbuzz-{versions['harfbuzz']}.tar.xz",
        'unibreak': f"https://github.com/adah1972/libunibreak/releases/download/libunibreak_{versions['unibreak'].replace('.', '_')}/libunibreak-{versions['unibreak']}.tar.gz",
        'libxml2': f"https://gitlab.gnome.org/GNOME/libxml2/-/archive/v{versions['libxml2']}/libxml2-v{versions['libxml2']}.tar.gz",
        'fontconfig': f"https://gitlab.freedesktop.org/fontconfig/fontconfig/-/archive/{versions['fontconfig']}/fontconfig-{versions['fontconfig']}.tar.gz",
        'lua': f"https://www.lua.org/ftp/lua-{versions['lua']}.tar.gz",
        'curl': f"https://curl.se/download/curl-{versions['curl']}.tar.gz",
    }
    for name, url in urls.items():
        archive(deps, name, url)


def setup(work, ndk, revision, disable_x86_asm=False):
    for name in ['include', 'scripts']:
        shutil.copytree(REFERENCE / name, work / name, dirs_exist_ok=True)
    script = (REFERENCE / 'buildall.sh').read_text()
    # Explicit shell invocation ignores options in the shebang.
    script = script.replace('#!/bin/bash -e', '#!/bin/bash\nset -e')
    # Apple's /bin/bash 3.2 does not support declare -g. Export preserves global scope.
    script = script.replace('declare -g "$varname=0"', 'export "$varname=0"')
    (work / 'buildall.sh').write_text(script)
    depinfo = work / 'include/depinfo.sh'
    text = depinfo.read_text()
    text = re.sub(r'^v_ndk=.*$', 'v_ndk=local', text, flags=re.M)
    text = re.sub(r'^v_ndk_n=.*$', 'v_ndk_n=' + revision, text, flags=re.M)
    depinfo.write_text(text)
    sdk = work / 'sdk'
    sdk.mkdir(exist_ok=True)
    link = sdk / 'android-ndk-local'
    if link.is_symlink():
        link.unlink()
    link.symlink_to(ndk, target_is_directory=True)
    # Explicit renderer/audio selection: never autodetect SDL in the build host.
    recipe = work / 'scripts/mpv.sh'
    text = recipe.read_text().replace('-D{lua,libcurl}=enabled', '-Dlua=enabled').replace('-Dmanpage-build=disabled',
        '-Dmanpage-build=disabled -Dsdl2-video=disabled -Dsdl2-audio=disabled '
        '-Dsdl2-gamepad=disabled -Dgl=enabled -Degl-android=enabled '
        '-Daudiotrack=enabled -Dopensles=enabled')
    recipe.write_text(text)
    ffmpeg = work / 'scripts/ffmpeg.sh'
    ffmpeg.write_text(ffmpeg.read_text().replace('--extra-ldflags="-L$prefix_dir/lib"', '--extra-ldflags="-L$prefix_dir/lib -Wl,-z,max-page-size=16384"'))
    if disable_x86_asm:
        ffmpeg.write_text(ffmpeg.read_text().replace('cpu=generic', 'cpu="generic --disable-asm"'))
        dav1d = work / 'scripts/dav1d.sh'
        dav1d.write_text(dav1d.read_text().replace('-Denable_tests=false',
            '-Denable_tests=false -Denable_asm=false'))
    # libass 0.17.4 includes a Meson build; avoid regenerating autotools files.
    ass = (REFERENCE / 'scripts/fribidi.sh').read_text().replace(
        '-D{tests,docs}=false',
        '-Dfontconfig=enabled -Dlibunibreak=enabled '
        '-Dtest=disabled -Dcompare=disabled -Dprofile=disabled '
        '-Dfuzz=disabled -Dcheckasm=disabled')
    (work / 'scripts/libass.sh').write_text(ass)
    if disable_x86_asm:
        (work / 'scripts/libass.sh').write_text(ass.replace('-Dfontconfig=enabled',
            '-Dasm=disabled -Dfontconfig=enabled'))
    # The only GNU sed-specific operation in the recipes is this in-place edit.
    placebo = work / 'scripts/libplacebo.sh'
    placebo.write_text(placebo.read_text().replace(
        '${SED:-sed} \'/^Libs:/ s|$| -lc++|\' "$prefix_dir/lib/pkgconfig/libplacebo.pc" -i',
        'sed \'/^Libs:/ s|$| -lc++|\' "$prefix_dir/lib/pkgconfig/libplacebo.pc" '
        '> "$prefix_dir/lib/pkgconfig/libplacebo.pc.tmp"\n'
        'mv "$prefix_dir/lib/pkgconfig/libplacebo.pc.tmp" "$prefix_dir/lib/pkgconfig/libplacebo.pc"'))
    path_script = work / 'include/path.sh'
    path_script.write_text(path_script.read_text().replace(
        'export INSTALL=`which ginstall`', 'export INSTALL=$(command -v install)').replace(
        'export SED=gsed', 'export SED=sed'))
    lua = work / 'scripts/lua.sh'
    lua.write_text(lua.read_text().replace(
        'make INSTALL=${INSTALL:-install} INSTALL_TOP="$prefix_dir" TO_BIN=/dev/null install',
        'mkdir -p "$prefix_dir/include" "$prefix_dir/lib"\n'
        'install -m 0644 src/lua.h src/luaconf.h src/lualib.h src/lauxlib.h src/lua.hpp "$prefix_dir/include/"\n'
        'install -m 0644 src/liblua.a "$prefix_dir/lib/"'))
    for item in (work / 'scripts').glob('*.sh'):
        item.chmod(0o755)


def install(work, abi, ndk, revision, source_hash, disable_x86_asm=False):
    prefix = work / 'prefix' / ARCHES[abi]
    libraries = list((prefix / 'lib').glob('*.so'))
    required = ['mpv', 'avcodec', 'avformat', 'avutil', 'avfilter', 'swresample', 'swscale']
    for name in required:
        if not (prefix / 'lib' / ('lib' + name + '.so')).is_file():
            raise RuntimeError('Missing built library: ' + name)
    # Confirm every DT_NEEDED dependency will be provided by the AAR or Android.
    toolchain = next((ndk / 'toolchains/llvm/prebuilt').iterdir())
    readelf = toolchain / 'bin/llvm-readelf'
    provided = {p.name for p in libraries} | {'libc++_shared.so'}
    system = {'libc.so', 'libm.so', 'libdl.so', 'liblog.so', 'libandroid.so', 'libz.so',
              'libEGL.so', 'libGLESv2.so', 'libGLESv3.so', 'libOpenSLES.so', 'libmediandk.so',
              'libjnigraphics.so', 'libvulkan.so', 'libaaudio.so'}
    for library in libraries:
        dynamic = subprocess.check_output([str(readelf), '-d', str(library)], text=True)
        soname = re.search(r'\(SONAME\).*\[(.*?)\]', dynamic)
        if soname and soname.group(1) != library.name:
            raise RuntimeError(f'{library.name} has an APK-incompatible SONAME: {soname.group(1)}')
        missing = set(re.findall(r'\(NEEDED\).*\[(.*?)\]', dynamic)) - provided - system
        if missing:
            raise RuntimeError(f'{library.name} has unpackaged dependencies: {missing}')
    sources = {}
    for path in (work / 'deps').iterdir():
        if (path / '.git').exists():
            sources[path.name] = subprocess.check_output(['git', '-C', str(path), 'rev-parse', 'HEAD'], text=True).strip()
        elif (path / '.download.json').exists():
            sources[path.name] = json.loads((path / '.download.json').read_text())
        elif (path / '.local-source.json').exists():
            sources[path.name] = json.loads((path / '.local-source.json').read_text())
    manifest = {'mpv': '0.41.0', 'ffmpeg': FFMPEG_TAG, 'abi': abi, 'api': 23,
                'disable_x86_asm': disable_x86_asm,
                'ndk': revision, 'mpv_source_sha256': source_hash, 'sources': sources,
                'libraries': {p.name: digest(p) for p in libraries}}
    target = MODULE / 'src/main/cpp/ffmpeg' / abi / 'mpv'
    target.parent.mkdir(parents=True, exist_ok=True)
    with tempfile.TemporaryDirectory(prefix='mpv-install-', dir=target.parent) as stage:
        stage = Path(stage)
        shutil.copytree(prefix / 'include', stage / 'include')
        (stage / 'lib').mkdir()
        for library in libraries:
            shutil.copy2(library, stage / 'lib' / library.name)
        (stage / 'manifest.json').write_text(json.dumps(manifest, indent=2) + '\n')
        if target.exists():
            shutil.rmtree(target)
        shutil.move(str(stage), str(target))
    runtime = MODULE / 'src/main/jniLibs' / abi
    runtime.mkdir(parents=True, exist_ok=True)
    old_manifest = runtime / 'mpv-runtime.json'
    if old_manifest.exists():
        for name in json.loads(old_manifest.read_text()):
            (runtime / name).unlink(missing_ok=True)
    names = []
    for library in libraries:
        # CMake packages these imports and c++_shared automatically; avoid duplicate entries.
        if library.name not in {'libmpv.so', 'libavcodec.so', 'libc++_shared.so'}:
            shutil.copy2(library, runtime / library.name)
            names.append(library.name)
    old_manifest.write_text(json.dumps(names, indent=2) + '\n')
    print('Installed ' + str(target), flush=True)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--abi', choices=list(ARCHES) + ['all'], default='arm64-v8a')
    parser.add_argument('--ndk', type=Path, default=os.environ.get('ANDROID_NDK_ROOT'))
    parser.add_argument('--ffmpeg-source', type=Path,
                        default=LOCAL_FFMPEG if LOCAL_FFMPEG.is_dir() else None,
                        help='Use a local FFmpeg 7.1.1 source tree instead of cloning GitHub')
    mode = parser.add_mutually_exclusive_group()
    mode.add_argument('--prepare-local-sources', action='store_true',
                        help='Stage local sources and report missing submodules, without downloading or compiling')
    mode.add_argument('--download-only', action='store_true',
                      help='Stage local sources and download missing dependencies without requiring build tools')
    parser.add_argument('--libass-source', type=Path,
                        default=LOCAL_LIBASS if LOCAL_LIBASS.is_dir() else None)
    parser.add_argument('--libplacebo-source', type=Path,
                        default=LOCAL_LIBPLACEBO if LOCAL_LIBPLACEBO.is_dir() else None)
    parser.add_argument('--offline', action='store_true', help='Use sources already downloaded in build/native/deps')
    parser.add_argument('--disable-x86-asm', action='store_true',
                        help='Build x86/x86_64 without NASM optimizations; slower software decoding')
    mode.add_argument('--check', action='store_true', help='Check tools only; do not download or build')
    args = parser.parse_args()
    if args.download_only and args.offline:
        parser.error('--download-only cannot be combined with --offline')
    if args.ndk is None:
        parser.error('--ndk or ANDROID_NDK_ROOT is required')
    ndk = args.ndk.expanduser().resolve()
    props = ndk / 'source.properties'
    if not props.exists():
        parser.error('Not an Android NDK: ' + str(ndk))
    revision = re.search(r'Pkg.Revision\s*=\s*(\S+)', props.read_text()).group(1)
    local_ffmpeg = validate_ffmpeg_source(args.ffmpeg_source) if args.ffmpeg_source else None
    if local_ffmpeg:
        print('FFmpeg source verified: ' + str(local_ffmpeg), flush=True)
    local_libraries = {}
    for name, source, version in [('libass', args.libass_source, '0.17.4'),
                                  ('libplacebo', args.libplacebo_source, 'v7.351.0')]:
        if source:
            source = validate_local_library(name, source)
            local_libraries[name] = (source, version, source_digest(source))
            print(f'{name} source version verified: {source}', flush=True)
    missing_submodules = (missing_placebo_submodules(local_libraries['libplacebo'][0])
                          if 'libplacebo' in local_libraries else [])
    if missing_submodules:
        message = 'libplacebo is missing vendored submodules: ' + ', '.join(missing_submodules)
        if not args.prepare_local_sources:
            raise RuntimeError(message + '. Obtain the v7.351.0 source with --recurse-submodules.')
        print('WARNING: ' + message + '. Staging only; source is not ready to build.', flush=True)
    # Reuse project-local Python tools and Ninja shipped with the Android SDK.
    tool_paths = [MODULE / 'build/host-tools/bin', ndk.parent.parent / 'cmake/3.22.1/bin']
    os.environ['PATH'] = os.pathsep.join([str(p) for p in tool_paths if p.is_dir()] +
                                       [os.environ.get('PATH', '')])
    tools = ['git', 'curl', 'make', 'meson', 'ninja', 'pkg-config']
    if args.abi in {'x86', 'x86_64', 'all'} and not args.disable_x86_asm:
        tools.append('nasm')
    missing = [name for name in tools if shutil.which(name) is None]
    if missing and not (args.prepare_local_sources or args.download_only):
        raise RuntimeError('Missing build tools: ' + ', '.join(missing))
    if args.download_only:
        missing_download_tools = [name for name in ['git', 'curl'] if shutil.which(name) is None]
        if missing_download_tools:
            raise RuntimeError('Missing download tools: ' + ', '.join(missing_download_tools))
    if args.check:
        print('Tool checks passed; NDK ' + revision)
        return
    work = MODULE / 'build/native'
    work.mkdir(parents=True, exist_ok=True)
    # Prevent accidentally reusing objects produced with a different NDK or mpv source.
    source_hash = source_digest(MPV)
    ffmpeg_hash = source_digest(local_ffmpeg) if local_ffmpeg else None
    fingerprint = {'ndk': str(ndk), 'revision': revision, 'mpv': source_hash, 'ffmpeg': FFMPEG_TAG,
                   'local_ffmpeg': str(local_ffmpeg) if local_ffmpeg else None,
                   'local_ffmpeg_sha256': ffmpeg_hash,
                   'recipe': digest(Path(__file__)), 'reference': source_digest(REFERENCE)}
    fingerprint['local_libraries'] = {
        name: {'path': str(source), 'version': version, 'sha256': source_hash}
        for name, (source, version, source_hash) in local_libraries.items()}
    marker = work / 'build-config.json'
    if marker.exists() and json.loads(marker.read_text()) != fingerprint:
        previous = json.loads(marker.read_text())
        def source_identity(config):
            # A byte-identical source copy may change location without changing inputs.
            result = {k: v for k, v in config.items() if k not in {'recipe', 'local_ffmpeg', 'local_libraries'}}
            result['local_libraries'] = {name: {k: v for k, v in info.items() if k != 'path'}
                                         for name, info in config.get('local_libraries', {}).items()}
            return result
        same_sources = source_identity(previous) == source_identity(fingerprint)
        if not same_sources:
            raise RuntimeError('Native sources changed. Remove module/build/native and rebuild.')
        # Recipe changes are handled per ABI below, keeping downloaded sources.
    marker.write_text(json.dumps(fingerprint, indent=2))
    setup(work, ndk, revision)
    (work / 'deps').mkdir(exist_ok=True)
    if not (work / 'deps/mpv').exists():
        shutil.copytree(MPV, work / 'deps/mpv')
    if local_ffmpeg:
        stage_local_source(work, 'ffmpeg', local_ffmpeg, FFMPEG_TAG, ffmpeg_hash)
    for name, (source, version, library_hash) in local_libraries.items():
        stage_local_source(work, name, source, version, library_hash)
    required_sources = ['mpv', 'ffmpeg', 'mbedtls', 'dav1d', 'libxml2', 'freetype2',
                        'fontconfig', 'fribidi', 'harfbuzz', 'unibreak', 'libass',
                        'lua', 'libplacebo', 'curl']
    absent = [name for name in required_sources if not (work / 'deps' / name).is_dir()]
    placebo = work / 'deps/libplacebo'
    missing_submodules = missing_placebo_submodules(placebo) if placebo.is_dir() else []
    def write_report():
        absent = [name for name in required_sources if not (work / 'deps' / name).is_dir()]
        submodules = missing_placebo_submodules(placebo) if placebo.is_dir() else []
        report = {'local_sources': [str(MPV)] + ([str(local_ffmpeg)] if local_ffmpeg else []) +
                  [str(item[0]) for item in local_libraries.values()],
                  'missing_sources': absent, 'missing_libplacebo_submodules': submodules,
                  'missing_tools_on_path': missing}
        report_path = work / 'remaining-dependencies.json'
        report_path.write_text(json.dumps(report, indent=2) + '\n')
        return report_path

    report_path = write_report()
    if args.prepare_local_sources:
        print('Local sources staged. No dependencies downloaded and no libraries built.\n'
              'Prerequisite report: ' + str(report_path))
        return
    if args.offline:
        if absent:
            raise RuntimeError('Offline build is missing dependency sources: ' + ', '.join(absent))
    if not args.offline:
        try:
            download(work)
        finally:
            write_report()
    missing_submodules = missing_placebo_submodules(placebo)
    if missing_submodules:
        raise RuntimeError('Staged libplacebo is missing submodules: ' + ', '.join(missing_submodules))
    if args.download_only:
        print('All dependency sources are ready. No libraries built.\n'
              'Prerequisite report: ' + str(report_path))
        return
    env = os.environ.copy()
    env.setdefault('cores', str(min(os.cpu_count() or 4, 8)))
    for abi in ARCHES if args.abi == 'all' else [args.abi]:
        portable = args.disable_x86_asm and abi in {'x86', 'x86_64'}
        abi_config = dict(fingerprint, disable_x86_asm=portable, build_root=str(work))
        abi_marker = work / ('build-config-' + abi + '.json')
        if not abi_marker.exists() or json.loads(abi_marker.read_text()) != abi_config:
            # Invalidate only this ABI's objects, including caches after module moves.
            for dependency in (work / 'deps').iterdir():
                cache = dependency / ('_build_' + ARCHES[abi])
                if cache.is_dir():
                    shutil.rmtree(cache)
            prefix = work / 'prefix' / ARCHES[abi]
            if prefix.exists():
                shutil.rmtree(prefix)
            abi_marker.write_text(json.dumps(abi_config, indent=2) + '\n')
        setup(work, ndk, revision, disable_x86_asm=portable)
        run(['bash', 'buildall.sh', '--arch', ARCHES[abi], 'mpv'], cwd=work, env=env)
        install(work, abi, ndk, revision, source_hash, disable_x86_asm=portable)


if __name__ == '__main__':
    try:
        main()
    except (RuntimeError, subprocess.CalledProcessError) as error:
        print('Build failed: ' + str(error), file=sys.stderr)
        sys.exit(1)
