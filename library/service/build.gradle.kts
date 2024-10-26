import build.BuildModules
import build.BuildModules.name
import configures.libraryConfigure

plugins {
    alias(libs.plugins.buildVersion)
}

libraryConfigure(BuildModules.Library.SERVICE.name())

dependencies {
    api(project(BuildModules.Library.BASE))
    api(project(BuildModules.Library.COMMON))
    api(project(BuildModules.Library.ROUTE))

    kapt(libs.bundles.compiler)

    testImplementation(libs.bundles.test)
    androidTestImplementation(libs.bundles.androidTest)
    debugImplementation(libs.bundles.debug)
}

