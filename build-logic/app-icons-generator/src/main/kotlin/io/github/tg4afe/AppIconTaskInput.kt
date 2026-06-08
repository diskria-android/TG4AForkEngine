package io.github.tg4afe

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested

data class AppIconTaskInput(

    @get:Input
    val name: String,

    @get:Nested
    val icon: IconResourceTaskInput,

    @get:Nested
    val roundIcon: IconResourceTaskInput,

    @get:Nested
    val background: IconResourceTaskInput,

    @get:Nested
    val foreground: IconResourceTaskInput,

    @get:Input
    val isDefault: Boolean,

    @get:Input
    val isPremium: Boolean
)

data class IconResourceTaskInput(
    @get:Input
    val resourceReference: String,

    @get:Input
    val codeReference: String,
)
