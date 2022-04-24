package dependencies

object TestDependencies {
    const val junit = "junit:junit:${Version.junit}"
    const val junitAndroid = "androidx.test.ext:junit:${Version.junitAndroid}"
    const val espresso = "androidx.test.espresso:espresso-core:${Version.espressoAndroid}"
    const val composeUiTest = "androidx.compose.ui:ui-test-junit4:${Version.composeCompiler}"
    const val composeUiTooling = "androidx.compose.ui:ui-tooling:${Version.composeCompiler}"

    const val leakcanaryDebug = "com.squareup.leakcanary:leakcanary-android:${Version.leakcanary}"
    const val blockCanary = "com.github.markzhai:blockcanary-android:${Version.blockCanary}"
}