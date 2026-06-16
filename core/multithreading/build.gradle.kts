@file:Suppress("UnstableApiUsage", "ChromeOsAbiSupport")

import io.github.tg4afe.extensions.android.requireProperty

plugins {
    alias(libs.plugins.android.library)
    id("android-conventions")
}

android {
    namespace = project.requireProperty("NAMESPACE") + ".core.multithreading"
}
