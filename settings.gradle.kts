@file:Suppress("AvoidApplyPluginMethod")

apply(from = "gradle/repositories.settings.gradle.kts")

pluginManagement {
    includeBuild("build-logic")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":core:utils")
include(":core:time")
include(":core:multithreading")
include(":core:logging")
include(":core:cpp")

include(":third-party")
include(":third-party:exoplayer")
include(":third-party:webrtc")
include(":third-party:recycler-view")

include(":feature")
include(":feature:app-icons")

include(":TMessagesProj")
project(":TMessagesProj").name = "app"
