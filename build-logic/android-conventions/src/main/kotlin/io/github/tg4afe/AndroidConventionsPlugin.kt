package io.github.tg4afe

import io.github.tg4afe.extensions.android.android
import io.github.tg4afe.extensions.android.androidComponents
import io.github.tg4afe.extensions.android.buildConfigFields
import io.github.tg4afe.extensions.android.disable
import io.github.tg4afe.extensions.android.getPropertyOrNull
import io.github.tg4afe.extensions.android.requireProperty
import org.gradle.api.GradleException
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidConventionsPlugin : Plugin<Project> {

    override fun apply(project: Project) = with(project) {
        val setupBuildConfigs = true
        val setupManifestPlaceholders = true
        android {
            compileSdk = 35
            defaultConfig.apply {
                minSdk = 21
                if (setupBuildConfigs) {
                    buildConfigFields(
                        mapOf(
                            "BUILD_VERSION_STRING" to requireProperty("APP_VERSION_NAME"),
                            "BUILD_HOST_IS_WINDOWS" to isWindowsHost,
                            "DEBUG_VERSION" to false,
                            "DEBUG_PRIVATE_VERSION" to false,
                            "BUNDLE" to false,
                            "API_ID" to requireProperty("API_ID").toInt(),
                            "API_HASH" to requireProperty("API_HASH"),
                            "APP_UPDATE_URL" to "https://telegram.org/android",
                        )
                    )
                }
                if (setupManifestPlaceholders) {
                    manifestPlaceholders += mapOf(
                        "applicationLabel" to "@string/AppName",
                    )
                }
            }

            flavorDimensions += listOf("distributionType", "outputFormat")
            productFlavors.apply {
                create("google") {
                    dimension = "distributionType"
                    if (setupBuildConfigs) {
                        buildConfigFields(
                            mapOf(
                                "APP_UPDATE_URL" to "https://play.google.com/store/apps/details?" +
                                    "id=${requireProperty("APP_ID")}",
                            )
                        )
                    }
                    if (setupManifestPlaceholders) {
                        manifestPlaceholders += mapOf(
                            "applicationName" to "org.telegram.messenger.google.ApplicationLoaderImpl",
                        )
                    }
                }
                create("huawei") {
                    dimension = "distributionType"
                    if (setupBuildConfigs) {
                        val huaweiAppId = requireProperty("HUAWEI_APP_ID")
                        buildConfigFields(
                            mapOf(
                                "HUAWEI_APP_ID" to huaweiAppId,
                                "APP_UPDATE_URL" to "https://appgallery.huawei.com/app/" +
                                    "C$huaweiAppId"
                            )
                        )
                    }
                    if (setupManifestPlaceholders) {
                        manifestPlaceholders += mapOf(
                            "applicationName" to "org.telegram.messenger.HuaweiApplicationLoader",
                        )
                    }
                }
                create("direct") {
                    dimension = "distributionType"
                    if (setupBuildConfigs) {
                        buildConfigFields(
                            mapOf(
                                "APP_UPDATE_URL" to "https://telegram.org/dl/android/apk",
                            )
                        )
                    }
                    if (setupManifestPlaceholders) {
                        manifestPlaceholders += mapOf(
                            "applicationName" to "org.telegram.messenger.direct.ApplicationLoaderImpl",
                        )
                    }
                }
                create("beta") {
                    dimension = "distributionType"
                    if (setupBuildConfigs) {
                        val publicUrl = getPropertyOrNull("BETA_PUBLIC_URL")
                        val privateUrl = getPropertyOrNull("BETA_PRIVATE_URL")
                        val hardcoreUrl = getPropertyOrNull("BETA_HARDCORE_URL")
                        val activeUrl = publicUrl ?: privateUrl ?: hardcoreUrl
                        val betaType = when {
                            publicUrl != null -> "public"
                            privateUrl != null -> "private"
                            else -> "hardcore"
                        }
                        val isPublicBeta = publicUrl != null
                        buildConfigFields(
                            listOfNotNull(
                                "BETA_URL" to activeUrl.orEmpty(),
                                "BETA_TYPE" to betaType,
                                "APP_CENTER_HASH" to getPropertyOrNull(
                                    "APP_CENTER_HASH_" + betaType.uppercase()
                                ).orEmpty(),
                                "DEBUG_VERSION" to true,
                                if (isPublicBeta) null else "DEBUG_PRIVATE_VERSION" to true,
                            ).toMap()
                        )
                        gradle.taskGraph.whenReady {
                            if (allTasks.none { it.name.lowercase().contains("beta") }) {
                                return@whenReady
                            }
                            if (listOfNotNull(publicUrl, privateUrl, hardcoreUrl).size == 1) {
                                return@whenReady
                            }
                            throw GradleException(
                                "Beta build requires EXACTLY ONE URL configuration. " +
                                    "Please check your environment and provide a single one of: " +
                                    "BETA_PUBLIC_URL, BETA_PRIVATE_URL, or BETA_HARDCORE_URL."
                            )
                        }
                    }
                    if (setupManifestPlaceholders) {
                        manifestPlaceholders += mapOf(
                            "applicationName" to "org.telegram.messenger.beta.ApplicationLoaderImpl",
                            "applicationLabel" to "@string/AppNameBeta",
                        )
                    }
                }
                create("appTestEnv") {
                    dimension = "distributionType"
                    minSdk = 26
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    if (setupManifestPlaceholders) {
                        manifestPlaceholders += mapOf(
                            "applicationName" to "org.telegram.messenger.apptestenv.ApplicationLoaderImpl",
                        )
                    }
                }
                create("afatApk") {
                    dimension = "outputFormat"
                }
                create("bundle") {
                    dimension = "outputFormat"
                    if (setupBuildConfigs) {
                        buildConfigFields(mapOf("BUNDLE" to true))
                    }
                }
                create("bundleMinApi23") {
                    dimension = "outputFormat"
                    minSdk = 23
                    if (setupBuildConfigs) {
                        buildConfigFields(mapOf("BUNDLE" to true))
                    }
                }
            }

            buildTypes.apply {
                named("debug") {
                    if (setupBuildConfigs) {
                        buildConfigFields(
                            mapOf(
                                "DEBUG_VERSION" to true,
                                "DEBUG_PRIVATE_VERSION" to true,
                            )
                        )
                    }
                }
            }
            if (setupBuildConfigs) {
                buildFeatures.apply {
                    buildConfig = true
                }
            }
            compileOptions.apply {
                sourceCompatibility = JavaVersion.VERSION_21
                targetCompatibility = JavaVersion.VERSION_21
            }
        }
        androidComponents.beforeVariants { variantBuilder ->
            val isRelease = variantBuilder.buildType == "release"

            val flavors = variantBuilder.productFlavors.toMap()
            val distributionType = flavors["distributionType"].orEmpty()

            if (distributionType == "appTestEnv" && isRelease) {
                variantBuilder.disable()
                return@beforeVariants
            }

            val outputFormat = flavors["outputFormat"].orEmpty()
            val isBundle = outputFormat.startsWith("bundle")
            val isGoogle = distributionType == "google"

            if (isBundle && (!isGoogle || !isRelease)) {
                variantBuilder.disable()
                return@beforeVariants
            }
        }
    }
}
