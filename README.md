# Android Jetpack Compose Project - Getting Started Guide

This guide will walk you through the steps to set up and run the Android Jetpack Compose project. Android Jetpack Compose is a modern UI toolkit for building native UIs for Android applications using a declarative syntax.

## Prerequisites

Before you begin, ensure you have the following prerequisites installed on your machine:

- [Android Studio](https://developer.android.com/studio) (recommended version: Giraffe+)
- Android SDK (API level 24+)
- Kotlin (usually bundled with Android Studio)

## Getting the Project

1. Clone the repository from the project's GitHub page using the following command:

   ```bash
   git clone https://github.com/NaufalRachmandani/BeliMotor.git
   ```

## Running the Project

1. Open Project on Android Studio.

2. Android Studio will take a moment to sync the project and download the necessary dependencies.

3. Click on the green "Run" button (usually a play icon) in the top menu.

4. Choose a target device or emulator to run the application. If you don't have a device or emulator set up, you can follow the prompts to create or configure one.

5. Android Studio will build and install the app on the selected device/emulator. The app should launch automatically and display the UI built with Jetpack Compose.

## Exploring the Code

- `MainActivity.kt`: This is the entry point of the application. It sets up the UI using Jetpack Compose components.

- `ui/theme/Theme.kt`: This is where you can define custom styles for your Compose components, in this project I used Material 3 with **dynamicLightColorScheme** which based on wallpaper color.

## Additional Resources

- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Android Developer Documentation](https://developer.android.com/docs)

## Conclusion

If you encounter any issues or have questions, feel free to refer to the project's documentation or seek help from the Android developer community.
