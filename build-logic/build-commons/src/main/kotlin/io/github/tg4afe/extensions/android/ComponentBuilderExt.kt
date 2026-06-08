package io.github.tg4afe.extensions.android

import com.android.build.api.variant.ComponentBuilder

fun ComponentBuilder.disable() {
    enable = false
}
