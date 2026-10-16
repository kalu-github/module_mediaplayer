#!/usr/bin/env python3
"""Offline FFmpeg bootstrap build for Android; no libmpv or AAR is produced.

Only built-in codecs and Android system libraries are enabled. HTTPS (mbedTLS),
software AV1 via dav1d and DASH via libxml2 require the complete dependency build.
Outputs stay in build/ffmpeg-core and cannot be mistaken for production mpv libs.
"""
import argparse
import json
import os
from pathlib import Path
import shutil
import subprocess
import sys

from build_native import MODULE, LOCAL_FFMPEG, FFMPEG_TAG, digest, source_digest, validate_ffmpeg_source

TARGETS = {
    'arm64-v8a': ('aarch64-linux-android', 'aarch64', 'armv8-a'),
    'armeabi-v7a': ('armv7a-linux-androideabi', 'arm', 'armv7-a'),
    'x86': ('i686-linux-android', 'x86', 'i686'),
    'x86_64': ('x86_64-linux-android', 'x86_64', 'generic'),
}


def logged(command, directory, log, env):
    with log.open('a') as output:
        output.write('+ ' + ' '.join(map(str, command)) + '\n')
        output.flush()
        result = subprocess.run(list(map(str, command)), cwd=directory, env=env,
                                stdout=output, stderr=subprocess.STDOUT)
    if result.returncode:
        print('\n'.join(log.read_text(errors='replace').splitlines()[-45:]), file=sys.stderr)
        raise RuntimeError('FFmpeg build failed; see ' + str(log))


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--source', type=Path, default=LOCAL_FFMPEG)
    parser.add_argument('--ndk', type=Path, required=True)
    parser.add_argument('--abi', choices=list(TARGETS) + ['all'], default='arm64-v8a')
    parser.add_argument('--jobs', type=int, default=min(os.cpu_count() or 4, 8))
    args = parser.parse_args()
    if args.jobs < 1:
        parser.error('--jobs must be positive')
    source = validate_ffmpeg_source(args.source)
    ndk = args.ndk.expanduser().resolve()
    prebuilt = ndk / 'toolchains/llvm/prebuilt'
    if not prebuilt.is_dir() or shutil.which('make') is None:
        parser.error('A complete Android NDK and make are required')
    toolchain = next(prebuilt.iterdir())
    toolbin = toolchain / 'bin'
    env = os.environ.copy()
    for name in ('CC', 'CXX', 'CFLAGS', 'CPPFLAGS', 'CXXFLAGS', 'LDFLAGS',
                 'CPATH', 'LIBRARY_PATH', 'C_INCLUDE_PATH', 'CPLUS_INCLUDE_PATH',
                 'PKG_CONFIG_PATH', 'PKG_CONFIG_LIBDIR', 'PKG_CONFIG_SYSROOT_DIR'):
        env.pop(name, None)
    env['PATH'] = str(toolbin) + os.pathsep + env.get('PATH', '')
    source_hash = source_digest(source)
    for abi in TARGETS if args.abi == 'all' else [args.abi]:
        triple, arch, cpu = TARGETS[abi]
        build = MODULE / 'build/ffmpeg-core' / abi
        objects, prefix = build / 'objects', build / 'prefix'
        objects.mkdir(parents=True, exist_ok=True)
        log = build / 'build.log'
        configuration = {
            'source': str(source), 'source_sha256': source_hash, 'ffmpeg': FFMPEG_TAG,
            'ndk': str(ndk), 'ndk_sha256': digest(ndk / 'source.properties'),
            'abi': abi, 'api': 23, 'recipe_sha256': digest(Path(__file__)),
            'profile': 'offline-bootstrap-without-external-libraries',
        }
        marker = build / 'config.json'
        if marker.exists() and json.loads(marker.read_text()) != configuration:
            raise RuntimeError('Configuration changed; clean ' + str(build) + ' before rebuilding')
        marker.write_text(json.dumps(configuration, indent=2) + '\n')
        flags = '-fPIC' + (' -march=armv7-a -mfpu=neon' if arch == 'arm' else '')
        configure = [source / 'configure', '--prefix=' + str(prefix),
            '--target-os=android', '--enable-cross-compile', '--arch=' + arch, '--cpu=' + cpu,
            '--cc=' + str(toolbin / (triple + '23-clang')),
            '--cxx=' + str(toolbin / (triple + '23-clang++')),
            '--ar=' + str(toolbin / 'llvm-ar'), '--ranlib=' + str(toolbin / 'llvm-ranlib'),
            '--nm=' + str(toolbin / 'llvm-nm'), '--strip=' + str(toolbin / 'llvm-strip'),
            '--pkg-config=false', '--extra-cflags=' + flags,
            '--extra-ldflags=-Wl,-z,max-page-size=16384',
            '--disable-autodetect', '--enable-zlib', '--enable-shared', '--disable-static',
            '--enable-pic', '--enable-jni', '--enable-mediacodec', '--enable-gpl', '--enable-version3',
            '--disable-programs', '--disable-doc', '--disable-debug', '--disable-avdevice',
            '--disable-postproc', '--disable-devices', '--disable-encoders', '--disable-muxers',
            '--enable-encoder=mjpeg,png', '--enable-muxer=mov,matroska,mpegts']
        if arch in ('x86', 'x86_64'):
            configure.append('--disable-x86asm')  # The offline bootstrap does not require NASM.
        if not (objects / 'ffbuild/config.mak').exists():
            print('Configuring FFmpeg ' + abi + '; log: ' + str(log), flush=True)
            logged(configure, objects, log, env)
        print('Compiling FFmpeg ' + abi, flush=True)
        logged(['make', '-j' + str(args.jobs)], objects, log, env)
        logged(['make', 'install'], objects, log, env)
        libs = ['avcodec', 'avformat', 'avfilter', 'avutil', 'swresample', 'swscale']
        outputs = {}
        for library in libs:
            file = prefix / 'lib' / ('lib' + library + '.so')
            if not file.is_file():
                raise RuntimeError('Missing output ' + str(file))
            outputs[file.name] = digest(file)
        (prefix / 'bootstrap-manifest.json').write_text(json.dumps(
            dict(configuration, libraries=outputs), indent=2) + '\n')
        print('Built ' + abi + ': ' + str(prefix), flush=True)
    print('FFmpeg bootstrap complete. libmpv dependencies and a full AAR are still required.', flush=True)


if __name__ == '__main__':
    try:
        main()
    except (RuntimeError, subprocess.CalledProcessError) as error:
        print(str(error), file=sys.stderr)
        sys.exit(1)
