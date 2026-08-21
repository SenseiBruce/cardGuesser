#!/usr/bin/env bash
# First-time contributor setup for a fresh clone.
set -euo pipefail
cd "$(dirname "$0")/.."

chmod +x ./gradlew ./scripts/test.sh ./scripts/build.sh ./scripts/setup.sh

if [[ ! -f .env ]]; then
  cp .env.example .env
  echo "Created .env from .env.example"
else
  echo ".env already present — leaving it unchanged"
fi

if [[ ! -f local.properties ]]; then
  if [[ -n "${ANDROID_SDK_ROOT:-}" ]]; then
    echo "sdk.dir=$ANDROID_SDK_ROOT" > local.properties
    echo "Wrote local.properties from ANDROID_SDK_ROOT"
  elif [[ -n "${ANDROID_HOME:-}" ]]; then
    echo "sdk.dir=$ANDROID_HOME" > local.properties
    echo "Wrote local.properties from ANDROID_HOME"
  else
    cp local.properties.example local.properties
    echo "Created local.properties from example — edit sdk.dir before building"
  fi
fi

echo
echo "Next steps:"
echo "  1. Ensure JDK 17+ and Android SDK 34 are installed"
echo "  2. (Device builds only) Download vosk-model-small-en-us and unpack to:"
echo "       app/src/main/assets/model-en-us/"
echo "  3. Run the suite:  ./gradlew test"
echo "     or:             make test"
echo "     or:             ./scripts/test.sh"
