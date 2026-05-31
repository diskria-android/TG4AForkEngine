package com.example

import org.gradle.api.GradleException
import org.gradle.api.Project
import java.util.Properties

fun Project.getPropertyOrNull(name: String): String? {
    val localProperties = Properties().apply {
        val file = rootProject.file("local.properties")
        if (file.exists()) {
            file.inputStream().use { load(it) }
        }
    }
    val localValue = localProperties.getProperty(name)
    if (!localValue.isNullOrEmpty()) {
        return localValue
    }
    val value = if (isCI) {
        System.getenv(name)
    } else {
        val gradleProperties = Properties().apply {
            val file = rootProject.file("gradle.properties")
            if (file.exists()) {
                file.inputStream().use { load(it) }
            }
        }
        gradleProperties.getProperty(name)
    }
    return if (!value.isNullOrEmpty()) value else null
}

fun Project.requireProperty(name: String): String =
    getPropertyOrNull(name) ?: run {
        val sourceName = if (isCI) "CI environment variables" else "gradle.properties"
        throw GradleException("Missing required property '$name' (searched in local.properties and $sourceName)")
    }
