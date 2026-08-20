# Magic Haptic Assistant

A premium, stealthy tool designed for magicians to discreetly receive information about a spectator's chosen card via haptic feedback.

## Features

- **Stealth Mode**: Dark obsidian UI with gold accents, optimized for low visibility during performance.
- **Offline Intelligence**: Uses Vosk Speech Recognition for total privacy and zero latency.
- **Precision Haptics**: Encodes all 52 cards into unique, tactile vibration patterns.
- **Customizable Stacks**: Built-in support for popular stacks (Mnemonica, Aronson) and full custom stack editing.
- **Encrypted Logs**: Review your performance and detected phrases in a secure toolkit log.

## Architecture

| Package | Responsibility |
|---------|----------------|
| `parser` | Voice-trigger phrase matching and number-word conversion |
| `haptic` | Card → vibration pattern encoding and playback |
| `card` | Deck presets and position → card lookup |
| `data` | Preferences (`AppDataStore`), shared models, event bus |
| `speech` | Vosk model unpack + continuous recognition |
| `service` | Foreground audio listener orchestration |
| `ui` | Activities/fragments for control, settings, and toolkit |

## Requirements

- JDK 17+
- Android SDK platform 34 / build-tools 34.0.0
- Gradle wrapper (included) — no global Gradle install required

## Quick start (from a fresh clone)

```bash
git clone <repo-url> && cd cardGuesser
chmod +x ./gradlew ./scripts/test.sh ./scripts/build.sh

# Optional: copy local SDK path
# cp local.properties.example local.properties

make test      # or: ./scripts/test.sh
make build     # or: ./scripts/build.sh
make coverage  # unit tests + JaCoCo report + 50% line gate
make lint      # Android lint + ktlint
```

Equivalent Gradle commands:

```bash
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest
./gradlew :app:jacocoTestReport :app:jacocoTestCoverageVerification
./gradlew :app:lintDebug ktlintCheck
```

### One-command containerized tests

Buyers and contributors can exercise the suite without a local Android SDK:

```bash
docker compose run --rm unit-tests
```

Other compose targets: `assemble`, `lint`.

### Speech model asset (device builds only)

Unit tests do **not** require the Vosk model. For a runnable APK on device:

1. Download `vosk-model-small-en-us-0.15` from [Vosk Models](https://alphacephei.com/vosk/models).
2. Unzip contents into `app/src/main/assets/model-en-us/`  
   (files such as `am/final.mdl`, `graph/HCLG.fst` must live directly under that folder).

### Permissions

- **Record Audio** — listen for position triggers
- **Notifications** — keep the foreground service alive
- **Vibrate** — deliver haptic signals

## Haptic guide

The assistant uses two pulse types: **S** (Short) and **L** (Long).

### Ranks
| Rank | Pattern | Rank | Pattern |
|------|---------|------|---------|
| 1 | S | 8 | LSSS |
| 2 | SS | 9 | LSL |
| 3 | SSS | 10 | LL |
| 4 | SL | J | LLS |
| 5 | L | Q | LLSS |
| 6 | LS | K | LLSSS |
| 7 | LSS | | |

### Suits
- **Hearts**: S · **Diamonds**: SS · **Clubs**: SSS · **Spades**: L

*Example: "Queen of Spades" = [LLSS] (gap) [L]*

## Dependency locking

Gradle lockfiles are committed (`gradle.lockfile`, `app/gradle.lockfile`). After changing dependencies:

```bash
./gradlew dependencies --write-locks
```

Dependabot opens weekly PRs for Gradle and GitHub Actions updates.

## Stealth tips

1. Use **Notification Disguise** to rename the active task (e.g. "System Optimization").
2. Test haptic sensitivity in the **Toolkit** tab before performing.
3. Keep the device where vibration is felt clearly but not heard.

---
*Built for the Modern Mystery Performer.*
