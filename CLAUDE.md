# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

See @README.md for project overview.

## Commands

```bash
# Build
./gradlew assembleDebug
./gradlew assembleRelease

# Unit tests (JVM)
./gradlew testDebugUnitTest
./gradlew test --tests "*ExampleUnitTest*"   # single test

# Instrumented tests (requires device/emulator)
./gradlew connectedDebugAndroidTest

# Lint
./gradlew lintDebug

# Clean
./gradlew clean
```

## Architecture

This is a fresh **single-module** Android app (`app/`) using:

- **Jetpack Compose** for all UI (no XML layouts)
- **Material Design 3** with dynamic color (Android 12+) and dark/light theme support
- **Single Activity** pattern — `MainActivity` is the sole entry point

### Package structure

`br.com.suinogestor`

- `MainActivity.kt` — entry point, hosts Compose content
- `ui/theme/` — `Theme.kt`, `Color.kt`, `Type.kt` (Material 3 theming)
- `src/test/` — JVM unit tests (JUnit 4)
- `src/androidTest/` — instrumented tests (Espresso + Compose test)

### Dependency management

All versions are centralized in `gradle/libs.versions.toml`. Use the version catalog accessors (`libs.*`) when adding dependencies — never hardcode version strings in build files.

### Key versions

| Tool | Version |
|------|---------|
| Min SDK | 24 (Android 7.0) |
| Target/Compile SDK | 36 |
| Kotlin | 2.2.10 |
| AGP | 9.1.0 |
| Compose BOM | 2024.09.00 |
| Java source compatibility | 11 |
