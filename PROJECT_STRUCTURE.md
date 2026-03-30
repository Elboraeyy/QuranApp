# Project Structure: QuranApp

This file provides a clear overview of the project structure to help AI assistants locate files efficiently.

## Root Directory
- `.cursorrules`: Core AI rules and post-task checklist.
- `PROJECT_CONTEXT.md`: Project overview and tech stack.
- `ARCHITECTURE.md`: Detailed architecture documentation.
- `PROJECT_STRUCTURE.md`: This file.
- `gradle/`, `build.gradle.kts`, `settings.gradle.kts`: Gradle configuration files.
- `*.py`: Data processing scripts for Quran, Hadith, and Adhkar.

## App Module (`app/src/main/`)
- `AndroidManifest.xml`: Android application manifest.
- `assets/`: Static assets (SVG, icons, etc.).
- `res/`: Android resources (layouts, drawables, strings, etc.).
- `java/com/example/quranapp/`: Main source code.

### Source Code Breakdown (`com/example/quranapp/`)
- `di/`: Hilt Dependency Injection modules (`AppModule.kt`).
- `data/`:
  - `local/`: Room Database (`QuranDatabase.kt`), DAOs, and Entities.
  - `remote/`: Retrofit API interfaces and DTOs.
  - `repository/`: Concrete implementations of domain repository interfaces.
  - `manager/`: Manager implementations (e.g., `AudioPlayerManagerImpl`).
  - `location/`, `qibla/`: Location and Qibla tracking implementations.
  - `alarm/`: Alarm and notification handling.
- `domain/`:
  - `model/`: Plain Kotlin models for business logic.
  - `repository/`: Repository interfaces.
  - `manager/`: Manager interfaces.
- `presentation/`:
  - `viewmodel/`: ViewModels for state management and logic.
  - `ui/`: Activities, Fragments, and custom UI components.
  - `navigation/`: Navigation logic and destination definitions.
- `util/` / `utils/`: Common utilities and extension functions.

## AI Tip: Use this file to find the correct directory before making any changes.
