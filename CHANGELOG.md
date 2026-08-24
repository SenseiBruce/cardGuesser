# Changelog

All notable changes to Magic Haptic Assistant are documented here.

## [Unreleased]

### Added
- Identification drill in Toolkit: play a haptic pattern and choose the card from four options
- Drill streak + best-streak tracking while you identify cards
- Cheat Sheet **Share** exports the active 52-card stack as numbered plain text
- Test tab Perception Log **Clear** button empties the speech log
- Control tab **Copy** exports live session duration, trigger count, and last detection
- Flask-style repo hygiene: root `LICENSE` (MIT), `.editorconfig`, GitHub issue/PR templates
- Canonical `./gradlew test` / `./gradlew build` root tasks; CI runs `./gradlew test` explicitly.
- `SpeechProcessor` pure pipeline extracted from `AudioListenerService`, with unit tests.
- `SpeechInputSchema` for speech/position/debounce validation at the parser boundary.
- Structured `AppLogger.formatMessage` fields + `CrashReporter` with init/record tests.
- `scripts/setup.sh` for `.env` / `local.properties` bootstrap; expanded `.env.example`.
- README **Project type** banner clarifying this is an Android/Kotlin app, not IaC.

### Changed
- JaCoCo gate raised to 60% line coverage on core packages (includes `SpeechProcessor`).
- Vosk/service error paths emit structured log fields and report via `CrashReporter`.
- Test tab Copy log copies the perception log to the clipboard.

## [Previous]

See git history for prior buyer-fit rounds (lockfiles, Docker, ktlint, Trivy, ViewModel, etc.).
