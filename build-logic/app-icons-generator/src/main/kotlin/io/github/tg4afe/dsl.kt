package io.github.tg4afe

import com.android.build.api.dsl.DefaultConfig
import com.android.build.api.dsl.ProductFlavor
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.plugins.ExtensionAware

fun DefaultConfig.appIcons(configure: AppIconsListExtension.() -> Unit) {
    (this as ExtensionAware)
        .extensions.configure(AppIconsGeneratorPlugin.APP_ICONS_EXTENSION_NAME, configure)
}

fun ProductFlavor.appIcons(configure: NamedDomainObjectContainer<AppIconConfig>.() -> Unit) {
    extensions.configure(AppIconsGeneratorPlugin.APP_ICONS_EXTENSION_NAME, configure)
}

fun AppIconsListExtension.register(name: String, configure: AppIconConfig.() -> Unit) {
    list.add(AppIconConfig(name).apply(configure))
}
