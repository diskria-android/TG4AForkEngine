package io.github.tg4afe.extensions.android

import com.android.build.api.variant.ComponentIdentity

fun ComponentIdentity.getFileLocations(
    fileName: String,
    includeRoot: Boolean = true,
): List<String> {
    val dirs = buildList {
        addAll(getFlavorDirNames())
        buildType?.let { add(it) }
    }.map { "$it/$fileName" }
    return if (includeRoot) dirs + fileName else dirs
}

fun ComponentIdentity.getFlavorDirNames(dimensionFilter: String? = null): List<String> =
    buildList {
        add(name)
        flavorName?.let { flavorName ->
            add(flavorName)
            if (dimensionFilter == null) {
                addAll(productFlavors.map { it.second })
            } else {
                productFlavors.toMap()[dimensionFilter]?.let { add(it) }
            }
        }
    }
