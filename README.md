# Magic Haptic Assistant 🎩📳

**Magic Haptic Assistant** is an Android application designed for stage magicians to discreetly identify cards or positions through haptic feedback (vibration) triggered by natural spoken phrases.

## 🚀 Overview

The app listens continuously in the background using the **Vosk offline speech recognition engine**. When it hears a pre-defined trigger phrase (e.g., *"the card at position twenty three"*), it translates the spoken position into a specific card identity based on a pre-memorized deck order and vibrates a coded pattern that the magician can feel in their pocket.

## ✨ Key Features

- **100% Offline**: No internet required. Ships with a bundled Vosk English model.
- **Stealth Mode**: Zero audio or visual output during performance. Works with the screen off and phone in pocket.
- **Customizable Decks**: Support for Default, Mnemonica (Tamariz), Aronson, and custom user-defined card orders.
- **Haptic Encoding**: Unique, distinguishable vibration patterns for all 52 cards (Rank + Suit).
- **Disguised Notification**: Uses a configurable, non-suspicious notification title/body for the foreground service.
- **Adjustable Speed**: Fast, Normal, and Slow haptic pulse presets, plus custom timing validation.

## 🏗️ Architecture

The app is built using **Kotlin** and follows modern Android best practices:
- **MVVM Pattern**: For clean separation of UI and business logic.
- **Foreground Service**: Ensures continuous microphone listening even when the app is in the background or the screen is off.
- **Jetpack DataStore**: For persistent preferences and configuration.
- **Vosk Engine**: Dedicated background thread for real-time PCM audio processing.

## 📂 Documentation

- **[Requirements.md](file:///Users/kinshuk.prasad/Documents/Project_X/cardGuesser/Requirement.md)**: High-level design and feature specifications.
- **[LLD.md](file:///Users/kinshuk.prasad/Documents/Project_X/cardGuesser/LLD.md)**: Low-level design, class diagrams, and sequence flows.
- **[business_logic.md](file:///Users/kinshuk.prasad/Documents/Project_X/cardGuesser/business_logic.md)**: Visual flowchart of the application logic and haptic encoding.

## 🛠️ Build & Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/SenseiBruce/cardGuesser.git
   ```
2. **Setup Vosk Model**:
   - Download the `vosk-model-small-en-us` from [Alphacephei](https://alphacephei.com/vosk/models).
   - Unpack the contents into `app/src/main/assets/model-en-us/`.
3. **Build the APK**:
   - Open in Android Studio or run:
   ```bash
   ./gradlew assembleDebug
   ```
4. **Permissions**:
   - Grant `RECORD_AUDIO` and `Vibrate` permissions on first launch.

## 🧪 Testing

The project includes unit tests for core logic:
- `HapticEncoderTest`: Verifies unique waveforms for all 52 cards.
- `ParserTest`: Validates regex trigger phrase matching and number word conversion.

## 📜 Section 15: What NOT to build
- This app does NOT use any cloud APIs.
- It does NOT provide audio confirmation (beeps/TTS).
- It is intended for professional use by magicians who require extreme discretion.

---
*Created by Antigravity - Advanced Agentic Coding.*