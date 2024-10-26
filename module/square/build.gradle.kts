import build.BuildModules
import build.BuildModules.name
import configures.moduleConfigure

plugins {
    alias(libs.plugins.buildVersion)
}

moduleConfigure(BuildModules.Module.SQUARE.name()) {
    dependencies {
        kapt(libs.bundles.compiler)

        testImplementation(libs.bundles.test)
        androidTestImplementation(libs.bundles.androidTest)
        debugImplementation(libs.bundles.debug)
    }
}

