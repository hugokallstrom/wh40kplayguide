# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A Kotlin-based Warhammer bot project using Gradle as the build system.

## Build Commands

```bash
# Build the project
./gradlew build

# Run the application
./gradlew run

# Run all tests
./gradlew test

# Run a specific test class
./gradlew test --tests "ClassName"

# Run a specific test method
./gradlew test --tests "ClassName.methodName"

# Clean build artifacts
./gradlew clean
```

## Technical Stack

- **Language:** Kotlin 2.2.20
- **JVM:** Java 19
- **Build System:** Gradle with Kotlin DSL
- **Testing:** JUnit Platform (via `kotlin("test")`)

## Project Structure

- `src/main/kotlin/` - Main Kotlin source files
- `src/test/kotlin/` - Test source files
- `build.gradle.kts` - Build configuration
- `settings.gradle.kts` - Project settings
- `core_rules.txt` - Extracted text from Warhammer 40K Core Rules PDF