# Contributing to cardGuesser

Thank you for helping improve Magic Haptic Assistant. This guide follows the same
open-source practices used by mature projects such as [Flask](https://github.com/pallets/flask).

## Prerequisites

- JDK 17+
- Android SDK 34 (or use Docker Compose — see README)

## Setup

```bash
./scripts/setup.sh
```

## Workflow

1. Create a focused branch for one feature or fix.
2. Ship the production change **with unit tests that pin the new behavior** in the same commit.
3. Keep commits small; avoid mixing formatting, refactors, and features.
4. Update `CHANGELOG.md` in the same commit when behavior changes.

## Verify before opening a PR

```bash
./gradlew test
./gradlew :app:jacocoTestCoverageVerification ktlintCheck :app:lintDebug --no-daemon
# or
make check
```

## Branch protection (maintainers)

On GitHub → Settings → Branches → `main` protection rules, require status checks:

- `test`
- `build`
- `lint`
- `dependency-audit`

Do not allow bypassing these checks for administrators in production workflows.

Or in isolation:

```bash
docker compose run --rm unit-tests
```

## Style

ktlint is enforced in CI (`./gradlew ktlintCheck`). Run `./gradlew ktlintFormat` before committing Kotlin changes.
Use `.editorconfig` for consistent formatting across editors.

## Security

Do not commit API keys, tokens, `.env`, or `local.properties`. Report security issues privately
to the repository owner.
