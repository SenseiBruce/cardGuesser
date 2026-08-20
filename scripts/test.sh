#!/usr/bin/env bash
# Run the unit test suite from a fresh clone.
set -euo pipefail
cd "$(dirname "$0")/.."
chmod +x ./gradlew
exec ./gradlew :app:testDebugUnitTest --no-daemon "$@"
