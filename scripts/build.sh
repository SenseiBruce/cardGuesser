#!/usr/bin/env bash
# Assemble the debug APK from a fresh clone.
# Prefer assembleDebug over full `build` (full build also compiles release and can OOM on large assets).
set -euo pipefail
cd "$(dirname "$0")/.."
chmod +x ./gradlew
exec ./gradlew :app:assembleDebug --no-daemon "$@"
