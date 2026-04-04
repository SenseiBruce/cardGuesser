🎩 MASTER DEVELOPMENT PROMPT — Magic Haptic Assistant
Hand this document directly to Antigravity. It is self-contained and requires no additional context.

📋 DOCUMENT PURPOSE
This is the complete, final specification for an Android application called Magic Haptic Assistant. Build exactly what is described below. Do not add features not listed. Do not skip features that are listed. If anything is ambiguous, follow the decision stated in the Decision callout boxes.

🔖 PROJECT METADATA
Field	Value
App Name	Magic Haptic Assistant
Package	com.magic.haptic
Platform	Android — Native Kotlin
Min SDK	26 (Android 8.0 Oreo)
Target SDK	34 (Android 14)
Architecture	MVVM + Foreground Service
Speech Engine	Vosk (offline, open-source, Apache 2.0)
UI Toolkit	Material 3 + ViewBinding
Persistence	Jetpack DataStore (Preferences)
Network	NONE — app must work in airplane mode from first launch
External SDKs	NONE paid — only free open-source libraries
APK Bundling	Everything in one APK — Vosk model bundled in assets/
Estimated Size	~55–65 MB (mostly the Vosk model)
🧠 WHAT THE APP DOES (Plain English)
A stage magician places this phone in their pocket. While performing, they speak naturally. Hidden inside their patter are trigger phrases like "card at position twenty three". The phone:

Hears the phrase through the microphone
Extracts the number (23)
Looks up position 23 in a pre-memorized deck order
Vibrates a coded pattern that tells the magician the card identity (e.g., Queen of Diamonds)
The audience sees nothing, hears nothing. The magician feels the answer in their pocket.

⚠️ CRITICAL CONSTRAINTS
#	Constraint	Explanation
1	Zero audio output during listening	No beeps, no TTS, no confirmation sounds
2	Zero visual output during listening	No toasts, no screen wake, no pop-ups
3	Fully offline	No network calls ever. Vosk model ships inside APK
4	Works with screen off	Foreground service keeps listening
5	Works in pocket	Vibration must be strong (max amplitude)
6	Single APK	No runtime downloads, no split APKs for model
7	No paid SDKs	Only free / open-source dependencies
📱 PERMISSIONS REQUIRED
Declare all of these. Request runtime permissions on first launch.

Permission	Type	Required For
RECORD_AUDIO	Runtime	Microphone access
VIBRATE	Manifest	Haptic output
FOREGROUND_SERVICE	Manifest	Keep service alive
FOREGROUND_SERVICE_MICROPHONE	Manifest	Mic in FG service (API 34+)
POST_NOTIFICATIONS	Runtime (API 33+)	Foreground service notification
If user denies RECORD_AUDIO: Show explanation, disable Start button, do not crash.
If user denies POST_NOTIFICATIONS: Service still works but notification may not show. Acceptable.

🃏 SECTION 1: CARD SYSTEM
1.1 Card Format
text
Format: <RANK><SUIT>

RANK values: A, 2, 3, 4, 5, 6, 7, 8, 9, 10, J, Q, K
SUIT values: S (Spades), H (Hearts), D (Diamonds), C (Clubs)

Examples: AS, 2D, 10H, KC, QS
Note: "10" is the only two-character rank.
Full deck = 52 unique cards
1.2 Deck Storage
Store as List<String> of exactly 52 elements
Persist the user's chosen deck across app restarts using DataStore
On launch, load saved deck; if none saved, use Default
1.3 Built-In Deck Presets
Ship these three built-in orderings. User picks from a dropdown.

Default (New Deck Order):

text
AS,2S,3S,4S,5S,6S,7S,8S,9S,10S,JS,QS,KS,
AH,2H,3H,4H,5H,6H,7H,8H,9H,10H,JH,QH,KH,
AD,2D,3D,4D,5D,6D,7D,8D,9D,10D,JD,QD,KD,
AC,2C,3C,4C,5C,6C,7C,8C,9C,10C,JC,QC,KC
Mnemonica (Juan Tamariz):

text
4C,2H,7D,3C,4H,6D,AS,5H,9S,2S,QH,3D,QC,8H,6S,5S,9H,KC,2D,JH,
3S,8S,6H,10C,5D,KD,2C,3H,8D,5C,KS,JD,8C,10S,KH,JC,7S,10H,AD,4S,
7H,4D,AC,9C,JS,QD,7C,QS,10D,6C,AH,9D
Aronson (Simon Aronson):

text
JS,KC,5C,2H,9S,AS,3H,6C,8D,AC,10S,5H,2D,KD,7D,8C,3S,AD,7S,5S,
QH,AH,8S,3C,10H,6H,4D,7H,KH,4H,JD,8H,10D,JC,3D,9H,QS,9C,2S,4S,
6S,5D,QD,10C,9D,JH,QC,2C,4C,7C,6D,KS
1.4 Custom Deck
User can type or paste a comma-separated list of 52 cards
Validate before saving:
Exactly 52 entries
Every entry is a valid card string
No duplicates
No missing cards
Show clear error messages if validation fails
Do NOT save invalid decks
1.5 Card Lookup
text
Input:  integer position (1-based)
Output: deck[position - 1]

If position < 1 or > 52 → return null (never crash)
🎤 SECTION 2: VOICE RECOGNITION
2.1 Engine Choice: Vosk
Decision: Use Vosk (com.alphacephei:vosk-android:0.3.47), NOT Android's built-in SpeechRecognizer.

Reasons:

SpeechRecognizer has 5–7 second silence timeouts, creating gaps
SpeechRecognizer may send audio to Google cloud (violates offline constraint)
SpeechRecognizer must run on main thread with Looper (fragile in services)
Vosk provides true continuous streaming, runs on any thread, guaranteed offline
2.2 Model Bundling
Use vosk-model-small-en-us (~40-50 MB)
Download from: https://alphacephei.com/vosk/models/vosk-model-small-en-us-0.15.zip
Extract contents into app/src/main/assets/model-en-us/
The folder should contain subdirectories like am/, conf/, graph/, ivector/
At app startup, copy from assets to internal storage if not already present (Vosk requires filesystem path)
2.3 Audio Capture Settings
Parameter	Value
Sample Rate	16000 Hz
Channel	Mono (AudioFormat.CHANNEL_IN_MONO)
Encoding	PCM 16-bit (AudioFormat.ENCODING_PCM_16BIT)
Audio Source	MediaRecorder.AudioSource.VOICE_RECOGNITION
Buffer Size	AudioRecord.getMinBufferSize(16000, MONO, PCM16) * 2
2.4 Recognition Pipeline
text
Microphone → AudioRecord → byte[] buffer → Vosk Recognizer
    → partial results (onPartialResult callback)
    → final results (onResult callback)
    → feed text into TriggerParser
2.5 Continuous Operation
Recognition must run indefinitely in a foreground service
Feed audio buffers to Vosk in a continuous loop on a background thread
Process both partial and final results for lowest latency
If Vosk or AudioRecord throws an error, wait 1 second and restart
🗣️ SECTION 3: TRIGGER PHRASE PARSING
3.1 Supported Trigger Patterns
The app must detect these patterns in recognized text (case-insensitive):

#	Pattern	Example Spoken Phrase
1	card at position <X>	"the card at position twenty three"
2	position <X> card	"position five card"
3	<X>th position	"twenty third position"
4	position number <X>	"position number forty two"
5	card number <X>	"card number seven"
6	number <X> card	"number twelve card"
Where <X> is any number from 1 to 52 in any of these formats:

Digits: "5", "23", "52"
Words: "five", "twenty three", "forty two"
Ordinals: "5th", "23rd", "1st", "forty second", "twenty-third"
3.2 Number Word Conversion
Build a converter that handles all integers 1–52 in word form:

text
Units:  one(1), two(2), three(3), four(4), five(5), six(6), seven(7),
        eight(8), nine(9), ten(10), eleven(11), twelve(12), thirteen(13),
        fourteen(14), fifteen(15), sixteen(16), seventeen(17), eighteen(18),
        nineteen(19)

Tens:   twenty(20), thirty(30), forty(40), fifty(50)

Compounds: "twenty three" → 20+3=23, "forty two" → 40+2=42

Ordinals (strip suffix, convert):
  "first"→1, "second"→2, "third"→3, "fourth"→4, "fifth"→5, ...
  "twentieth"→20, "twenty first"→21, ...
  "5th"→5, "23rd"→23, "42nd"→42, "51st"→51

Hyphenated: "twenty-three" → treat same as "twenty three"
3.3 Matching Rules
Scenario	Behavior
Pattern matched, number is 1–52	✅ Proceed to lookup + vibrate
Pattern matched, number is 0, 53+, or negative	❌ Ignore silently
Pattern matched, no parseable number	❌ Ignore silently
No pattern matched	❌ Ignore, keep listening
Multiple numbers in phrase	Take the first valid one
Two triggers within 3 seconds of each other	Debounce — ignore the second
3.4 Phrases That Must NOT Trigger (Negative Cases)
text
"I have 52 cards in my hand"         → NO (no trigger pattern)
"the position is unknown"            → NO (no number)
"card at position zero"              → NO (out of bounds)
"card at position 100"               → NO (out of bounds)
"what card is that"                  → NO (no pattern)
Random audience chatter              → NO
3.5 Debounce
After a successful trigger, ignore all subsequent triggers for 3 seconds (configurable)
This prevents the same spoken phrase from firing twice if Vosk sends it in both partial and final results
📳 SECTION 4: HAPTIC ENCODING
4.1 Pulse Definitions
Symbol	Name	Default Duration	Description
S	Short pulse	100 ms	Quick vibration tap
L	Long pulse	300 ms	Sustained vibration buzz
G	Gap	150 ms	Silence between pulses in a group
SEP	Separator	500 ms	Silence between RANK and SUIT
4.2 Rank Encoding (13 unique patterns)
Rank	Pattern	Description
A	S	1 short
2	S S	2 shorts
3	S S S	3 shorts
4	S L	1 short + 1 long
5	L	1 long
6	L S	1 long + 1 short
7	L S S	1 long + 2 shorts
8	L S S S	1 long + 3 shorts
9	L S L	1 long + 1 short + 1 long
10	L L	2 longs
J	S L L	1 short + 2 longs
Q	S S L	2 shorts + 1 long
K	L L L	3 longs
⚠️ NOTE: Queen is S S L, NOT L S. This was changed to avoid collision with rank 6 (L S). Verify this in your implementation.

4.3 Suit Encoding (4 unique patterns)
Suit	Pattern	Mnemonic
Spades ♠	S L S	Sandwich
Hearts ♥	S S S S	Fast heartbeat
Diamonds ♦	L L S	Dash-dash-dot
Clubs ♣	S S S L	Slow build
4.4 Full Vibration Structure
text
[RANK pulses] → [500ms silence SEPARATOR] → [SUIT pulses]
Each pulse within a group is separated by a 150ms gap.

4.5 Collision Verification
All 13 rank patterns are unique. All 4 suit patterns are unique. Some rank patterns equal some suit patterns (e.g., rank 5 = L = Spades suit), but this is safe because the 500ms separator makes the boundary between rank and suit unambiguous.

4.6 Encoding Example: Queen of Diamonds (QD)
text
Rank Q: S  G  S  G  L
        100 150 100 150 300  = 800ms

Separator: 500ms

Suit D:  S   G   L
         100 150 300  = 550ms

Total duration: 1850ms

Vibrator timings array (starting with 0ms delay):
  [0, 100, 150, 100, 150, 300, 500, 100, 150, 300]
Amplitudes:
  [0, 255,   0, 255,   0, 255,   0, 255,   0, 255]
4.7 Worst-Case Duration
King of Clubs (KC):

text
K: L(300) G(150) L(300) G(150) L(300) = 1200ms
SEP: 500ms
Clubs: L(300) G(150) S(100) = 550ms
TOTAL: 2250ms
Decision: Maximum haptic duration is 2.5 seconds at Normal speed. This is acceptable.

4.8 Speed Presets
Preset	S	L	G	SEP	Max Duration
Fast	80ms	200ms	100ms	350ms	~1600ms
Normal	100ms	300ms	150ms	500ms	~2250ms
Slow	150ms	400ms	200ms	600ms	~3100ms
Custom	User-defined	User-defined	User-defined	User-defined	Cap at 4000ms
User selects preset in Settings. If Custom, they enter four values. Validate that worst-case (King of Clubs) stays ≤ 4000ms; reject otherwise.

4.9 Vibration API
kotlin
// API 31+ (Android 12):
val vibratorManager = getSystemService(VibratorManager::class.java)
val vibrator = vibratorManager.defaultVibrator

// API 26–30:
val vibrator = getSystemService(Vibrator::class.java)

// Execute pattern:
val effect = VibrationEffect.createWaveform(timingsArray, amplitudesArray, -1) // -1 = don't repeat
vibrator.vibrate(effect)
Always use amplitude 255 (maximum) for perceptibility in pocket.

🔄 SECTION 5: SERVICE ARCHITECTURE
5.1 Component Overview
text
┌──────────────────────────────────────────────────────┐
│                    MainActivity                       │
│         (3 tabs: Control / Test / Settings)           │
└───────────────────────┬──────────────────────────────┘
                        │ startService / stopService
                        ▼
┌──────────────────────────────────────────────────────┐
│              AudioListenerService                     │
│              (Foreground Service)                     │
│                                                      │
│   VoskRecognizerManager                              │
│       │ recognized text                              │
│       ▼                                              │
│   TriggerParser                                      │
│       │ position (Int)                               │
│       ▼                                              │
│   CardRepository                                     │
│       │ card string (e.g. "QD")                      │
│       ▼                                              │
│   HapticEncoder                                      │
│       │ HapticPattern (timings + amplitudes)         │
│       ▼                                              │
│   HapticPlayer                                       │
│       │ vibrate()                                    │
│       ▼                                              │
│   [Phone vibrates in pocket]                         │
└──────────────────────────────────────────────────────┘
5.2 AudioListenerService
Type: Foreground Service with foregroundServiceType="microphone"
Lifecycle: START_STICKY (OS restarts it if killed)
Notification: Low-priority, persistent, with disguised text (user-configurable title/body, default: "System Optimizer" / "Running…")
Responsibilities: Wire all components together, manage lifecycle
On start: load deck, init Vosk, start listening loop
On stop: release AudioRecord, release Vosk model, stop foreground
5.3 VoskRecognizerManager
Initialize Vosk model from internal storage (copy from assets on first run)
Create AudioRecord with settings from Section 2.3
Run a loop on a dedicated background thread: read buffer → feed to Vosk
Provide callback interface for partial and final results
On any error: log it, wait 1 second, restart everything
5.4 TriggerParser
Receives raw text strings from Vosk
Applies regex patterns from Section 3.1
Uses NumberWordConverter to parse <X> from matched text
Returns TriggerResult(position, rawText, matchedPattern) or null
Handles debounce (tracks last trigger timestamp)
5.5 CardRepository
Stores current deck as List<String>
Loads from DataStore on service start
Provides getCard(position: Int): String?
Exposes current deck for UI
Can hot-swap deck if settings change while running
5.6 HapticEncoder
Pure function, no side effects — easily unit-testable
Input: card string (e.g., "QD") + HapticConfig
Output: HapticPattern (timings array, amplitudes array, description string, total duration)
Parses rank and suit from card string
Encodes rank → pulse list, suit → pulse list
Joins with separator
Converts pulse list to Android vibration arrays
5.7 HapticPlayer
Takes a HapticPattern and executes it on the device vibrator
Handles API branching (VibratorManager on API 31+ vs Vibrator on 26–30)
Always uses amplitude 255
If vibrator not available, logs error silently (no crash)
5.8 Communication: Service ↔ UI
Use a singleton event bus with Kotlin StateFlow and SharedFlow:

text
ServiceEventBus (singleton object):
  - status: StateFlow<ServiceStatus>          // STOPPED, INITIALIZING, LISTENING, ERROR
  - speechLog: SharedFlow<SpeechLogEntry>     // all recognized text for debug display
  - triggerEvent: SharedFlow<TriggerResult>   // each successful trigger
  - lastTrigger: StateFlow<TriggerResult?>    // most recent trigger for Control tab
  - triggerCount: StateFlow<Int>              // session count
  - sessionStartTime: StateFlow<Long>         // for duration display
No need for AIDL, bound services, or broadcast receivers. SharedFlow is sufficient.

📱 SECTION 6: USER INTERFACE
6.1 Structure
Single MainActivity with a ViewPager2 + TabLayout holding 3 fragments:

Control — Start/stop, status, session info
Test — Manual testing and debug
Settings — Configuration
6.2 Tab 1: Control
text
┌──────────────────────────────────────┐
│  Magic Haptic Assistant              │
│                                      │
│  Status: ● LISTENING                 │  (green=active, grey=stopped,
│                                      │   red=error, yellow=initializing)
│                                      │
│  ┌──────────────────────────────┐    │
│  │      START LISTENING         │    │  (large toggle button;
│  │      [STOP LISTENING]        │    │   changes label when active)
│  └──────────────────────────────┘    │
│                                      │
│  Session Duration:  00:12:34         │
│  Triggers Detected: 3               │
│                                      │
│  ── Last Trigger ──                  │
│  Phrase: "card at position 23"       │
│  Position: 23                        │
│  Card: QD (Queen of Diamonds)        │
│  Pattern: S S L | SEP | S L         │
│                                      │
└──────────────────────────────────────┘
6.3 Tab 2: Test Mode
text
┌──────────────────────────────────────┐
│  TEST MODE                           │
│                                      │
│  Position: [____]  [VIBRATE]         │  (enter 1-52, tap button)
│                                      │
│  Card: QD (Queen of Diamonds)        │
│  Rank Pattern: S S L                 │
│  Suit Pattern: S L                   │
│  Full Pattern: S S L | SEP | S L    │
│  Duration: 1850ms                    │
│                                      │
│  ── Quick Test ──                    │
│  [AS] [KC] [6S] [QH]                │  (tap any card to vibrate it)
│  [10D] [JC] [4H] [9S]               │
│                                      │
│  ── Speech Log ──                    │
│  12:01:05 ✓ "card at position 23"   │  (green = matched)
│  12:01:03   "the card at"           │  (grey = partial, no match)
│  12:00:58   "how about the"         │  (grey = no match)
│  (scrolling list, newest on top)     │
│                                      │
└──────────────────────────────────────┘
Quick Test Cards (chosen to cover edge cases):

AS — shortest pattern (Ace of Spades)
KC — longest pattern (King of Clubs)
6S — rank 6 = L S, previously collided with Q
QH — new Queen encoding S S L
10D — two-character rank
JC — Jack + Clubs
4H — max consecutive shorts for rank
9S — max rank with shorts + long
6.4 Tab 3: Settings
text
┌──────────────────────────────────────┐
│  SETTINGS                            │
│                                      │
│  ── Deck ──                          │
│  Preset: [Default ▼]                 │  (dropdown: Default / Mnemonica
│  [Edit Custom Deck]                  │   / Aronson / Custom)
│                                      │
│  ── Haptic Speed ──                  │
│  ( ) Fast  (●) Normal  ( ) Slow     │  (radio buttons)
│  ( ) Custom                          │
│    Short: [100] ms                   │  (shown only if Custom selected)
│    Long:  [300] ms                   │
│    Gap:   [150] ms                   │
│    Sep:   [500] ms                   │
│    [Validate & Save]                 │
│                                      │
│  ── Notification Disguise ──         │
│  Title: [System Optimizer       ]    │
│  Body:  [Running...             ]    │
│                                      │
│  ── Advanced ──                      │
│  Debounce: [3] seconds              │
│                                      │
└──────────────────────────────────────┘
6.5 Custom Deck Editor (Sub-screen or Dialog)
text
┌──────────────────────────────────────┐
│  CUSTOM DECK EDITOR                  │
│                                      │
│  Paste 52 cards, comma-separated:    │
│  ┌──────────────────────────────┐    │
│  │ 4C,2H,7D,3C,4H,6D,AS,...   │    │  (large multiline EditText)
│  │                              │    │
│  └──────────────────────────────┘    │
│                                      │
│  [VALIDATE]  [SAVE]  [CANCEL]        │
│                                      │
│  Status: ✓ Valid deck (52 cards)     │  (or list errors)
│                                      │
└──────────────────────────────────────┘
6.6 Permission Handling Screen
On first launch, if permissions not granted:

Show a simple explanation: "This app needs microphone access to listen for trigger phrases and vibration access for haptic feedback."
Button: "Grant Permissions"
If denied, disable the Start button and show: "Microphone permission required. Tap to open Settings."
⚡ SECTION 7: PERFORMANCE TARGETS
Metric	Target
Time from end of speech to parser output	< 1000ms
Time from parser output to first vibration pulse	< 500ms
Total: end of speech → first vibration	< 1500ms
Max vibration duration (Normal speed)	≤ 2500ms
Max vibration duration (Custom speed)	≤ 4000ms
Debounce window	3000ms (configurable)
Service restart after kill	< 2000ms
Memory usage (including Vosk model)	< 150MB
Continuous operation	≥ 2 hours on 3000mAh battery
🛡️ SECTION 8: ERROR HANDLING
Scenario	Required Behavior
Vosk model fails to load	Show error on Control tab, disable Start button
Vosk model not found in assets	Show clear error: "Speech model missing from assets"
AudioRecord fails to init	Retry 3 times with 1-sec delay; show error if all fail
Recognition returns empty text	Ignore, continue listening
Trigger pattern matched but no valid number	Discard silently, continue listening
Position out of bounds (0, 53, etc.)	Discard silently, continue listening
Vibrator hardware not available	Log warning; show note in Test tab; don't crash
Service killed by OS	START_STICKY auto-restarts; resume listening
Permission revoked while running	Stop service gracefully; on next app open, re-request
Custom deck validation fails	Show specific errors; don't save; keep old deck
Custom haptic timing validation fails (>4000ms worst case)	Show error; don't save
Golden rule: The app must NEVER crash during a performance. Every error → log + continue.

📁 SECTION 9: COMPLETE PROJECT STRUCTURE
text
app/
├── src/main/
│   ├── java/com/magic/haptic/
│   │   ├── MagicApp.kt                          // Application class; create notification channel
│   │   │
│   │   ├── data/
│   │   │   ├── Models.kt                        // All data classes & enums
│   │   │   ├── ServiceEventBus.kt               // Singleton StateFlow/SharedFlow bus
│   │   │   └── AppDataStore.kt                  // DataStore wrapper for all settings
│   │   │
│   │   ├── card/
│   │   │   ├── CardRepository.kt                // Deck storage + lookup
│   │   │   ├── DeckPresets.kt                   // Default, Mnemonica, Aronson
│   │   │   └── DeckValidator.kt                 // Validate 52-card deck
│   │   │
│   │   ├── parser/
│   │   │   ├── TriggerParser.kt                 // Regex matching + debounce
│   │   │   └── NumberWordConverter.kt            // "twenty three" → 23
│   │   │
│   │   ├── haptic/
│   │   │   ├── HapticEncoder.kt                 // Card string → vibration pattern
│   │   │   └── HapticPlayer.kt                  // Execute vibration on hardware
│   │   │
│   │   ├── speech/
│   │   │   └── VoskRecognizerManager.kt         // Vosk init, AudioRecord, streaming loop
│   │   │
│   │   ├── service/
│   │   │   ├── AudioListenerService.kt          // Foreground service; wires everything
│   │   │   └── NotificationHelper.kt            // Channel creation + notification builder
│   │   │
│   │   └── ui/
│   │       ├── MainActivity.kt                  // ViewPager2 + TabLayout + permissions
│   │       ├── ControlFragment.kt               // Start/stop, status, session info
│   │       ├── TestFragment.kt                  // Manual testing + speech log
│   │       ├── SettingsFragment.kt              // All configuration
│   │       └── SpeechLogAdapter.kt              // RecyclerView adapter for speech log
│   │
│   ├── assets/
│   │   └── model-en-us/                         // ← Vosk model files go here
│   │       ├── am/
│   │       ├── conf/
│   │       ├── graph/
│   │       ├── ivector/
│   │       └── ...
│   │
│   ├── res/
│   │   ├── layout/
│   │   │   ├── activity_main.xml
│   │   │   ├── fragment_control.xml
│   │   │   ├── fragment_test.xml
│   │   │   └── fragment_settings.xml
│   │   ├── values/
│   │   │   ├── strings.xml
│   │   │   ├── colors.xml
│   │   │   └── themes.xml
│   │   ├── drawable/                            // Status icons, etc.
│   │   └── mipmap/                              // App icon
│   │
│   └── AndroidManifest.xml
│
├── src/test/java/com/magic/haptic/
│   ├── HapticEncoderTest.kt                     // All 52 cards produce unique patterns
│   ├── TriggerParserTest.kt                     // All 6 patterns + negative cases
│   ├── NumberWordConverterTest.kt               // All numbers 1-52 in all formats
│   ├── CardRepositoryTest.kt                    // Bounds checking
│   └── DeckValidatorTest.kt                     // Valid/invalid deck inputs
│
└── build.gradle.kts
🧪 SECTION 10: REQUIRED UNIT TESTS
10.1 HapticEncoderTest
text
- testAllRanksProduceUniquePatterns()           // 13 ranks → 13 distinct patterns
- testAllSuitsProduceUniquePatterns()           // 4 suits → 4 distinct patterns
- testAll52CardsProduceUniqueFullPatterns()     // 52 cards → 52 distinct patterns
- testAceOfSpadesEncoding()                     // Shortest: S | SEP | L
- testKingOfClubsEncoding()                     // Longest: L L L | SEP | L S
- testQueenIsNotSameAsSix()                     // Q = S S L ≠ 6 = L S
- testTenOfDiamondsEncoding()                   // Two-char rank: L L | SEP | S L
- testPatternDurationWithinLimit()              // All cards ≤ 2500ms at Normal speed
- testFastSpeedDurations()                      // All cards ≤ 1600ms at Fast
- testInvalidCardReturnsNull()                  // "XX" → null
10.2 TriggerParserTest
text
- testCardAtPositionPattern()                   // "card at position five" → 5
- testPositionXCardPattern()                    // "position twelve card" → 12
- testXthPositionPattern()                      // "twenty third position" → 23
- testPositionNumberPattern()                   // "position number forty two" → 42
- testCardNumberPattern()                       // "card number seven" → 7
- testNumberXCardPattern()                      // "number twelve card" → 12
- testDigitExtraction()                         // "card at position 23" → 23
- testOrdinalDigit()                            // "5th position" → 5
- testBoundsRejection()                         // "card at position 0" → null
- testUpperBoundsRejection()                    // "card at position 53" → null
- testNoPatternMatch()                          // "I have cards" → null
- testDebounce()                                // Two calls within 3s → second returns null
- testRandomChatter()                           // "hello everyone" → null
10.3 NumberWordConverterTest
text
- testDigits1Through52()                        // "1" through "52" → 1–52
- testWordsSingle()                             // "one" through "nineteen" → 1–19
- testWordsCompound()                           // "twenty one" through "fifty two"
- testOrdinalWords()                            // "first" through "fifty second"
- testOrdinalDigits()                           // "1st", "2nd", "3rd", ..., "52nd"
- testHyphenated()                              // "twenty-three" → 23
- testInvalidReturnsNull()                      // "banana" → null
- testEmptyReturnsNull()                        // "" → null
10.4 DeckValidatorTest
text
- testValidDefaultDeck()                        // 52 unique cards → valid
- testDuplicateCard()                           // 51 unique + 1 duplicate → invalid
- testMissingCard()                             // 51 cards → invalid
- test53Cards()                                 // 53 cards → invalid
- testInvalidCardString()                       // "XY" → invalid
- testEmptyString()                             // "" → invalid
🔍 SECTION 11: KNOWN PLATFORM LIMITATIONS
Document these in a README or in-app "About" section. These are not bugs:

Limitation	Explanation
🟢 Green dot on Android 12+	OS shows a microphone indicator in status bar. Cannot be disabled. Phone should be in pocket so this is acceptable.
🔔 Persistent notification	Foreground services require a notification. We disguise it with configurable title/body.
🔋 Battery usage	Continuous mic listening uses significant battery. Expect ~10-15% per hour. App should be started only when needed.
🗣️ Recognition accuracy	Vosk small model is ~85-90% accurate in quiet environments. Loud venues may reduce accuracy. Speak trigger phrases clearly and at moderate volume.
📱 OEM battery optimization	Some manufacturers (Samsung, Xiaomi, Huawei) aggressively kill background services. User may need to disable battery optimization for this app in system settings.
✅ SECTION 12: DEFINITION OF DONE
All of the following must be true before the build is considered complete:

Functional
 App installs and runs from a single APK with no network
 Vosk model loads from bundled assets
 Continuous listening works with screen off for 30+ minutes
 All 6 trigger patterns correctly recognized in unit tests
 Numbers 1–52 parsed correctly in digit, word, and ordinal form
 All 52 cards produce unique vibration patterns (automated test)
 Queen (S S L) does NOT collide with Six (L S)
 Test mode allows manual position input → shows card → vibrates
 Quick test grid vibrates correct patterns for all 8 edge-case cards
 Speech log displays in Test tab with match highlighting
 Three deck presets load correctly
 Custom deck validates and persists across restarts
 Speed presets change vibration timing
 Custom speed validates worst-case ≤ 4000ms
 Debounce prevents duplicate triggers within configured window
 Notification shows disguised text
Non-Functional
 No audio output during listening (manual QA)
 No visual output during listening (manual QA)
 Vibration clearly perceptible in trouser pocket (manual QA)
 End-to-end latency < 1.5 seconds
 Service survives screen off for 30+ min
 Service restarts after being killed (START_STICKY)
 All permissions handled gracefully (grant, deny, revoke)
 App never crashes (fuzz test with random inputs)
 Works in airplane mode from first install
Code Quality
 All Kotlin code has KDoc comments on public functions
 All unit tests pass
 No compiler warnings
 No lint errors (severity: error)
 Clean build with no dependency conflicts
📋 SECTION 13: SUGGESTED SPRINT PLAN
Day	Focus	Deliverables
1	Project scaffold + permissions	Gradle setup, manifest, MagicApp, MainActivity shell with ViewPager2, permission flow, NotificationHelper
2	Vosk integration	Asset bundling, model copy to internal storage, VoskRecognizerManager with AudioRecord loop, verify text output in logcat
3	Parser + number converter	NumberWordConverter (all 52 values), TriggerParser (all 6 patterns), unit tests for both
4	Card system + haptic engine	CardRepository, DeckPresets, DeckValidator, HapticEncoder, HapticPlayer, unit tests for all
5	Wire service end-to-end	AudioListenerService connects all components, ServiceEventBus, test full flow: speak → vibrate
6	UI: Control + Test tabs	ControlFragment (status, start/stop, session info), TestFragment (manual input, quick test grid, speech log)
7	UI: Settings + polish	SettingsFragment (deck, speed, notification, debounce), custom deck editor, edge case testing, QA
📎 SECTION 14: DEPENDENCIES (Exact Versions)
kotlin
// app/build.gradle.kts dependencies block

// Vosk offline speech recognition
implementation("com.alphacephei:vosk-android:0.3.47")
implementation("net.java.dev.jna:jna:5.13.0@aar")

// Jetpack core
implementation("androidx.core:core-ktx:1.12.0")
implementation("androidx.appcompat:appcompat:1.6.1")
implementation("androidx.fragment:fragment-ktx:1.6.2")
implementation("androidx.lifecycle:lifecycle-service:2.7.0")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
implementation("androidx.datastore:datastore-preferences:1.0.0")

// UI
implementation("com.google.android.material:material:1.11.0")
implementation("androidx.viewpager2:viewpager2:1.0.0")
implementation("androidx.recyclerview:recyclerview:1.3.2")
implementation("androidx.constraintlayout:constraintlayout:2.1.4")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

// Testing
testImplementation("junit:junit:4.13.2")
testImplementation("com.google.truth:truth:1.1.5")
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
androidTestImplementation("androidx.test.ext:junit:1.1.5")
Vosk Maven repository (must be in settings.gradle.kts):

kotlin
maven { url = uri("https://alphacephei.com/maven/") }
🚫 SECTION 15: WHAT NOT TO BUILD
Do NOT	Reason
Cloud speech APIs (Google, AWS, Azure)	Offline constraint
Android built-in SpeechRecognizer	Unreliable for continuous listening
Text-to-speech or audio feedback	Stealth constraint
Screen wake or visual alerts during performance	Stealth constraint
Network calls of any kind	Offline constraint
Complex multi-activity navigation	Over-engineering
Room database	Overkill; DataStore is sufficient
Bluetooth or wearable integration	Out of scope
Multiple language support	English only for v2
Widget or quick-settings tile	Out of scope
💬 FINAL NOTES FOR THE DEV TEAM
Test on a real device. Vibration cannot be tested on emulators. Use a physical Android phone.
Test in a noisy room. The Vosk small model handles quiet environments well but degrades in noise. This is expected.
The magician's workflow: Open app → configure deck once → tap Start → lock phone → put in pocket → perform. The app must survive this entire flow.
When in doubt, fail silently. Never interrupt the magician's performance with an error message or sound.
The haptic patterns are the core product. Spend extra time making them crisp and distinguishable. Test every card.
This document is the single source of truth. Build exactly this. Ship it as one APK.