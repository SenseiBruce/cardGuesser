#!/usr/bin/env bash
# Assemble the debug APK from a fresh clone.
set -euo pipefail
cd "$(dirname "$0")/.."
chmod +x ./gradlew
exec ./gradlew :app:assembleDebug --no-daemon "$@"
