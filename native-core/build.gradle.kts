@file:Suppress("UnstableApiUsage", "ChromeOsAbiSupport")

import io.github.tg4afe.extensions.android.getPropertyOrNull
import io.github.tg4afe.extensions.android.requireProperty
import io.github.tg4afe.extensions.capitalized

plugins {
    alias(libs.plugins.android.library)
    id("android-conventions")
}

android {
    namespace = project.requireProperty("NAMESPACE") + ".native_core"
    ndkVersion = "27.2.12479018"

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }

    defaultConfig {
        externalNativeBuild {
            cmake {
                version = "3.10.2"
            }
        }

        ndk {
            debugSymbolLevel = "NONE"
        }
    }

    packaging {
        jniLibs {
            pickFirsts.add("lib/**/libtmessages.*.so")
        }
    }
}

androidComponents {
    onVariants { variant ->
        val externalNativeBuild = requireNotNull(variant.externalNativeBuild)

        val variantName = variant.name
        val taskVariantPart = variantName.capitalized()
        val prebuiltJniLibsPath = "prebuiltJniLibs/$variantName"
        val prebuiltJniLibsDir = layout.projectDirectory.dir(prebuiltJniLibsPath).apply {
            asFile.mkdirs()
        }

        val hasPrebuiltJniLibs = prebuiltJniLibsDir.asFile.list()?.isNotEmpty() == true
        if (hasPrebuiltJniLibs) {
            variant.sources.jniLibs?.addStaticSourceDirectory(prebuiltJniLibsPath)
            variant.externalNativeBuild?.arguments?.add("-DSKIP_NATIVE_BUILD=true")
        } else {
            val strippedNativeLibsDir = layout.buildDirectory.dir(
                "intermediates/" +
                    "stripped_native_libs/" +
                    "$variantName/" +
                    "strip${taskVariantPart}DebugSymbols/" +
                    "out/" +
                    "lib"
            )
            val copyNativeLibsTask = tasks.register<Copy>("copy${taskVariantPart}NativeLibs") {
                description = "Copy jni libs to prebuilt cache for $variantName"

                from(strippedNativeLibsDir) {
                    include("**/**.so")
                }
                into(prebuiltJniLibsDir)
                includeEmptyDirs = true
            }
            tasks.configureEach {
                if (name == "assemble$taskVariantPart") {
                    finalizedBy(copyNativeLibsTask)
                }
            }
        }
        externalNativeBuild.abiFilters.addAll(setOf("arm64-v8a", "armeabi-v7a"))
        when (variant.buildType) {
            "debug" -> {
                val overrideAbiFilters = project.getPropertyOrNull("DEBUG_ABI_FILTERS").orEmpty()
                if (overrideAbiFilters.isNotEmpty()) {
                    externalNativeBuild.abiFilters = overrideAbiFilters
                        .split(Regex("""[,;\s]+"""))
                        .filter { it.isNotBlank() }
                } else {
                    externalNativeBuild.abiFilters.addAll(setOf("x86", "x86_64"))
                }
            }

            "release" -> {
                val distributionType = variant.productFlavors.toMap()["distributionType"].orEmpty()
                if (distributionType == "direct" || distributionType == "beta") {
                    externalNativeBuild.abiFilters.addAll(setOf("x86", "x86_64"))
                }
            }
        }
    }
}

tasks.named<Delete>("clean") {
    delete(layout.projectDirectory.dir(".cxx"))
}
