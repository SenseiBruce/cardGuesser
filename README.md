# Magic Haptic Assistant 🎩✨

A premium, stealthy tool designed for magicians to discreetly receive information about a spectator's chosen card via haptic feedback.

## 🌟 Master Features
- **Stealth Mode**: Dark obsidian UI with gold accents, optimized for low visibility during performance.
- **Offline Intelligence**: Uses Vosk Speech Recognition for total privacy and zero latency.
- **Precision Haptics**: Encodes all 52 cards into unique, tactile vibration patterns.
- **Customizable Stacks**: Built-in support for popular stacks (Mnemonica, Aronson) and full custom stack editing.
- **Encrypted Logs**: Review your performance and detected phrases in a secure toolkit log.

## 🛠 Setup Instructions

### 1. Mandatory Assets
Due to size constraints, you must manually add the Speech Recognition Model:
1. Download the `vosk-model-small-en-us-0.15` (or latest) from [Vosk Models](https://alphacephei.com/vosk/models).
2. Unzip and place the contents exactly in:
   `app/src/main/assets/model-en-us/`
   *(Ensure files like `am/final.mdl`, `graph/HCLG.fst`, etc. are directly in that folder).*

### 2. Permissions
Ensure the following are granted:
- **Record Audio**: To listen for position triggers.
- **Notifications**: To maintain the stealthy foreground service.
- **Vibrate**: To deliver the secret signals.

## 📜 The Magician's Code (Haptic Guide)

The assistant uses two types of pulses: **S** (Short) and **L** (Long).

### Ranks (Numbers)
- **1**: S
- **2**: SS
- **3**: SSS
- **4**: SL
- **5**: L
- **6**: LS
- **7**: LSS
- **8**: LSSS
- **9**: LSL
- **10**: LL
- **J**: LLS
- **Q**: LLSS
- **K**: LLSSS

### Suits
- **Hearts**: S (1 Short)
- **Diamonds**: SS (2 Shorts)
- **Clubs**: SSS (3 Shorts)
- **Spades**: L (1 Long)

*Example: "Queen of Spades" = [LLSS] (Gap) [L]*

## 🕵️ Stealth Tips
1. Use the **Notification Disguise** setting to rename the active task to something generic like "System Optimization".
2. Test your haptic sensitivity in the **Toolkit** tab before going on stage.
3. Keep the device in a pocket where the vibration is felt clearly but not heard.

---
*Built for the Modern Mystery Performer.*