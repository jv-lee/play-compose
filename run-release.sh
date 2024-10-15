./gradlew assembleRelease

adb shell am force-stop com.lee.playcompose
adb install -r ./app/build/outputs/apk/release/app-release.apk
adb shell am start com.lee.playcompose/com.lee.playcompose.MainActivity
#
#./gradlew assembleDebug
#
#adb shell am force-stop com.lee.playcompose.debug
#adb install -r ./app/build/outputs/apk/debug/app-debug.apk
#adb shell am start com.lee.playcompose.debug/com.lee.playcompose.MainActivity