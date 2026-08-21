#!/usr/bin/env bash
# Run the unit test suite from a fresh clone.
# Canonical command (also used by CI and Makefile): ./gradlew test
set -euo pipefail
cd "$(dirname "$0")/.."
chmod +x ./gradlew
exec ./gradlew test --no-daemon "$@"
