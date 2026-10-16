#!/usr/bin/env python3
"""Build a real AAR playback test APK using installed SDK tools, optionally run it via ADB.

Building the APK alone does not verify playback. Runtime checks require an Android
device with a display, GPU, AudioTrack/Visualizer support and public internet access.
"""
import argparse
import hashlib
import json
import os
from pathlib import Path
import shutil
import subprocess
import zipfile

MODULE = Path(__file__).resolve().parents[1]
PACKAGE = 'lib.kalu.mpvplayer.smoke'


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--aar', type=Path, required=True)
    parser.add_argument('--sdk', type=Path, required=True)
    parser.add_argument('--java-home', type=Path, required=True)
    parser.add_argument('--run', action='store_true')
    parser.add_argument('--serial', help='ADB serial, required if multiple devices are connected')
    args = parser.parse_args()
    output = MODULE / 'build/playback-test'
    output.mkdir(parents=True, exist_ok=True)
    source = MODULE / 'tests/playback'
    android = args.sdk / 'platforms/android-32/android.jar'
    optional = args.sdk / 'platforms/android-32/optional'
    build_tools = args.sdk / 'build-tools/36.0.0'
    env = dict(os.environ, JAVA_HOME=str(args.java_home))

    def run(command):
        subprocess.run(list(map(str, command)), env=env, check=True)

    classes = output / 'classes'
    dex = output / 'dex'
    for directory in [classes, dex]:
        if directory.exists():
            shutil.rmtree(directory)
        directory.mkdir()
    aar_classes = output / 'aar-classes.jar'
    with zipfile.ZipFile(args.aar) as aar:
        aar_classes.write_bytes(aar.read('classes.jar'))
    libraries = [android, optional / 'android.test.runner.jar', optional / 'android.test.base.jar']
    run([args.java_home / 'bin/javac', '--release', '8', '-encoding', 'UTF-8',
         '-cp', os.pathsep.join(map(str, [aar_classes] + libraries)), '-d', classes]
        + sorted(source.glob('*.java')))
    test_jar = output / 'test-classes.jar'
    with zipfile.ZipFile(test_jar, 'w') as jar:
        for path in sorted(classes.rglob('*.class')):
            jar.write(path, path.relative_to(classes).as_posix())
    command = [build_tools / 'd8', '--min-api', '23', '--output', dex]
    for library in libraries:
        command += ['--lib', library]
    run(command + [aar_classes, test_jar])
    unsigned = output / 'unsigned.apk'
    run([build_tools / 'aapt2', 'link', '-o', unsigned, '-I', android,
         '--manifest', source / 'AndroidManifest.xml'])
    with zipfile.ZipFile(unsigned, 'a', zipfile.ZIP_DEFLATED) as apk, zipfile.ZipFile(args.aar) as aar:
        for path in sorted(dex.glob('*.dex')):
            apk.write(path, path.name)
        for name in aar.namelist():
            if name.startswith('jni/') and name.endswith('.so'):
                apk.writestr('lib/' + name[len('jni/'):], aar.read(name))
    aligned = output / 'aligned.apk'
    run([build_tools / 'zipalign', '-f', '-P', '16', '4', unsigned, aligned])
    key = output / 'test-only.keystore'
    if not key.exists():
        run([args.java_home / 'bin/keytool', '-genkeypair', '-keystore', key,
             '-storepass', 'android', '-keypass', 'android', '-alias', 'androiddebugkey',
             '-dname', 'CN=MPV Local Test', '-keyalg', 'RSA', '-validity', '3650'])
    apk = output / 'mpv-public-mp4-test.apk'
    run([build_tools / 'apksigner', 'sign', '--ks', key, '--ks-pass', 'pass:android',
         '--key-pass', 'pass:android', '--out', apk, aligned])
    run([build_tools / 'apksigner', 'verify', apk])
    report = {
        'aar': str(args.aar.resolve()), 'aar_sha256': hashlib.sha256(args.aar.read_bytes()).hexdigest(),
        'apk': str(apk), 'apk_sha256': hashlib.sha256(apk.read_bytes()).hexdigest(),
        'url': 'https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4',
        'device_tests_executed': False, 'callbacks_pixels_audio_pcm_verified': False,
        'speaker_audibility_verified': False,
    }
    report_path = output / 'result.json'
    report_path.write_text(json.dumps(report, indent=2) + '\n')
    print('Built: ' + str(apk), flush=True)
    if not args.run:
        print('Device playback NOT tested. Use --run when ADB is accessible.')
        return
    adb = [str(args.sdk / 'platform-tools/adb')]
    if args.serial:
        adb += ['-s', args.serial]
    try:
        run(adb + ['get-state'])
        run(adb + ['install', '-r', '-g', str(apk)])
        result = subprocess.run(adb + ['shell', 'am', 'instrument', '-w', '-r', '-e', 'class',
            PACKAGE + '.PublicMp4Test', PACKAGE + '/android.test.InstrumentationTestRunner'],
            capture_output=True, text=True, timeout=300)
        (output / 'instrumentation.txt').write_text(result.stdout + result.stderr)
        print(result.stdout + result.stderr)
        report['device_tests_executed'] = True
        report['callbacks_pixels_audio_pcm_verified'] = result.returncode == 0 and 'OK (2 tests)' in result.stdout
        logs = subprocess.run(adb + ['logcat', '-d', '-s', 'MpvPlayer', 'MpvPlaybackTest', 'AndroidRuntime'],
                              capture_output=True, timeout=15)
        (output / 'logcat.txt').write_bytes(logs.stdout)
        frame = subprocess.run(adb + ['exec-out', 'run-as', PACKAGE, 'cat', 'files/video-frame.png'],
                               capture_output=True, timeout=15)
        if frame.returncode == 0 and frame.stdout.startswith(b'\x89PNG\r\n\x1a\n'):
            (output / 'video-frame.png').write_bytes(frame.stdout)
        if not report['callbacks_pixels_audio_pcm_verified']:
            raise RuntimeError('Playback checks failed; see instrumentation.txt and logcat.txt')
    except (OSError, subprocess.SubprocessError, RuntimeError) as error:
        report['error'] = str(error)
        raise
    finally:
        report_path.write_text(json.dumps(report, indent=2) + '\n')


if __name__ == '__main__':
    main()
