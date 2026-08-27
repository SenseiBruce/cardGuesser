# Changelog

All notable changes to Magic Haptic Assistant are documented here.

## [Unreleased]

### Added
- Identification drill in Toolkit: play a haptic pattern and choose the card from four options
- Drill streak + best-streak tracking while you identify cards
- Cheat Sheet **Share** exports the active 52-card stack as numbered plain text
- Test tab Perception Log **Clear** button empties the speech log
- Control tab **Copy** exports live session duration, trigger count, and last detection
- Settings tab Copy button pastes a summary of deck, haptic speed, debounce, and disguise
- Cheat Sheet Copy button pastes position, card, and haptic pattern
- Tap session duration on the Control tab to copy `HH:MM:SS` to the clipboard.
- Tap the Control tab status label to copy the listener state.
- Tap the last detected card on the Control tab to copy it.
- Tap the last-phrase line on Control to copy the spoken trigger text
- Tap the last-pattern line on Control to copy the haptic pattern description
- Tap the session trigger count on Control to copy it
- Flask-style repo hygiene: root `LICENSE` (MIT), `.editorconfig`, GitHub issue/PR templates
- Canonical `./gradlew test` / `./gradlew build` root tasks; CI runs `./gradlew test` explicitly.
- `SpeechProcessor` pure pipeline extracted from `AudioListenerService`, with unit tests.
- `SpeechInputSchema` for speech/position/debounce validation at the parser boundary.
- Structured `AppLogger.formatMessage` fields + `CrashReporter` with init/record tests.
- `scripts/setup.sh` for `.env` / `local.properties` bootstrap; expanded `.env.example`.
- README **Project type** banner clarifying this is an Android/Kotlin app, not IaC.
- Control tab can copy the session trigger count.

### Changed
- JaCoCo gate raised to 60% line coverage on core packages (includes `SpeechProcessor`).
- Vosk/service error paths emit structured log fields and report via `CrashReporter`.
- Test tab Copy log copies the perception log to the clipboard.
- Settings can copy the current haptic preset and timings.
- Control tab can copy the last detected trigger (card, phrase, pattern).

## [Previous]

See git history for prior buyer-fit rounds (lockfiles, Docker, ktlint, Trivy, ViewModel, etc.).
