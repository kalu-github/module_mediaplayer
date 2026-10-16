#!/usr/bin/env python3
"""Combine the four verified per-ABI AARs into one Android library."""
import argparse
import io
import json
from pathlib import Path
import tempfile
import zipfile

from build_native import MODULE, digest
from package_aar import TRIPLES, elf_report


def classes(data):
    with zipfile.ZipFile(io.BytesIO(data)) as jar:
        if jar.testzip() is not None:
            raise RuntimeError('Corrupt classes.jar')
        return {name: jar.read(name) for name in jar.namelist()}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--ndk', type=Path, required=True)
    args = parser.parse_args()
    output = MODULE / 'build/outputs/aar'
    toolchain = next((args.ndk / 'toolchains/llvm/prebuilt').iterdir())
    common = None
    native = {}
    builds = {}
    reports = {}
    exports = {}
    with tempfile.TemporaryDirectory(prefix='combine-', dir=output) as temporary:
        stage = Path(temporary)
        for abi in TRIPLES:
            source = output / f'lib_mediaplayer_mpv_0.41.0_{abi}_merged_release.aar'
            expected_hash = source.with_suffix('.aar.sha256').read_text().split()[0]
            if digest(source) != expected_hash:
                raise RuntimeError('AAR checksum mismatch: ' + str(source))
            with zipfile.ZipFile(source) as aar:
                if aar.testzip() is not None:
                    raise RuntimeError('Corrupt AAR: ' + str(source))
                files = {name: aar.read(name) for name in aar.namelist() if not name.endswith('/')}
            info = json.loads(files.pop('META-INF/mpv-build.json'))
            if info['dependency_build']['abi'] != abi:
                raise RuntimeError('Wrong ABI metadata: ' + str(source))
            names = {name for name in files if name.startswith('jni/')}
            expected = {f'jni/{abi}/libmpvffmpeg.so', f'jni/{abi}/libmpvplayer.so'}
            if names != expected:
                raise RuntimeError('Expected exactly two native libraries for ' + abi)
            libraries = []
            for name in sorted(names):
                data = files.pop(name)
                native[name] = data
                dest = stage / name
                dest.parent.mkdir(parents=True, exist_ok=True)
                dest.write_bytes(data)
                libraries.append(dest)
            reports[abi] = elf_report(libraries, toolchain, abi)
            if reports[abi] != info['libraries']:
                raise RuntimeError('Native libraries differ from build metadata: ' + abi)
            if common is None:
                common = files
            else:
                if set(common) != set(files):
                    raise RuntimeError('AAR contents differ between ABIs')
                for name in common:
                    equal = (classes(common[name]) == classes(files[name])) if name == 'classes.jar' else common[name] == files[name]
                    if not equal:
                        raise RuntimeError('AAR shared content differs: ' + name)
            builds[abi] = info['dependency_build']
            exports[abi] = info['public_exports']
        metadata = {'packager': 'CMake+javac', 'dependency_builds': builds,
                    'libraries': reports, 'public_exports': exports,
                    'device_playback_verified': False}
        common['META-INF/mpv-build.json'] = (json.dumps(metadata, indent=2) + '\n').encode()
        target = output / 'lib_mediaplayer_mpv_0.41.0_all_merged_release.aar'
        pending = target.with_suffix('.aar.partial')
        with zipfile.ZipFile(pending, 'w', zipfile.ZIP_DEFLATED) as aar:
            for name, data in sorted({**common, **native}.items()):
                aar.writestr(name, data)
        with zipfile.ZipFile(pending) as aar:
            if aar.testzip() is not None:
                raise RuntimeError('Corrupt combined AAR')
        pending.replace(target)
        target.with_suffix('.aar.sha256').write_text(digest(target) + '  ' + target.name + '\n')
        report = {'aar': str(target), 'sha256': digest(target), 'bytes': target.stat().st_size,
                  'abis': list(TRIPLES), 'libraries': reports,
                  'public_exports': exports,
                  'device_playback_verified': False}
        target.with_suffix('.verification.json').write_text(json.dumps(report, indent=2) + '\n')
        print('Four-ABI AAR created: ' + str(target))


if __name__ == '__main__':
    main()
