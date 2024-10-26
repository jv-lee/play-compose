package configures

import build.BuildConfig
import build.BuildDebug
import build.BuildModules
import build.BuildPlugin
import build.BuildRelease
import build.BuildTypes
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import freeCompilerArgs
import implementation
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * app模块配置依赖扩展
 * @author jv.lee
 * @date 2021/10/1
 */
fun Project.appConfigure(projectConfigure: Project.() -> Unit = {}) {
    plugins.apply(BuildPlugin.APPLICATION)
    plugins.apply(BuildPlugin.KOTLIN_ANDROID)
    plugins.apply(BuildPlugin.KOTLIN_KAPT)

    extensions.configure<BaseAppModuleExtension> {
        namespace = BuildConfig.APPLICATION_ID
        compileSdk = BuildConfig.COMPILE_SDK

        defaultConfig {
            applicationId = BuildConfig.APPLICATION_ID

            minSdk = BuildConfig.MIN_SDK
            targetSdk = BuildConfig.TARGET_SDK
            versionName = BuildConfig.VERSION_NAME
            versionCode = BuildConfig.VERSION_CODE

            // 混淆配置 指定只支持中文字符
            multiDexEnabled = BuildConfig.MULTI_DEX_ENABLE
            resourceConfigurations.add("zh-rCN")

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
            kotlinCompilerExtensionVersion = BuildConfig.COMPOSE_KOTLIN_COMPILER
        }

        packaging {
            resources {
                excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
        }

        buildFeatures {
            dataBinding = true
            viewBinding = true
            compose = true
        }

        val signingConfigs = signingConfigs.create(BuildTypes.RELEASE).apply {
            storeFile(rootProject.file(BuildRelease.SigningConfig.STORE_FILE))
            storePassword(BuildRelease.SigningConfig.STORE_PASSWORD)
            keyAlias(BuildRelease.SigningConfig.KEY_ALIAS)
            keyPassword(BuildRelease.SigningConfig.KEY_PASSWORD)
        }

        buildTypes {
            getByName(BuildTypes.DEBUG) {
                applicationIdSuffix = ".debug"
                manifestPlaceholders["app_name"] = "Debug\nCompose"
                isMinifyEnabled = BuildDebug.isMinifyEnabled // 混淆模式
                isShrinkResources = BuildDebug.isShrinkResources // 资源压缩
                proguardFiles("proguard-android-optimize.txt", "proguard-rules.pro")
            }

            getByName(BuildTypes.RELEASE) {
                manifestPlaceholders["app_name"] = "Play\nCompose"
                isMinifyEnabled = BuildRelease.isMinifyEnabled // 混淆模式
                isShrinkResources = BuildRelease.isShrinkResources // 资源压缩
                proguardFiles("proguard-android-optimize.txt", "proguard-rules.pro")
                signingConfig = signingConfigs
            }
        }
    }

    dependencies {
        // 添加基础服务依赖
        implementation(project(BuildModules.Library.SERVICE))

        // 各业务组建基础依赖
        implementation(project(BuildModules.Module.HOME))
        implementation(project(BuildModules.Module.SQUARE))
        implementation(project(BuildModules.Module.SYSTEM))
        implementation(project(BuildModules.Module.ME))
        implementation(project(BuildModules.Module.OFFICIAL))
        implementation(project(BuildModules.Module.PROJECT))
        implementation(project(BuildModules.Module.DETAILS))
        implementation(project(BuildModules.Module.SEARCH))
        implementation(project(BuildModules.Module.ACCOUNT))
        implementation(project(BuildModules.Module.TODO))
    }

    projectConfigure()
}