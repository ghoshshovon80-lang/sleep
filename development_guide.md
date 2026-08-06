# Sleep Dev Guide
This file outlines the process of setting up a local dev environment for Sleep.

## Prerequisites

- JDK 21
- Android platform tools (if you don't have a keystore already)
- protobuf-compiler v3.21 or newer

## Basic setup

This has been tested on Windows and Linux.

```bash
git clone https://github.com/ghoshshovon80/Sleep
cd Sleep
[ ! -f "app/persistent-debug.keystore" ] && keytool -genkeypair -v -keystore app/persistent-debug.keystore -storepass android -keypass android -alias androiddebugkey -keyalg RSA -keysize 2048 -validity 10000 -dname "CN=Android Debug,O=Android,C=US" || echo "Keystore already exists."
./gradlew :app:assembleFossDebug
ls app/build/outputs/apk/foss/debug/app-foss-debug.apk
```
