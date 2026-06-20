# MelodyFlow 🎵

MelodyFlow is a premium, modern local music player for Android, designed with Material 3 (Material You) guidelines. It scans the device's local storage to organize audio files by songs, albums, and artists, and features robust background audio playback powered by Jetpack Media3.

## 🚀 Features

-   **Background Audio Playback**: Fully managed playback utilizing AndroidX Media3 ExoPlayer and `MediaSessionService` for uninterrupted listening, lockscreen controls, and system notification integration.
-   **Local Storage Scanning**: Instantly loads and categorizes audio files from device storage using the Android MediaStore API, supporting the latest permissions models (API 33+ `READ_MEDIA_AUDIO`).
-   **Dynamic Theming (Material You)**: Supports dynamic color palettes on Android 12+ that match your wallpaper, alongside custom light and dark mode toggles.
-   **Smart Library Organization**: Automatically groups music into **Songs**, **Albums**, **Artists**, **Playlists**, and **Favourites**.
-   **Playlists & Favourites**: Built-in SQLite local database via Room ORM to create, rename, delete playlists, and bookmark your favourite tracks.
-   **Beautiful Interactive UI**: Features a gorgeous bottom sheet sliding player, sleek grid views, smooth micro-animations, and a blurred album art background in the Now Playing screen.
-   **Complete Audio Controls**: Support for Shuffle, Repeat modes, seeking, and responsive play/pause transitions.

---

## 🛠️ Tech Stack & Libraries

-   **Language**: Kotlin (2.1.0)
-   **UI Toolkit**: Jetpack Compose (BOM 2025.05.01)
-   **Design Language**: Material Design 3 (Material You)
-   **Media Playback**: AndroidX Media3 (1.6.0) (ExoPlayer & MediaSession)
-   **Dependency Injection**: Hilt (2.54) & Hilt Navigation Compose
-   **Local Database**: Room DB (2.6.1)
-   **Image Loading**: Coil (2.7.0) (for high-performance album art caching)
-   **Architecture**: MVVM + Clean Architecture / Repository Pattern
-   **Local Storage**: Jetpack DataStore (Preferences) for settings caching

---

## 🏗️ Architecture

MelodyFlow follows modern Android development best practices with a clean MVVM structure:

```
com.melodyflow.player
├── data
│   ├── local
│   │   ├── db (Room Entity, DAO, MelodyDatabase)
│   │   └── mediastore (MediaStoreDataSource for device queries)
│   ├── model (Domain/Data models: Song, Album, Artist)
│   └── repository (MusicRepository and MusicRepositoryImpl)
├── di (Hilt dependency injection modules: AppModule, DatabaseModule)
├── service (PlaybackService extending MediaSessionService)
└── ui
    ├── components (Reusable views: MiniPlayer, SongItem, PermissionHandler)
    ├── navigation (NavGraph, Screen destinations)
    ├── screens (Screen views & corresponding ViewModels)
    └── theme (Color, Type, Theme tokens)
```

-   **Playback Service**: Runs in the background as a foreground service with a `mediaPlayback` type. It manages the `ExoPlayer` instance and registers a `MediaSession`.
-   **PlayerViewModel**: Connected to the `PlaybackService` via a `MediaController`. It publishes the current playback state (progress, playing/paused state, active song, queue) to the UI components.
-   **MusicRepository**: Coordinates data retrieval from the local MediaStore and Room database tables.

---

## 📋 Requirements & Setup

### Requirements
-   **Android Studio** Ladybug (2024.2.1) or newer
-   **JDK**: Version 17
-   **Minimum SDK**: API 24 (Android 7.0)
-   **Target SDK**: API 35 (Android 15)

### Building the Project
1.  Clone this repository to your local machine.
2.  Open the project in Android Studio.
3.  Let Gradle sync finish.
4.  Compile and run the app:
    -   To build from the command line:
        ```bash
        ./gradlew assembleDebug
        ```
    -   To install the debug app on a connected device:
        ```bash
        ./gradlew installDebug
        ```

---

## 🔐 Permissions
The application requests the following permissions depending on the Android API version:
-   `android.permission.READ_MEDIA_AUDIO` (Android 13 / API 33+)
-   `android.permission.READ_EXTERNAL_STORAGE` (Android 12 and below / maxSdkVersion 32)
-   `android.permission.FOREGROUND_SERVICE` and `android.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK` (API 34+ for background play support)
-   `android.permission.POST_NOTIFICATIONS` (Android 13+ to display the media notification control)
