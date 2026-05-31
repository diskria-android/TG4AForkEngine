package com.example

import org.gradle.kotlin.dsl.provideDelegate

fun String.quoted(): String = "\"$this\""

val isCI: Boolean by lazy {
    !System.getenv("CI").isNullOrEmpty()
}
