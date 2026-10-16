#!/usr/bin/env python3
"""Compile Java and Android JNI objects and run the native file-range regression test.
Does not build/link libmpv or claim to verify actual playback.
"""
import argparse
import ast
import os
from pathlib import Path
import platform
import subprocess

from build_native import LOCAL_FFMPEG, MPV

MODULE = Path(__file__).resolve().parents[1]
ROOT = MODULE.parent
TRIPLES = {'arm64-v8a': 'aarch64-linux-android', 'armeabi-v7a': 'armv7a-linux-androideabi',
           'x86': 'i686-linux-android', 'x86_64': 'x86_64-linux-android'}


def run(args):
    subprocess.run(list(map(str, args)), check=True)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--sdk', type=Path, required=True)
    parser.add_argument('--ndk', type=Path, required=True)
    parser.add_argument('--java-home', type=Path, required=True)
    args = parser.parse_args()
    output = MODULE / 'build/verify'
    output.mkdir(parents=True, exist_ok=True)
    javac = args.java_home / 'bin/javac'
    android = args.sdk / 'platforms/android-32/android.jar'
    java_sources = sorted((MODULE / 'src/main/java').rglob('*.java'))
    run([javac, '--release', '8', '-Xlint:unchecked', '-Xlint:-options', '-cp', android,
         '-d', output / 'classes'] + java_sources)
    print('Java compilation passed', flush=True)
    optional = args.sdk / 'platforms/android-32/optional'
    test_classpath = os.pathsep.join(str(p) for p in [android, output / 'classes',
        optional / 'android.test.runner.jar', optional / 'android.test.base.jar'])
    run([javac, '--release', '8', '-Xlint:-options', '-cp', test_classpath,
         '-d', output / 'androidTest'] + sorted((MODULE / 'src/androidTest/java').rglob('*.java')))
    print('Device test sources compiled (not executed)', flush=True)
    toolchain = next((args.ndk / 'toolchains/llvm/prebuilt').iterdir())
    for abi, triple in TRIPLES.items():
        dependencies = MODULE / 'src/main/cpp/ffmpeg' / abi
        includes = dependencies / 'include'
        if not includes.exists():
            includes = LOCAL_FFMPEG
        if not (includes / 'libavcodec/jni.h').is_file():
            raise RuntimeError('FFmpeg 7.1.1 headers are missing: ' + str(includes))
        run([toolchain / 'bin' / (triple + '23-clang++'), '-std=c++17', '-Wall', '-Wextra', '-Werror',
             # On case-insensitive macOS, FFmpeg's VERSION would shadow C++ <version>
             # if the source root were placed before the standard include paths.
             '-D_FILE_OFFSET_BITS=64', '-I' + str(MPV / 'include'), '-idirafter', includes,
             '-c', MODULE / 'src/main/cpp/mpv/mpv_jni.cpp', '-o', output / (abi + '.o')])
        print(abi + ' JNI object compilation passed', flush=True)
    host = [os.environ.get('CXX', 'clang++'), '-std=c++17', '-Wall', '-Wextra', '-Werror',
            '-fsanitize=address,undefined', '-I' + str(MODULE / 'src/main/cpp/mpv')]
    if platform.system() == 'Darwin':
        sdk = Path(subprocess.check_output(['xcrun', '--show-sdk-path'], text=True).strip())
        host += ['-isysroot', str(sdk), '-isystem', str(sdk / 'usr/include/c++/v1')]
    run(host + [MODULE / 'tests/file_slice_test.cpp', '-o', output / 'file_slice_test'])
    run([output / 'file_slice_test'])
    ast.parse((MODULE / 'tools/build_native.py').read_text())
    print('Local checks passed. Full dependency linking and device playback still require Android builds.', flush=True)


if __name__ == '__main__':
    main()
