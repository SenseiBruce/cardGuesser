# LINT_REPORT

Summary (generated 2026-04-06):
- Lint found 1 error and 90 warnings for the `debug` build.
- First error (MissingPermission) location:
  - `app/src/main/java/com/magic/haptic/speech/VoskRecognizerManager.kt:65`
  - Message: Call requires permission which may be rejected by user — explicitly check permission or handle SecurityException.

Full lint text report (path in workspace):
- `app/build/intermediates/lint_intermediate_text_report/debug/lintReportDebug/lint-results-debug.txt`

Suggested next actions:
- Add runtime permission check (e.g., ContextCompat.checkSelfPermission) before creating `AudioRecord`, and handle the case where RECORD_AUDIO is denied.
- Optionally create a lint baseline if you want to ignore existing issues and only surface new ones: add `lint { baseline = file("lint-baseline.xml") }` to module `build.gradle.kts` and run `./gradlew updateLintBaseline`.

Excerpt (first reported failure):

/Users/kinshuk.prasad/Documents/Project_X/cardGuesser/app/src/main/java/com/magic/haptic/speech/VoskRecognizerManager.kt:65: Error: Call requires permission which may be rejected by user: code should explicitly check to see if permission is available (with checkPermission) or explicitly handle a potential SecurityException [MissingPermission]
    audioRecord = AudioRecord(
                  ^


--- End of report ---

