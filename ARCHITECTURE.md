# Architecture Guide: QuranApp Clean Architecture

QuranApp follows a Domain-Driven Clean Architecture approach to ensure separation of concerns, testability, and scalability.

## 1. Domain Layer (`domain/`)
The core of the application. It contains business logic and is independent of any other layer.
- **Models**: Business logic representation of entities (e.g., `Ayah`, `Surah`).
- **Repositories Interfaces**: Abstract definitions of data operations.
- **UseCases (Optional)**: Specific business rules that combine repositories or other domain entities.

## 2. Data Layer (`data/`)
Handles all data retrieval from local and remote sources and maps them to domain models.
- **Local**: Room DAOs, Entities, and Database (`QuranDatabase`).
- **Remote**: Retrofit API interfaces and DTOs.
- **Repositories Implementations**: Concrete implementation of domain repository interfaces (e.g., `QuranRepositoryImpl`).
- **Mappers**: Functions to convert between DTOs, Entities, and Domain Models.

## 3. Presentation Layer (`presentation/`)
Responsible for UI rendering and handling user interactions.
- **ViewModels**: Manage UI state and interact with the Domain layer.
- **UI (Activities/Fragments)**: Render data from ViewModels and send user events.
- **UI Models**: Specialized models for UI rendering to avoid exposing internal entities.

## 4. Dependency Injection (`di/`)
Uses **Hilt** to provide dependencies across all layers.
- `AppModule.kt`: Defines how to provide Singleton dependencies like `QuranDatabase`, `QuranApi`, and repository implementations.

## Rules for AI
- **No Direct Access**: Data layer should NEVER be accessed directly from the UI. Always go through the Domain layer.
- **Purity**: Domain models should not contain Any Room or Retrofit annotations.
- **Mapping**: Data layer MUST map entities to domain models before passing them to the domain/presentation layer.
- **State Management**: Use `StateFlow` or `LiveData` in ViewModels to expose UI state.
