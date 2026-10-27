# Lege Artis Consulium — Bio-Legal NGS & Q-MAX Offline Cypher Suite

**Lege Artis Consulium** (`com.aistudio.legeartis.ngsbio`) is an Android bioinformatics, cryptographic security, and medico-legal consulting application built with **Kotlin**, **Jetpack Compose (Material 3)**, **Room SQLite (KSP)**, and **Firebase AI Logic (Gemini API)**.

---

## 1. Architecture & Core Modules

### A. NGS Genomic Analysis Workspace (`NGS Lab`)
- **3D Molecular Structure & Locus Sync**: Hardware-accelerated 3D double-helix canvas supporting touch rotation (yaw/pitch) and spring-animated locus synchronization when selecting variants from the NGS table.
- **Base-Call Chromatogram & Phred Magnification Loupe**: Interactive A/T/C/G nucleotide trace viewer with drag/tap scrubbing, local peak magnification, and Phred quality zone shading ($Q \ge 30$ clinical confidence vs. $Q < 20$ sequencing artifacts).
- **Volcano Distribution Plot & Live Threshold Curtain**: Interactive $-\log_{10}(p)$ vs. Variant Allele Frequency (VAF) scatter plot with a live threshold curtain controlled by filter sliders.
- **Sample QC Telemetry & Lege Artis Compliance Calculator**: Combines Q30 score, read depth coverage, and Ts/Tv ratio with medico-legal compliance switches (*GDPR & Informed Consent*, *Dual Bio-Legal Sign-Off*, and *T=1 podání Single-Submission Lock*).

### B. Local Secure Q-MAX Cypher & Random Hashing Sync (`Q-MAX Cypher`)
- **Salted SHA-256/512 & AES-GCM Block Cypher (`QMaxCypherEngine`)**: Computes cryptographic hash chains, CRC32 frame checks, and authenticated local payload encryption.
- **Interactive 128-Bit Matrix (`8×16`) & Avalanche Inspector**: Allows toggling individual bits in the 128-bit matrix to inspect real-time SHA-256 Hamming diffusion (%) and tension-resilience scores.
- **Offline Room Database Persistence (`LegeArtisDatabase`)**: Stores deterministic binary sync packets (`QMAXv4|SLT:...|CRC:...|RES:...|HSH:...`) locally on-device.

### C. W4 Calibration Metasimulator & Multi-Model Gemini Studio (`MAP & AI`)
- **Tension Reinforcement Metasimulator (`Task A → B → C` MAP-Processing)**:
  - **Task A (Lookup)**: Indexes genomic locus telemetry and ACMG/AMP classification parameters.
  - **Task B (Research & Q-MAX Cypher)**: Balances adversarial sequencing tension against reinforcement learning policy rates.
  - **Task C (Define MAP-Processing)**: Emits a deterministic `T=1 podání` single-submission token and 4-step `Divide Reasoning` verification trail.
- **Multi-Model Gemini AI Logic (`AiCopilotViewModel`)**:
  - **High Thinking Mode**: `gemini-4-argon` configured with `ThinkingLevel.HIGH` for complex clinical and medico-legal reasoning.
  - **Google Search Grounding Mode**: `gemini-3.8-flash` with `Tool.googleSearch()` for live ClinVar, gnomAD, and regulatory lookup.
  - **Low-Latency Lab Mode**: `gemini-3.1-flash-lite` using `generateContentStream()` for rapid sub-second responses.
  - **Studio Visual Generator**: `gemini-3-pro-image-preview` supporting selectable **1K**, **2K**, and **4K** (`ImageSize.SIZE_1K`, `SIZE_2K`, `SIZE_4K`) molecular and report visual synthesis.
- **Stín v Kolonce — Semantic Institutional Audit**: Generates structured 5-part (`A–E`) bio-bureaucratic satire and semantic contradiction analyses.

### D. Consulting Dossiers, Encrypted Client Portals & RBAC (`Portal & Audit`)
- **Automated Version Tracking**: Commits semantic versions (`v1.0` → `v1.1` → `v1.2`) with immutable SHA-256 content hashes and changelogs.
- **Encrypted Client Access Portals**: Passphrase-gated client portals with AES-256-GCM / Q-MAX token verification.
- **Role-Based Permission Matrix (RBAC) & Live Activity Audit Log**: Granular role switching (*Lead Bioinformatician*, *Clinical Geneticist*, *Legal Counsel*, *Client Reviewer*) and real-time local Room activity logging.

---

## 2. Color Palette & Accessibility (Microscope-Safe Dual Mode)

| Token Role | Dark Mode (Microscope / Darkroom) | Light Mode (Clinical / Legal Review) |
| :--- | :--- | :--- |
| **Background** | `#090D14` (Deep Optical Obsidian) | `#F4F7FA` (Sterile Alabaster) |
| **Surface / Elevated** | `#111823` / `#1A2433` | `#FFFFFF` / `#EBF0F5` |
| **Primary Accent** | `#38BDF8` (Cerenkov Cyan) | `#0284C7` (Deep Cobalt) |
| **Secondary Accent** | `#818CF8` (Helix Indigo) | `#4F46E5` (Legal Ultramarine) |
| **Pathogenic Alert** | `#EF4444` (Crimson Pulse) | `#EF4444` (Crimson Pulse) |
| **Sequencing Artifact** | `#F59E0B` (Amber Warning) | `#F59E0B` (Amber Warning) |
| **Benign / Verified** | `#10B981` (Bio-Emerald) | `#059669` (Clinical Emerald) |

---

## 3. Compiling & Exporting the APK (Kotlin / Gradle — Not Flutter)

> **Important**: This is a **native Android project (Kotlin + Jetpack Compose + Gradle)**, so it uses `build.gradle.kts` rather than Flutter's `pubspec.yaml`. Do **not** use `flutter build apk`.

### Option A: One-Click APK Download in Google AI Studio (Fastest)
1. Click the **Settings (gear) / Export** icon in the top-right corner of Google AI Studio.
2. Select **Download APK** (or **Build APK / AAB**). AI Studio compiles and signs the APK directly in the cloud container without needing a local Android SDK installation.

### Option B: Building Locally on Linux (`~/Stažené/Lege-Artis-Consulium-main`)
Ensure you have **JDK 17+** and the **Android SDK** (`ANDROID_HOME` / `ANDROID_SDK_ROOT`) configured on your machine, then run either the included `build-apk.sh` script or `./gradlew`:

```bash
cd ~/Stažené/Lege-Artis-Consulium-main

# One-step build script (decodes debug.keystore and runs assembleDebug)
chmod +x gradlew build-apk.sh
./build-apk.sh
```

Or run the steps manually:

```bash
cd ~/Stažené/Lege-Artis-Consulium-main
[ -f debug.keystore ] || base64 -d debug.keystore.base64 > debug.keystore
chmod +x gradlew
./gradlew :app:assembleDebug
```

The installable APK will be generated at:
```text
app/build/outputs/apk/debug/app-debug.apk
```

