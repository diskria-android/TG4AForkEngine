@file:Suppress("AvoidApplyPluginMethod")

apply(from = "../gradle/repositories.settings.gradle.kts")

dependencyResolutionManagement {
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
