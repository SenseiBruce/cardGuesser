# MEMORY_BANK

## Project Snapshot
- App: Magic Haptic Assistant (`com.magic.haptic`), Android native Kotlin app for magicians to receive card identity via haptic feedback.
- Primary workflow: spoken trigger phrase -> extract position (1-52) -> deck lookup -> vibration pattern for rank/suit.
- Platform constraints: Min SDK 26, Target SDK 34, MVVM + Foreground Service, fully offline from first launch.
- Single source of truth for requirements: `Requirement.md`.

## Architecture Map
- Runtime flow: `AudioListenerService` (foreground) orchestrates speech recognition, parsing, lookup, encoding, and vibration.
- Speech pipeline: Microphone -> `AudioRecord` -> Vosk recognizer -> text callbacks (partial/final).
- Parsing pipeline: `TriggerParser` + `NumberWordConverter` -> `TriggerResult(position)` with 3-second debounce.
- Card pipeline: `CardRepository.getCard(position)` returns card code (`AS`, `QD`, etc.).
- Haptics pipeline: `HapticEncoder` -> `HapticPattern` -> `HapticPlayer` -> Android vibrator APIs.
- UI shell: single `MainActivity` with 3 tabs (`Control`, `Test`, `Settings`).
- Inter-component signaling: `ServiceEventBus` via `StateFlow`/`SharedFlow`.

## Domain Glossary
- Trigger phrase: speech pattern that contains position info and matches parser regex.
- Position: 1-based integer index into current deck (`1..52`).
- Deck preset: predefined order (`Default`, `Mnemonica`, `Aronson`).
- Custom deck: user CSV of 52 cards; must pass strict validation.
- Haptic pattern: waveform timings + amplitudes encoding rank then suit with separator.
- Debounce window: ignore repeated triggers for configured seconds (default 3).

## Important Paths and Ownership
- Requirements/spec: `Requirement.md` (canonical), `LLD.md`, `business_logic.md`.
- App module: `app/`.
- Source root: `app/src/main/java/com/magic/haptic/`.
- Key packages present: `card/`, `data/`, `haptic/`, `parser/`, `service/`, `speech/`, `ui/`.
- App entrypoint file present: `app/src/main/java/com/magic/haptic/MagicApp.kt`.
- Prompt commands: `.github/prompts/`.

## Technical Constraints
- No network calls, no cloud speech, no paid SDKs.
- No audio or visual feedback during live listening.
- Foreground service must work with screen off; notification required.
- Vosk model must be bundled in APK assets (`app/src/main/assets/model-en-us/`) and copied to internal storage for runtime use.
- Runtime permissions include microphone and notifications (API 33+ behavior noted in spec).
- Robustness rule: never crash during performance; recover/retry where specified.

## Build/Test/Run Commands
- Assumption: Gradle wrapper-based workflow (wrapper files are present).
- Common commands to verify locally:
  - `./gradlew :app:assembleDebug`
  - `./gradlew :app:testDebugUnitTest`
  - `./gradlew :app:lintDebug`

## Active Priorities
- Implement/verify end-to-end voice-to-vibration flow with foreground service lifecycle safety.
- Ensure parser coverage for all six trigger patterns and all number formats (digits/words/ordinals/hyphenated).
- Ensure haptic uniqueness and timing constraints (including Queen vs Six non-collision requirement).
- Complete settings flows: deck presets/custom validation, speed presets/custom timing validation, debounce, notification disguise.
- Validate runtime permission handling for grant/deny/revoke paths.

## Risk Register
- Model asset missing or incorrectly placed -> speech init failure.
- OEM battery optimization may kill background service unexpectedly.
- Noisy environments reduce speech accuracy.
- Pocket perceptibility variance across devices can affect usability.
- Trigger duplication risk from partial+final callback pairs if debounce is incorrect.

## Open Questions
- What is the canonical status of current implementation versus `Requirement.md` (done vs pending by section)?
- Should this memory bank track completion by requirement section (traceability matrix) in future updates?
- What real device(s) are used for vibration perceptibility and long-running service validation?
- Are any requirement decisions intentionally overridden since the spec was written?

## Source Traceability
- Product and constraints: `Requirement.md` Sections "PROJECT METADATA", "CRITICAL CONSTRAINTS", "WHAT THE APP DOES".
- Pipeline/components: `Requirement.md` Section 5 and `LLD.md` Architecture + Sequence diagrams.
- Trigger and haptic rules: `Requirement.md` Sections 3 and 4.
- Persistence/keys/state handling: `LLD.md` Persistence + State Machine.
- User workflow and visuals: `business_logic.md` and `README.md`.
 
## Build Environment (verified)
- JDK: Java 17 required (verified with `/usr/libexec/java_home -v 17`).
- Gradle wrapper: project now includes a working Gradle wrapper configured to Gradle 8.7 (updated to satisfy AGP 8.6.0 requirements).
- Android Gradle Plugin (AGP): 8.6.0 (declared in root `build.gradle.kts`).
- Commands used to validate locally (run from repo root):
  - `export JAVA_HOME="$(/usr/libexec/java_home -v 17)"`
  - `./gradlew :app:compileDebugKotlin`
  - `./gradlew :app:testDebugUnitTest`
  - `./gradlew :app:lintDebug`  (lint may take several minutes)

Notes:
- The repository previously lacked wrapper binaries; the wrapper was regenerated and set to Gradle 8.7 (see `gradle/wrapper/gradle-wrapper.properties`).
- If CI uses a different JDK, ensure it points to Java 17+.

## Lint Results (summary)
- Date: 2026-04-06
- Lint outcome (initial): 1 error, 90 warnings (debug). After fixing the MissingPermission issue, lint now reports 0 errors and 90 warnings.
- Top error: MissingPermission at `app/src/main/java/com/magic/haptic/speech/VoskRecognizerManager.kt:65` — AudioRecord constructor is used without an explicit runtime `RECORD_AUDIO` permission check. See `LINT_REPORT.md` for full details and suggested fixes.

Action items:
- Add runtime permission checks (ContextCompat.checkSelfPermission) before creating `AudioRecord` and handle denial gracefully.
- Consider adding a lint baseline if you want to defer fixing existing warnings: add `lint { baseline = file("lint-baseline.xml") }` to module `build.gradle.kts` and run `./gradlew updateLintBaseline`.

## Task History
### Task: Initialize project memory & fix Gradle wrapper
- Date: 2026-04-06
- Scope: Added Copilot prompt files; created `MEMORY_BANK.md` and `TASK_BOOTSTRAP.md`; restored and updated Gradle wrapper to 8.7; verified compilation and unit tests using Java 17.
- Files changed: `.github/prompts/project-memory-init.prompt.md`, `MEMORY_BANK.md`, `TASK_BOOTSTRAP.md`, `gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.jar`, `gradle/wrapper/gradle-wrapper.properties`
- Validation performed: `:app:compileDebugKotlin` (success), `:app:testDebugUnitTest` (success), `:app:lintDebug` (failed; 1 error — see LINT_REPORT.md)
- Assumption: Local Java 17 is available at `/usr/libexec/java_home -v 17`. CI must be configured similarly.
- Open question: Do you want lint results captured and appended here once complete, or would you prefer those in a separate `LINT_REPORT.md`?

