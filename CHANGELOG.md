# Changelog

All notable changes to Magic Haptic Assistant are documented here.

## [Unreleased]

### Added
- Unit tests for `AppDataStore` persistence round-trips and `CardRepository.getCard` bounds handling.
- Real assertions for `"the number …"` digit and word triggers (replacing no-op `TempTest`).
- JaCoCo coverage report task (`:app:jacocoTestReport`) wired into CI.
- ktlint Gradle plugin with CI `ktlintCheck` enforcement.
- `AppLogger` Timber wrapper and structured logging in speech/service layers.
- Gradle dependency locking (`app/gradle.lockfile`, `settings-gradle.lockfile`), version catalog, and Dependabot weekly updates.
- Docker Compose + Dockerfile for isolated build/test runs.
- Expanded README with architecture overview, explicit build/test commands, and container startup.
- `CONTRIBUTING.md` and `local.properties.example` for fresh-clone onboarding.

### Changed
- `AppDataStore` accepts an injectable `DataStore<Preferences>` for unit testing while retaining the Context constructor.
- `AudioListenerService.extractJsonText` logs JSON parse failures instead of swallowing them silently.
- `CardRepository` accepts an injectable `CoroutineScope` and exposes `close()` for clean test teardown.
