# Contributing

## Prerequisites

- JDK 17+
- Android SDK 34 (or use Docker Compose — see README)

## Workflow

1. Create a focused branch for one feature or fix.
2. Ship the production change **with unit tests that pin the new behavior** in the same commit.
3. Keep commits small; avoid mixing formatting, refactors, and features.
4. Update `CHANGELOG.md` in the same commit when behavior changes.

## Verify before opening a PR

```bash
make check
# or
./scripts/test.sh
./gradlew :app:jacocoTestCoverageVerification ktlintCheck :app:lintDebug --no-daemon
```

Branch protection on `main` should require the CI `test` job (and preferably `build` / `lint` / `dependency-audit`) to pass before merge.

Or in isolation:

```bash
docker compose run --rm unit-tests
```

## Style

ktlint is enforced in CI (`./gradlew ktlintCheck`). Run `./gradlew ktlintFormat` before committing Kotlin changes.
