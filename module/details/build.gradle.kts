import build.BuildModules
import build.BuildModules.name
import configures.moduleConfigure

plugins {
    alias(libs.plugins.buildVersion)
}

moduleConfigure(BuildModules.Module.DETAILS.name()) {
    dependencies {
        kapt(libs.bundles.compiler)
    }
}

