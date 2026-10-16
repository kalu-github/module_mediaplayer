#!/bin/bash
# Install ARM64 build tools into this module only. No sudo or Homebrew required.
set -euo pipefail
script_dir=$(cd "$(dirname "$0")" && pwd)
module_dir=$(cd "$script_dir/.." && pwd)
mpv_python=
for candidate in python3.12 python3.13 python3.14 python3 \
    "$HOME/.cache/codex-runtimes/codex-primary-runtime/dependencies/python/bin/python3"; do
    if command -v "$candidate" >/dev/null 2>&1 && \
       "$candidate" -B -c 'import sys, tarfile; sys.exit(sys.version_info < (3, 10) or not hasattr(tarfile, "data_filter"))' 2>/dev/null; then
        mpv_python=$candidate
        break
    fi
done
if [ -z "$mpv_python" ]; then
    echo 'Python 3.12+ is required.' >&2
    exit 1
fi
mpv_tools="$module_dir/build/host-tools"
if [ ! -x "$mpv_tools/bin/python3" ]; then
    "$mpv_python" -m venv "$mpv_tools"
fi
"$mpv_tools/bin/python3" -m pip --disable-pip-version-check --no-cache-dir install \
    --only-binary=:all: --retries 1 --timeout 20 meson==1.11.0 pkgconf==3.0.7.post0
"$mpv_tools/bin/meson" --version
"$mpv_tools/bin/pkg-config" --version
mpv_ndk=${ANDROID_NDK_ROOT:-${ANDROID_HOME:-${ANDROID_SDK_ROOT:-$HOME/Library/Android/sdk}}/ndk/28.1.13356709}
exec "$mpv_tools/bin/python3" -B "$script_dir/build_native.py" --check --abi arm64-v8a --ndk "$mpv_ndk" "$@"
