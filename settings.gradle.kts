@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        google {
            name = "Google"
            content {
                includeGroupByRegex("""com\.android.*""")
                includeGroupByRegex("""com\.google\..*""")
                includeGroupByRegex("androidx.*")
            }
        }
        gradlePluginPortal { name = "Gradle Plugin Portal" }
        maven("https://developer.huawei.com/repo") {
            name = "Huawei"
            content {
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
        pluginManagement.repositories.find { it.name == "Google" }?.let { add(it) }
        mavenCentral { name = "Maven Central" }
        pluginManagement.repositories.find { it.name == "Huawei" }?.let { add(it) }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":native-core")

include(":third-party")
include(":third-party:recycler-view")

include(":feature")
include(":feature:app-icons")

include(":TMessagesProj")
project(":TMessagesProj").name = "app"
