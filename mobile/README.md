# FILERALUV Android build

This folder wraps the web frontend as an Android app. It uses the same Render API URLs configured in `../web-app/frontend/index.html`.

## Build a debug APK

1. Install Android Studio on Windows and, in its setup wizard, install the Android SDK and Android SDK Platform Tools.
2. Open PowerShell in this `mobile` folder.
3. Run `npm install` once.
4. Run `npm run cap-sync` to copy the latest `web-app/frontend/index.html` into the Android project.
5. Run `npm run android` to open the project in Android Studio.
6. In Android Studio, wait for Gradle sync. If prompted, install the recommended Android SDK platform/build tools.
7. Choose **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
8. Android Studio will show a notification with **Locate**. The debug APK is at `android\app\build\outputs\apk\debug\app-debug.apk`.

The debug APK can be installed on an Android device for personal use. To publish on Google Play, create a signed release build in Android Studio.

Whenever `web-app/frontend/index.html` changes, run `npm run cap-sync` before rebuilding so the APK gets the latest page.
