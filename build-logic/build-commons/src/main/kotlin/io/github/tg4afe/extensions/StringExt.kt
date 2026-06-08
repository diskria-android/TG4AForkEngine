package io.github.tg4afe.extensions

import java.util.Locale

fun String.quoted(): String = "\"$this\""

fun String.capitalized(locale: Locale = Locale.ROOT): String =
    replaceFirstChar { it.titlecase(locale) }
