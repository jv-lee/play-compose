package configures

import build.BuildConfig
import build.BuildPlugin
import com.android.build.gradle.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import kapt
import commonProcessors
import commonTest
import baseService
import configures.core.freeCompilerArgs
import dependencies.Version

/**
 * @author jv.lee
 * @date 2021/10/1
 * @description 组件模块配置依赖扩展
 */
@Suppress("MISSING_DEPENDENCY_SUPERCLASS")
fun Project.moduleConfigure(
    projectConfigure: Project.() -> Unit = {},
    androidConfigure: LibraryExtension.() -> Unit = {}
) {
    plugins.apply(BuildPlugin.library)
    plugins.apply(BuildPlugin.kotlin)
    plugins.apply(BuildPlugin.kapt)

    projectConfigure()

    extensions.configure<LibraryExtension> {
        compileSdk = BuildConfig.compileSdk

        defaultConfig {
            minSdk = BuildConfig.minSdk
            targetSdk = BuildConfig.targetSdk

            vectorDrawables {
                useSupportLibrary = true
            }
        }

        tasks.withType<KotlinCompile> {
            kotlinOptions.jvmTarget = "1.8"
            kotlinOptions.freeCompilerArgs += freeCompilerArgs
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }

        composeOptions {
            kotlinCompilerExtensionVersion = Version.compose
            kotlinCompilerVersion = "1.6.10"
        }

        packagingOptions {
            resources {
                excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
        }

        buildFeatures {
            dataBinding = true
            viewBinding = true
            compose = true
        }

        kapt {
            generateStubs = true
        }

        androidConfigure()
    }

    dependencies {
        commonTest()
        commonProcessors()
        baseService()
    }
}