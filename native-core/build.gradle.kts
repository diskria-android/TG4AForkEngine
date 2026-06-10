@file:Suppress("UnstableApiUsage", "ChromeOsAbiSupport")

import io.github.tg4afe.extensions.android.getPropertyOrNull
import io.github.tg4afe.extensions.android.requireProperty
import io.github.tg4afe.extensions.capitalized

plugins {
    id("com.android.library")
    id("android-conventions")
}

android {
    namespace = project.requireProperty("NAMESPACE") + ".native_core"
    ndkVersion = "27.2.12479018"

    externalNativeBuild.cmake.path = file("src/main/cpp/CMakeLists.txt")

    defaultConfig {
        externalNativeBuild {
            cmake {
                version = "3.10.2"
            }
        }

        ndk {
            abiFilters += setOf("arm64-v8a", "armeabi-v7a")
            debugSymbolLevel = "NONE"
        }
    }

    packaging {
        jniLibs {
            pickFirsts.add("lib/**/*.so")
        }
    }
}

androidComponents {
    onVariants { variant ->
        val externalNativeBuild = requireNotNull(variant.externalNativeBuild)

        val variantName = variant.name
        val taskVariantPart = variantName.capitalized()
        val prebuiltLibsPath = "prebuilt_libs/$variantName"
        val prebuiltLibsDir = layout.projectDirectory.dir(prebuiltLibsPath)
        prebuiltLibsDir.asFile.mkdirs()
        val hasPrebuiltLibs = prebuiltLibsDir.asFile.list()?.isNotEmpty() == true
        if (hasPrebuiltLibs) {
            variant.sources.jniLibs?.addStaticSourceDirectory(prebuiltLibsPath)
            externalNativeBuild.abiFilters = emptySet()
            externalNativeBuild.arguments.add("-DSKIP_NATIVE_BUILD=ON")
        } else {
            val stripDebugSymbolsTaskName = "strip${taskVariantPart}DebugSymbols"
            val strippedLibsDir = layout.buildDirectory.dir(
                "intermediates/stripped_native_libs/$variantName/$stripDebugSymbolsTaskName/out/lib"
            )
            val syncJniLibsTask = tasks.register<Sync>("sync${taskVariantPart}JniLibs") {
                description = "Copy jni libs to prebuilt cache for $variantName"

                from(strippedLibsDir) {
                    include("**/**.so")
                }
                into(prebuiltLibsDir)
                includeEmptyDirs = true
            }
            tasks.configureEach {
                if (name == "assemble$taskVariantPart") {
                    finalizedBy(syncJniLibsTask)
                }
            }
            when (variant.buildType) {
                "debug" -> {
                    val overrideAbiFilters = project.getPropertyOrNull("DEBUG_ABI_FILTERS")
                        .orEmpty().split(Regex("""[,;\s]+""")).filter { it.isNotBlank() }
                    if (overrideAbiFilters.isNotEmpty()) {
                        externalNativeBuild.abiFilters = overrideAbiFilters
                    } else {
                        externalNativeBuild.abiFilters.addAll(setOf("x86", "x86_64"))
                    }
                }

                "release" -> {
                    val distributionType = variant.productFlavors.toMap()["distributionType"]
                        .orEmpty()
                    if (distributionType == "direct" || distributionType == "beta") {
                        externalNativeBuild.abiFilters.addAll(setOf("x86", "x86_64"))
                    }
                }
            }
        }
    }
}
