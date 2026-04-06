# LINT_REPORT

Summary (generated 2026-04-06):
- Initial lint run reported 1 error and 90 warnings for the `debug` build. The top error was a MissingPermission for `RECORD_AUDIO` at
  `app/src/main/java/com/magic/haptic/speech/VoskRecognizerManager.kt:65`.

Status (after fix):
- The MissingPermission issue was addressed by adding an explicit runtime permission check before creating `AudioRecord`. A subsequent lint run reported no errors and only warnings.

Full HTML report (path in workspace):
- `app/build/reports/lint-results-debug.html`

Notes & next steps:
- The code now checks `ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)` and returns a `SecurityException` via the recognition callback if permission is not granted. Ensure UI flows request RECORD_AUDIO before starting the service.
- If you prefer to suppress existing warnings and only surface new ones, create a lint baseline: add `lint { baseline = file("lint-baseline.xml") }` to module `build.gradle.kts` and run `./gradlew updateLintBaseline`.

--- End of report ---

