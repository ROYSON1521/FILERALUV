# FILERALUV

FILERALUV is the Android app for the file converter web service.

## Folders

- `mobile/` contains the Capacitor and Android Studio project, the bundled web app, and the native Android download handler.
- `android-apk/` contains the installable debug APK and its build metadata.

## Install the APK

Copy `android-apk/app-debug.apk` to an Android phone and open it to install. If Android asks, allow your file manager or browser to install apps from that source. Converted files are saved under `Downloads/FILERALUV`.

## Build the Android app

Install Node.js and Android Studio with Android SDK Platform 35 and Build Tools 35.0.0 or newer. Then, from this repository:

```powershell
cd mobile
npm install
npm run cap-sync
cd android
.\gradlew.bat assembleDebug
```

The APK will be created at `mobile/android/app/build/outputs/apk/debug/app-debug.apk`. The project has minimum Android API 23 and targets API 35, so it runs on Android 15 and Android 16 devices.

`mobile/www/index.html` is the bundled web frontend. In the full development workspace, `npm run cap-sync` copies the latest frontend from `web-app/frontend/index.html`; in this Android-only repository it uses the bundled copy.

## Backend

The APK calls the hosted Render backend configured in the bundled frontend. Backend source and deployment settings are not included in this Android-only repository; deploy backend changes separately for them to take effect in the app.
