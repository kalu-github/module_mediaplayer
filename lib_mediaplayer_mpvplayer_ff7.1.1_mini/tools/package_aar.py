#!/usr/bin/env python3
"""Compile and package this resource-free Java/JNI module without a Gradle daemon.

Requires real dependencies installed by build_native.py. Does not verify playback.
AAR layout: https://developer.android.com/studio/projects/android-library#aar-contents
"""
import argparse
import io
import json
from pathlib import Path
import re
import shutil
import subprocess
import tempfile
import xml.etree.ElementTree as ET
import zipfile

from build_native import MODULE, ROOT, digest

SYSTEM_LIBS = {'libc.so', 'libm.so', 'libdl.so', 'liblog.so', 'libandroid.so', 'libz.so',
               'libEGL.so', 'libGLESv2.so', 'libGLESv3.so', 'libOpenSLES.so',
               'libmediandk.so', 'libjnigraphics.so', 'libvulkan.so', 'libaaudio.so'}
TRIPLES = {'arm64-v8a': 'aarch64-linux-android', 'armeabi-v7a': 'arm-linux-androideabi',
           'x86': 'i686-linux-android', 'x86_64': 'x86_64-linux-android'}
MACHINES = {'arm64-v8a': 'AArch64', 'armeabi-v7a': 'ARM',
            'x86': 'Intel 80386', 'x86_64': 'Advanced Micro Devices X86-64'}


def run(args):
    print('+ ' + ' '.join(map(str, args)), flush=True)
    subprocess.run(list(map(str, args)), check=True)


def elf_report(libraries, toolchain, abi):
    result = {}
    for lib in libraries:
        output = subprocess.check_output([str(toolchain / 'bin/llvm-readelf'),
                                          '-h', '-l', '-d', '-W', str(lib)], text=True)
        machine = re.search(r'Machine:\s*(.*)', output).group(1).strip()
        if machine != MACHINES[abi]:
            raise RuntimeError(f'Wrong architecture: {lib}: {machine}')
        soname = re.search(r'\(SONAME\).*\[(.*?)\]', output)
        if not soname or soname.group(1) != lib.name:
            raise RuntimeError('Invalid SONAME: ' + str(lib))
        needed = re.findall(r'\(NEEDED\).*\[(.*?)\]', output)
        missing = set(needed) - {p.name for p in libraries} - SYSTEM_LIBS
        if missing:
            raise RuntimeError(f'{lib.name} has missing dependencies: {missing}')
        aligns = [int(line.split()[-1], 16) for line in output.splitlines()
                  if line.strip().startswith('LOAD ')]
        if not aligns or min(aligns) < 16384:
            raise RuntimeError('Library does not support 16 KB pages: ' + str(lib))
        result[lib.name] = {'sha256': digest(lib), 'bytes': lib.stat().st_size,
                            'needed': needed, 'machine': machine, 'load_alignment': aligns}
    return result


def verify_exports(deps, libs, toolchain, java_source):
    def symbols(name):
        output = subprocess.check_output([str(toolchain / 'bin/llvm-nm'), '-D',
            '--defined-only', '--format=posix', str(libs / name)], text=True)
        return {line.split()[0].split('@')[0] for line in output.splitlines()
                if len(line.split()) >= 2 and line.split()[1] != 'A'}
    ffmpeg = set(re.findall(r'--undefined=(\w+)', (deps / 'ffmpeg-roots.rsp').read_text()))
    mpv = set()
    for header in (deps / 'include/mpv').glob('*.h'):
        mpv.update(re.findall(r'^MPV_EXPORT\s+[^;{]*?\b(mpv_\w+)\s*\(', header.read_text(), re.M))
    jni = {'JNI_OnLoad'} | {'Java_lib_kalu_mpvplayer_MpvPlayer_' + name for name in
           re.findall(r'\bnative\s+\S+\s+(\w+)\(', java_source.read_text())}
    if not ffmpeg or not mpv or len(jni) < 2:
        raise RuntimeError('Cannot determine the required public API')
    for name, expected in [('libffmpeg.so', ffmpeg), ('libmpvplayer.so', mpv | jni)]:
        missing = expected - symbols(name)
        if missing:
            raise RuntimeError(name + ' is missing exports: ' + ', '.join(sorted(missing)))
    return {'ffmpeg': len(ffmpeg), 'mpv': len(mpv), 'jni': len(jni) - 1}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--sdk', type=Path, required=True)
    parser.add_argument('--ndk', type=Path, required=True)
    parser.add_argument('--java-home', type=Path, required=True)
    parser.add_argument('--abi', choices=list(TRIPLES), default='arm64-v8a')
    parser.add_argument('--layout', choices=['merged', 'split'], default='merged')
    args = parser.parse_args()
    main_source = MODULE / 'src/main'
    for folder in ['res', 'assets', 'aidl', 'kotlin']:
        if (main_source / folder).exists() and any(p.is_file() for p in (main_source / folder).rglob('*')):
            raise RuntimeError(f'This packaging path does not handle {folder}; use Gradle.')
    merged = args.layout == 'merged'
    deps = main_source / 'cpp/ffmpeg' / args.abi
    if not merged:
        deps /= 'mpv'
    manifest = json.loads((deps / 'manifest.json').read_text())
    if merged and manifest.get('layout') != 'static-inputs':
        raise RuntimeError('Reinstall native dependencies in the include/lib static layout first.')
    for name, expected in manifest.get('link_files', {}).items():
        if digest(deps / name) != expected:
            raise RuntimeError('Link configuration differs from its manifest: ' + name)
    for name, expected in manifest['libraries'].items():
        if digest(deps / 'lib' / name) != expected:
            raise RuntimeError('Dependency differs from its build manifest: ' + name)
    for name, expected in manifest.get('archives', {}).items():
        if digest(deps / 'lib' / name) != expected:
            raise RuntimeError('Static archive differs from its build manifest: ' + name)
    work = MODULE / ('build/aar-merged' if merged else 'build/aar-direct') / args.abi
    work.mkdir(parents=True, exist_ok=True)
    cmake = args.sdk / 'cmake/3.22.1/bin/cmake'
    run([cmake, '-S', main_source / 'cpp', '-B', work / 'cmake', '-G', 'Ninja',
         '-DCMAKE_MAKE_PROGRAM=' + str(cmake.parent / 'ninja'),
         '-DCMAKE_TOOLCHAIN_FILE=' + str(args.ndk / 'build/cmake/android.toolchain.cmake'),
         '-DANDROID_ABI=' + args.abi, '-DANDROID_PLATFORM=android-23',
         '-DMPV_LAYOUT=' + args.layout,
         '-DANDROID_STL=' + ('c++_static' if merged else 'c++_shared'), '-DCMAKE_BUILD_TYPE=Release'])
    run([cmake, '--build', work / 'cmake', '--parallel', '8'])
    toolchain = next((args.ndk / 'toolchains/llvm/prebuilt').iterdir())
    with tempfile.TemporaryDirectory(prefix='package-', dir=work) as temporary:
        stage = Path(temporary)
        classes = stage / 'classes'
        classes.mkdir()
        run([args.java_home / 'bin/javac', '--release', '8', '-encoding', 'UTF-8',
             '-cp', args.sdk / 'platforms/android-32/android.jar', '-d', classes] +
            sorted((main_source / 'java').rglob('*.java')))
        jar = stage / 'classes.jar'
        with zipfile.ZipFile(jar, 'w', zipfile.ZIP_DEFLATED) as z:
            for p in sorted(classes.rglob('*.class')):
                z.write(p, p.relative_to(classes).as_posix())
        libs = stage / 'jni' / args.abi
        libs.mkdir(parents=True)
        if merged:
            shutil.copy2(work / 'cmake/libffmpeg.so', libs / 'libffmpeg.so')
        else:
            for p in sorted((deps / 'lib').glob('*.so')):
                shutil.copy2(p, libs / p.name)
        shutil.copy2(work / 'cmake/libmpvplayer.so', libs / 'libmpvplayer.so')
        if not merged:
            shutil.copy2(toolchain / 'sysroot/usr/lib' / TRIPLES[args.abi] / 'libc++_shared.so',
                         libs / 'libc++_shared.so')
        elif {p.name for p in libs.glob('*.so')} != {'libffmpeg.so', 'libmpvplayer.so'}:
            raise RuntimeError('Merged AAR must contain exactly two native libraries.')
        for p in libs.glob('*.so'):
            run([toolchain / 'bin/llvm-strip', '--strip-unneeded', p])
        report = elf_report(list(libs.glob('*.so')), toolchain, args.abi)
        api_report = verify_exports(deps, libs, toolchain,
            main_source / 'java/lib/kalu/mpvplayer/MpvPlayer.java') if merged else None
        android_ns = 'http://schemas.android.com/apk/res/android'
        ET.register_namespace('android', android_ns)
        xml = ET.parse(main_source / 'AndroidManifest.xml')
        xml.getroot().set('package', 'lib.kalu.mpvplayer')
        ET.SubElement(xml.getroot(), 'uses-sdk', {
            '{' + android_ns + '}minSdkVersion': '23',
            '{' + android_ns + '}targetSdkVersion': '32'})
        xml.write(stage / 'AndroidManifest.xml', encoding='utf-8', xml_declaration=True)
        shutil.copy2(MODULE / 'consumer-rules.pro', stage / 'proguard.txt')
        (stage / 'R.txt').write_text('')
        (stage / 'public.txt').write_text('')
        metadata = stage / 'META-INF/com/android/build/gradle/aar-metadata.properties'
        metadata.parent.mkdir(parents=True)
        metadata.write_text('aarFormatVersion=1.0\naarMetadataVersion=1.0\nminCompileSdk=32\n')
        info = stage / 'META-INF/mpv-build.json'
        info.write_text(json.dumps({'packager': 'CMake+javac', 'dependency_build': manifest,
                                   'libraries': report, 'public_exports': api_report,
                                   'device_playback_verified': False}, indent=2) + '\n')
        output = MODULE / 'build/outputs/aar'
        output.mkdir(parents=True, exist_ok=True)
        suffix = '_merged' if merged else ''
        aar = output / f'lib_mediaplayer_mpv_0.41.0_{args.abi}{suffix}_release.aar'
        pending = aar.with_suffix('.aar.partial')
        with zipfile.ZipFile(pending, 'w', zipfile.ZIP_DEFLATED) as z:
            for p in sorted(stage.rglob('*')):
                if p.is_file() and classes not in p.parents:
                    z.write(p, p.relative_to(stage).as_posix())
        with zipfile.ZipFile(pending) as z:
            if z.testzip() is not None:
                raise RuntimeError('Corrupt AAR')
            with zipfile.ZipFile(io.BytesIO(z.read('classes.jar'))) as j:
                if 'lib/kalu/mpvplayer/MpvPlayer.class' not in j.namelist():
                    raise RuntimeError('Player class missing from AAR')
        pending.replace(aar)
        if merged:
            native_output = MODULE / 'build/outputs/merged-jni' / args.abi
            native_output.mkdir(parents=True, exist_ok=True)
            for library in libs.glob('*.so'):
                shutil.copy2(library, native_output / library.name)
        (aar.with_suffix('.aar.sha256')).write_text(digest(aar) + '  ' + aar.name + '\n')
        (work / 'verification.json').write_text(json.dumps(report, indent=2) + '\n')
        aar.with_suffix('.verification.json').write_text(json.dumps({
            'aar': str(aar), 'sha256': digest(aar), 'bytes': aar.stat().st_size,
            'abi': args.abi, 'libraries': report, 'public_exports': api_report,
            'device_playback_verified': False}, indent=2) + '\n')
        print('AAR created: ' + str(aar), flush=True)
        print('ELF dependencies and 16 KB alignment verified. Device playback has not been verified.')


if __name__ == '__main__':
    main()
