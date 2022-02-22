plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://maven.aliyun.com/repository/google")
    maven("https://maven.aliyun.com/repository/jcenter/")
}

dependencies {
    implementation("com.android.tools.build:gradle:7.0.3")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
}