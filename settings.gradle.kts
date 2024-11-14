@file:Suppress("UnstableApiUsage")

includeBuild("build-version")

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")
        maven("https://maven.aliyun.com/repository/google")
        maven("https://maven.aliyun.com/repository/jcenter/")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://maven.aliyun.com/repository/google")
        maven("https://maven.aliyun.com/repository/jcenter/")
    }

    versionCatalogs {
        create("libs") {
            from(files("build-version/gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "play-compose"
include(":app")

include(":library:base")
include(":library:common")
include(":library:router")
include(":library:service")

include(":module:home")
include(":module:square")
include(":module:system")
include(":module:me")
include(":module:official")
include(":module:project")
include(":module:details")
include(":module:search")
include(":module:account")
include(":module:todo")
