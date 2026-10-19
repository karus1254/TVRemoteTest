# TV Remote Test

Android test remote for Xiaomi Mi 9 built-in infrared emitter. Samsung UE42F5020AK and T95 W2.

**Experimental only:** T95 W2 infrared protocol is not confirmed, and Samsung codes are unverified on this exact TV. The app does not use Wi-Fi. No hardware modifications are needed.

## Build APK with GitHub (no Android Studio)

1. Create a new GitHub repository (private is fine), with no auto-generated README.
2. On the repository page choose **Add file → Upload files** and upload the **contents of this project folder**, including `.github/workflows/build-apk.yml`. Note: GitHub web file uploader may not allow uploading folders or hidden folders conveniently; if `.github` is missing, use **Add file → Create new file** and type `.github/workflows/build-apk.yml`, then paste the workflow content.
3. Commit files to `main`.
4. Go to **Actions → Build Android APK**. If needed, click **Run workflow**.
5. Wait for a green successful run. Open it and download the **TVRemoteTest-debug-apk** artifact.
6. Unzip the downloaded artifact to obtain `app-debug.apk`. Install on Xiaomi Mi 9, granting install-from-this-source permission if prompted.

GitHub Actions workflow builds with Java 17, Gradle 8.10.2, Android SDK 35, Android Gradle Plugin 8.6.1.

## Caveats

- IR signals are not learned from the physical remote; correct T95 W2 carrier, bit order and frame encoding still need real-device validation.
- The app is a diagnostic prototype, not a validated universal remote.
