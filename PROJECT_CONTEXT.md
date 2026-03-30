# Project Context: QuranApp

## Purpose
QuranApp is a comprehensive Android application designed to provide users with a rich experience for interacting with the Quran, Hadith, Adhkar, and other Islamic tools (Prayer times, Qibla, Tasbih). It focuses on data accuracy, clean UI, and offline accessibility.

## Key Features
- **Holy Quran**: Surah listing, Ayah reading, and audio recitations (api.alquran.cloud).
- **Hadith & Bookmarks**: Bookmarking specific Ayat and Hadiths for later reference.
- **Prayer & Qibla**: Accurate prayer times (api.aladhan.com) and Qibla direction tracking.
- **Adhkar & Tasbih**: Daily remembrances and a digital Tasbih counter.
- **User Progress**: Tracking reading progress and spiritual goals.
- **Offline Mode**: Local caching of Quranic data using Room database.

## Technical Summary
- **Platform**: Android (Native)
- **Primary Language**: Kotlin
- **Building System**: Gradle (Kotlin DSL)
- **UI Framework**: XML Layouts (primarily) with Hilt for dependency injection.
- **Architecture**: Domain-driven Clean Architecture.

## Key Dependencies
- **Hilt**: Dependency Injection.
- **Room**: Local data storage.
- **Retrofit**: Network communication.
- **OkHttp**: HTTP client and logging.
- **Google Location Services**: Qibla and Prayer times.
- **Coroutines & Flow**: Reactive data streams and concurrency.

## Success Criteria for AI
- Always maintain architectural purity (don't mix layers).
- Don't reinvent common modules; use `AppModule.kt` for DI.
- Keep the UI responsive and handle network errors gracefully.
- Follow the `.cursorrules` for every task.
