./gradlew assembleRelease

adb shell am force-stop com.lee.playcompose
adb install -r ./app/build/outputs/apk/release/app-release.apk
adb shell am start com.lee.playcompose/.MainActivity
