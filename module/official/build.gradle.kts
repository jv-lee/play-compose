import build.BuildModules
import build.BuildModules.name
import configures.moduleConfigure

plugins {
    alias(libs.plugins.buildVersion)
}

moduleConfigure(BuildModules.Module.OFFICIAL.name()) {
    dependencies {
        kapt(libs.bundles.compiler)
    }
}

