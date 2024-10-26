import build.BuildModules
import build.BuildModules.name
import configures.libraryConfigure

plugins {
    alias(libs.plugins.buildVersion)
}

libraryConfigure(BuildModules.Library.COMMON.name()) {
    paramsConfigure()

    dependencies {
        implementation(project(BuildModules.Library.BASE))

        kapt(libs.bundles.compiler)

        testImplementation(libs.bundles.test)
        androidTestImplementation(libs.bundles.androidTest)
        debugImplementation(libs.bundles.debug)
    }
}




