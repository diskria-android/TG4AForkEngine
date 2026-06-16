@file:Suppress("UnstableApiUsage", "ChromeOsAbiSupport")

import io.github.tg4afe.extensions.android.requireProperty

plugins {
    alias(libs.plugins.android.library)
    id("android-conventions")
}

android {
    namespace = project.requireProperty("NAMESPACE") + ".core.logging"
}

dependencies {
    implementation("androidx.core:core:1.16.0")
    implementation("com.google.code.gson:gson:2.11.0")

    implementation(project(":core:time"))
    implementation(project(":core:multithreading"))
}
