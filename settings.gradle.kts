@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        exclusiveContent {
            forRepository {
                maven("https://developer.huawei.com/repo") { name = "Huawei" }
            }
            filter {
                includeGroupByRegex("""com\.huawei\..*""")
            }
        }
        mavenLocal {
            content {
                includeGroup("io.github.forky")
            }
        }
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "com.huawei.agconnect") {
                useModule("com.huawei.agconnect:agcp:${requested.version}")
            }
        }
    }
    includeBuild("build-logic")
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        pluginManagement.repositories.find { it.name == "Huawei" }?.let { add(it) }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":native-core")

include(":TMessagesProj")
project(":TMessagesProj").name = "app"

include(":third-party")
include(":third-party:recycler-view")

include(":feature")
include(":feature:app-icons")
