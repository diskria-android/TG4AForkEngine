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
    }
}

dependencyResolutionManagement {
    repositories {
        pluginManagement.repositories.find { it.name == "Google" }?.let { add(it) }
        mavenCentral { name = "Maven Central" }
        maven("https://developer.huawei.com/repo") {
            name = "Huawei"
            content {
                includeGroupByRegex("""com\.huawei\..*""")
            }
        }
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

include(":build-commons")
include(":android-conventions")
include(":test-generator")
include(":app-icons-generator")
