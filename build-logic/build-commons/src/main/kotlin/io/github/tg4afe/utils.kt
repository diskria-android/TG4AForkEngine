package io.github.tg4afe

import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.nativeplatform.OperatingSystemFamily
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

val isCI: Boolean by lazy {
    !System.getenv("CI").isNullOrEmpty()
}

val isWindowsHost: Boolean by lazy {
    DefaultNativePlatform.getCurrentOperatingSystem()
        .toFamilyName() == OperatingSystemFamily.WINDOWS
}
