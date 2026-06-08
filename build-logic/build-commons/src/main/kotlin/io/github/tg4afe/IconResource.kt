package io.github.tg4afe

open class IconResource(
    val type: IconResourceType,
    val name: String,
)

enum class IconResourceType(val value: String) {
    DRAWABLE("drawable"),
    MIPMAP("mipmap"),
}

fun String.drawableRes(): IconResource =
    IconResource(IconResourceType.DRAWABLE, this)

fun String.mipmapRes(): IconResource =
    IconResource(IconResourceType.MIPMAP, this)
