# Changelog

All notable changes to Magic Haptic Assistant are documented here.

## [Unreleased]

### Added
- Discoverable test entrypoints: `Makefile`, `scripts/test.sh`, `scripts/build.sh`, and a CI job named `test`.
- `.repo-meta.json` declaring primary class `mobile`/`android` (not infra/IaC).
- `.env.example` and expanded README quick-start via `make test` / `make build`.
- `SpeechJsonExtractor` with unit tests; `AudioListenerService` delegates JSON extraction to it.
- `DeckValidatorTest`, `ServiceEventBusTest`, `NumberWordConverterTest`, and TriggerParser input-validation tests.
- `CardViewModel` shared by `ControlFragment` / `ReferenceFragment`, with `CardViewModelTest`.
- JaCoCo `jacocoTestCoverageVerification` (50% line coverage on core packages) enforced in CI.
- Trivy filesystem dependency audit job in CI (`dependency-audit`).

### Changed
- `NumberWordConverter` ignores optional `and` between tens/units (e.g. "thirty and five").
- `TriggerParser` rejects blank/oversized input and negative debounce values.
- CI split into separate `test`, `build`, `lint`, and `dependency-audit` jobs.

## [Previous]

### Added
- Unit tests for `AppDataStore` / `CardRepository`; JaCoCo; ktlint; Timber `AppLogger`.
- Gradle lockfiles, Dependabot, Docker Compose / Dockerfile / devcontainer.
- `CONTRIBUTING.md`, `CHANGELOG.md`, `local.properties.example`.
