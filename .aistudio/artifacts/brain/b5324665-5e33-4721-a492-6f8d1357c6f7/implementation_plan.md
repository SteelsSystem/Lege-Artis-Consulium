# Lege Artis Consulium — Gradle Wrapper & APK Assembly Plan

This plan adds portable local Linux build tooling (`gradlew` and a one-step `build-apk.sh` helper) to the **Lege Artis Consulium** workspace and runs the full `:app:assembleDebug` APK verification build while preserving the existing 4-tab Bio-Legal NGS & Q-MAX Cypher UI.

---

### User Review & Critical Decisions

> [!IMPORTANT]
> Confirm the choices below to proceed with generating the Gradle wrapper and executing the APK assembly build.

- **Confirmed Decision 1 — Generate `gradlew` Wrapper & Run `assembleDebug` Here**: We will generate the standard Gradle Wrapper files (`gradlew`, `gradlew.bat`, and `gradle-wrapper.jar`) alongside a convenient `build-apk.sh` script that automatically decodes `debug.keystore.base64` into `debug.keystore` and builds the signed APK.
- **Confirmed Decision 2 — Preserve Current 4-Tab Bio-Legal NGS & Q-MAX UI**: The existing 4-workspace layout (**NGS Lab**, **Q-MAX Cypher**, **MAP & AI**, **Portal & Audit**) remains untouched.

---

### 1. Overview & Core Concept

- **What It Does**:
  1. Generates a self-contained **Gradle Wrapper (`./gradlew`)** and a Linux build script (`./build-apk.sh`) so that when you export/download the project ZIP to `~/Stažené/Lege-Artis-Consulium-main`, you can build the APK even if a global `gradle` binary is not installed on your system `PATH`.
  2. Executes `gradle :app:assembleDebug` and `compile_applet` inside the cloud build environment to verify the signed APK output (`app/build/outputs/apk/debug/app-debug.apk`).
  3. Updates `README.md` with exact step-by-step instructions for both `./gradlew :app:assembleDebug` and downloading the pre-built APK directly from Google AI Studio.

---

### 2. User Experience & Visual Design

- **Key User Flows**:
  - **In-App Experience**: Unchanged—retains the microscope-safe Dark/Light theme (`#090D14` / `#F4F7FA`), 3D molecular helix, chromatogram peak loupe, Volcano threshold curtain, 128-bit Q-MAX cypher matrix, W4 Task A–B–C metasimulator, Multi-Model Gemini Studio, and Consulting Portal.
  - **Local Linux Terminal Flow**:
    - Run `chmod +x gradlew build-apk.sh && ./build-apk.sh` from the extracted project folder.

---

### 3. Key Product Decisions & Trade-Offs

- **Decision 1: Self-Bootstrapping `gradlew` + `build-apk.sh`**
  - *Chosen Approach*: Provide both the standard `gradlew` wrapper generated via `gradle wrapper` and a resilient `build-apk.sh` script that checks for `debug.keystore` (decoding `debug.keystore.base64` if missing), verifies `ANDROID_HOME` / `ANDROID_SDK_ROOT`, and invokes `./gradlew :app:assembleDebug`.
  - *Why*: Prevents common local build errors when transitioning from AI Studio's cloud container to a local Linux desktop environment.

---

### 4. Technical Architecture & Data Strategy *(Technical Reference)*

```
┌──────────────────────────────────────────────────────────────────────┐
│              Local & Cloud APK Build Pipeline                        │
│  1. Decode Keystore: base64 -d debug.keystore.base64 > debug.keystore│
│  2. Gradle Wrapper:  ./gradlew :app:assembleDebug                    │
│  3. Signed Output:   app/build/outputs/apk/debug/app-debug.apk       │
└──────────────────────────────────────────────────────────────────────┘
```
