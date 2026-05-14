# Farm Manager

Android app for small-scale farmers to track **agriculture expenses**, **harvest**, **sales**, and **profit/loss per crop**—fully **offline** with a local database. The UI is built with **Jetpack Compose** and **Material Design 3**.

**Repository:** [github.com/velsans/farms](https://github.com/velsans/farms)

## Features

- **Agri module**
  - **Dashboard** with month/year filters and profit/loss views
  - **Crops** — sowing details, variety, field, area, season
  - **Expenses** — categories (seed, fertilizer, labor, water irrigation, harvest management, etc.)
  - **Harvest** — quantities and notes
  - **Sales** — price per kg, income, buyer details
  - **Reports** and share/export workflows
- **Excel import & export** (Apache POI); merge import avoids wiping existing data when possible
- **Share** exports via system sheet (e.g. WhatsApp) using `FileProvider`
- **Module tabs** — Agri (active); **Goat** and **Chicken** placeholders for future expansion
- **Back navigation** — from sub-screens returns to the Agri dashboard; from dashboard, back prompts to exit the app
- **Adaptive launcher icon** with safe-zone foreground

## Tech stack

| Area | Technology |
|------|------------|
| UI | Jetpack Compose, Material 3 |
| Architecture | MVI-style intents/effects, `FarmViewModel` |
| DI | Hilt |
| Database | Room (Kotlin + coroutines / Flow) |
| Excel | Apache POI (`poi-ooxml`) |
| Language | Kotlin **2.0**, Java **17** |

## Requirements

- **Android Studio** (recommended) or compatible Gradle + Android SDK
- **JDK 17** (Android Studio’s bundled JBR is fine)
- **compileSdk 35**, **minSdk 26**, **targetSdk 35**

## Build & run

From the project root:

```bash
./gradlew assembleDebug
```

Install the debug APK on a device or emulator, or use **Run** in Android Studio.

If `java` is not on your `PATH`, point Gradle at the IDE JDK, for example:

```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./gradlew assembleDebug
```

## Project layout (overview)

```
app/src/main/java/com/farmmanager/
├── MainActivity.kt          # Compose UI, navigation, dialogs, back handling
├── FarmViewModel.kt         # State, intents, repository + Excel use cases
├── FarmMvi.kt               # FarmIntent / FarmEffect definitions
├── di/AppModule.kt          # Hilt: DB, DAO, repository, Excel manager
├── data/                    # Room entities, DAO, repository
├── export/                  # Excel import/export
└── ui/                      # Theme, Agri submenu screens
```

Strings and dimensions live under `app/src/main/res/values/` (`strings.xml`, `dimens.xml`).

## License

No license file is bundled in this repository. Add one (for example MIT or Apache-2.0) if you intend to distribute or accept contributions.
