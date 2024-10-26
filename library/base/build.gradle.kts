import build.BuildModules
import build.BuildModules.name
import configures.libraryConfigure

plugins {
    alias(libs.plugins.buildVersion)
}

libraryConfigure(BuildModules.Library.BASE.name()) {
    dependencies {
        kapt(libs.bundles.compiler)

        api(platform(libs.androidx.compose.bom))
        api(libs.bundles.androidx)
        api(libs.bundles.thirdPart)

        testImplementation(libs.bundles.test)
        androidTestImplementation(libs.bundles.androidTest)
        debugImplementation(libs.bundles.debug)
    }
}




