package io.github.tg4afe.extensions.android

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.AndroidComponentsExtension
import io.github.tg4afe.isCI
import org.gradle.api.GradleException
import org.gradle.api.Project
import java.util.Properties

val Project.android: CommonExtension
    get() = extensions.findByName("android") as? CommonExtension
        ?: throw GradleException("Android plugin is not applied to project '$name'")

fun Project.android(configure: CommonExtension.() -> Unit) {
    android.apply(configure)
}

val Project.androidComponents: AndroidComponentsExtension<*, *, *>
    get() = extensions.findByName("androidComponents") as? AndroidComponentsExtension<*, *, *>
        ?: throw GradleException("Android plugin is not applied to project '$name'")

fun Project.androidComponents(configure: AndroidComponentsExtension<*, *, *>.() -> Unit) {
    androidComponents.apply(configure)
}

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
        throw GradleException(
            "Missing required property '$name' " +
                "(searched in local.properties and $sourceName)"
        )
    }
