#!/bin/bash
# Download dependencies from a network-enabled terminal, without compiling.
set -euo pipefail

script_dir=$(cd "$(dirname "$0")" && pwd)
mpv_python=
for candidate in python3.12 python3.13 python3.14 python3 \
    "$HOME/.cache/codex-runtimes/codex-primary-runtime/dependencies/python/bin/python3"; do
    if command -v "$candidate" >/dev/null 2>&1 && \
       "$candidate" -B -c 'import sys, tarfile; sys.exit(not hasattr(tarfile, "data_filter"))' 2>/dev/null; then
        mpv_python=$candidate
        break
    fi
done
if [ -z "$mpv_python" ]; then
    echo 'Python with tarfile.data_filter is required; install Python 3.12+.' >&2
    exit 1
fi

mpv_ndk=${ANDROID_NDK_ROOT:-${ANDROID_HOME:-${ANDROID_SDK_ROOT:-$HOME/Library/Android/sdk}}/ndk/28.1.13356709}
echo "Using Python: $mpv_python"
exec "$mpv_python" -B "$script_dir/build_native.py" --download-only --ndk "$mpv_ndk" "$@"
