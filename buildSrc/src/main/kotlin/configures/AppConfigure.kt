package configures

import appDependencies
import build.*
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import configures.core.freeCompilerArgs
import dependencies.Version
import kapt
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * @author jv.lee
 * @date 2021/10/1
 * @description app模块配置依赖扩展
 */
@Suppress("MISSING_DEPENDENCY_SUPERCLASS", "MISSING_DEPENDENCY_CLASS")
fun Project.appConfigure(
    projectConfigure: Project.() -> Unit = {},
    androidConfigure: BaseAppModuleExtension.() -> Unit = {}
) {
    plugins.apply(BuildPlugin.application)
    plugins.apply(BuildPlugin.kotlin)
    plugins.apply(BuildPlugin.kapt)

    projectConfigure()

    extensions.configure<BaseAppModuleExtension> {
        compileSdk = BuildConfig.compileSdk

        defaultConfig {
            applicationId = BuildConfig.applicationId

            minSdk = BuildConfig.minSdk
            targetSdk = BuildConfig.targetSdk
            versionName = BuildConfig.versionName
            versionCode = BuildConfig.versionCode

            multiDexEnabled = BuildConfig.multiDex

            //指定只支持中文字符
            resConfig("zh-rCN")

            testInstrumentationRunner = BuildConfig.TEST_INSTRUMENTATION_RUNNER

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
            kotlinCompilerExtensionVersion = Version.composeCompiler
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

        val signingConfigs = signingConfigs.create(BuildTypes.RELEASE).apply {
            storeFile(rootProject.file(BuildRelease.SigningConfig.storeFile))
            storePassword(BuildRelease.SigningConfig.storePassword)
            keyAlias(BuildRelease.SigningConfig.keyAlias)
            keyPassword(BuildRelease.SigningConfig.keyPassword)
        }

        buildTypes {
            getByName(BuildTypes.DEBUG) {
                applicationIdSuffix = ".debug"
                manifestPlaceholders["app_name"] = "Debug\nCompose"
                isMinifyEnabled = BuildDebug.isMinifyEnabled //混淆模式
                proguardFiles("proguard-android-optimize.txt", "proguard-rules.pro")
            }

            getByName(BuildTypes.RELEASE) {
                manifestPlaceholders["app_name"] = "Play\nCompose"
                isMinifyEnabled = BuildRelease.isMinifyEnabled //混淆模式
                proguardFiles("proguard-android-optimize.txt", "proguard-rules.pro")
                signingConfig = signingConfigs
            }
        }

        androidConfigure()
    }

    dependencies {
        appDependencies()
    }

}