@file:Suppress("UnstableApiUsage")

import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.maven

fun Settings.configurePluginRepositories() {
    pluginManagement {
        repositories {
            googleRepository()
            gradlePluginPortalRepository()
            huaweiRepository()
            mavenLocal {
                content {
                    includeGroup("io.github.forky")
                }
            }
        }
    }
}

fun Settings.configureDependencyRepositories() {
    dependencyResolutionManagement {
        repositories {
            googleRepository()
            mavenCentralRepository()
            huaweiRepository()
        }
    }
}

fun RepositoryHandler.mavenCentralRepository() {
    mavenCentral { name = "Maven Central" }
}

fun RepositoryHandler.gradlePluginPortalRepository() {
    gradlePluginPortal { name = "Gradle Plugin Portal" }
}

fun RepositoryHandler.googleRepository() {
    google {
        name = "Google"
        content {
            includeGroupByRegex("""com\.android.*""")
            includeGroupByRegex("""com\.google\..*""")
            includeGroupByRegex("androidx.*")
        }
    }
}

fun RepositoryHandler.huaweiRepository() {
    maven("https://developer.huawei.com/repo") {
        name = "Huawei"
        content {
            includeGroupByRegex("""com\.huawei\..*""")
        }
    }
}

configurePluginRepositories()
configureDependencyRepositories()
