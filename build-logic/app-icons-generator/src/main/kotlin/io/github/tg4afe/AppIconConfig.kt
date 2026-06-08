package io.github.tg4afe

import org.gradle.api.GradleException
import javax.inject.Inject

open class AppIconConfig @Inject constructor(val name: String) {

    var icon: IconResource? = null
    var roundIcon: IconResource? = null
    var background: IconResource? = null
    var foreground: IconResource? = null
    var default: Boolean = false
    var premium: Boolean = false

    internal fun copyFrom(source: AppIconConfig) {
        icon = source.icon
        roundIcon = source.roundIcon
        background = source.background
        foreground = source.foreground
        default = source.default
        premium = source.premium
    }

    internal fun cloneConfig(): AppIconConfig =
        AppIconConfig(name).also { it.copyFrom(this) }

    internal fun mergeFrom(flavor: AppIconConfig) {
        if (flavor.icon != null) {
            icon = flavor.icon
        }
        if (flavor.roundIcon != null) {
            roundIcon = flavor.roundIcon
        }
        if (flavor.background != null) {
            background = flavor.background
        }
        if (flavor.foreground != null) {
            foreground = flavor.foreground
        }
        if (flavor.default) {
            default = true
        }
        if (flavor.premium) {
            premium = true
        }
    }

    internal fun validateIcon(): IconResource =
        icon ?: throw GradleException("App icon '$name' doesn't have icon")

    internal fun validateRoundIcon(): IconResource =
        roundIcon ?: throw GradleException("App icon '$name' doesn't have icon")

    internal fun validateBackground(): IconResource =
        background ?: throw GradleException("App icon '$name' doesn't have background")

    internal fun validateForeground(): IconResource =
        foreground ?: throw GradleException("App icon '$name' doesn't have foreground")
}
