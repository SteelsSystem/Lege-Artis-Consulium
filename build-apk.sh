#!/usr/bin/env bash
set -euo pipefail

echo "=== Lege Artis Consulium — Local APK Builder ==="

# 1. Decode debug.keystore from debug.keystore.base64 if not present
if [ ! -f "debug.keystore" ] && [ -f "debug.keystore.base64" ]; then
    echo "[1/3] Decoding debug.keystore from debug.keystore.base64..."
    base64 -d debug.keystore.base64 > debug.keystore
else
    echo "[1/3] debug.keystore already present."
fi

# 2. Select Gradle executable (prefer ./gradlew if executable, else system gradle)
if [ -x "./gradlew" ]; then
    GRADLE_CMD="./gradlew"
elif command -v gradle >/dev/null 2>&1; then
    GRADLE_CMD="gradle"
else
    echo "Error: Neither ./gradlew nor system 'gradle' was found."
    exit 1
fi

echo "[2/3] Running $GRADLE_CMD :app:assembleDebug..."
"$GRADLE_CMD" :app:assembleDebug

echo "[3/3] Build complete!"
echo "Signed Debug APK: app/build/outputs/apk/debug/app-debug.apk"
