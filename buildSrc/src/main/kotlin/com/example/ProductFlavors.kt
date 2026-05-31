package com.example

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.GradleException
import org.gradle.api.Project

object ProductFlavors {
    @JvmStatic
    fun configure(
        project: Project,
        setupBuildConfigs: Boolean = false,
        setupManifestPlaceholders: Boolean = false,
    ) {
        val androidExtension = project.extensions.findByName("android")
            as? CommonExtension<*, *, *, *, *, *>
            ?: throw GradleException("Android plugin is not applied to project '${project.name}'")

        androidExtension.apply {
            flavorDimensions += listOf("distributionType", "outputFormat")
            productFlavors {
                create("google") {
                    dimension = "distributionType"
                    if (setupBuildConfigs) {
                        val appId = project.requireProperty("APP_ID")
                        buildConfigField(
                            "String",
                            "APP_UPDATE_URL",
                            "https://play.google.com/store/apps/details?id=$appId".quoted()
                        )
                    }
                    if (setupManifestPlaceholders) {
                        manifestPlaceholders += "applicationName" to "org.telegram.messenger.google.ApplicationLoaderImpl"
                    }
                }
                create("huawei") {
                    dimension = "distributionType"
                    if (setupBuildConfigs) {
                        val huaweiAppId = project.requireProperty("HUAWEI_APP_ID")
                        buildConfigField("String", "HUAWEI_APP_ID", huaweiAppId.quoted())
                        buildConfigField(
                            "String",
                            "APP_UPDATE_URL",
                            "https://appgallery.huawei.com/app/C$huaweiAppId".quoted()
                        )
                    }
                    if (setupManifestPlaceholders) {
                        manifestPlaceholders += "applicationName" to "org.telegram.messenger.HuaweiApplicationLoader"
                    }
                }
                create("direct") {
                    dimension = "distributionType"
                    if (setupBuildConfigs) {
                        buildConfigField(
                            "String",
                            "APP_UPDATE_URL",
                            "https://telegram.org/dl/android/apk".quoted()
                        )
                    }
                    if (setupManifestPlaceholders) {
                        manifestPlaceholders += mapOf("applicationName" to "org.telegram.messenger.direct.ApplicationLoaderImpl")
                    }
                }
                create("beta") {
                    dimension = "distributionType"
                    if (setupBuildConfigs) {
                        val publicUrl = project.getPropertyOrNull("BETA_PUBLIC_URL")
                        val privateUrl = project.getPropertyOrNull("BETA_PRIVATE_URL")
                        val hardcoreUrl = project.getPropertyOrNull("BETA_HARDCORE_URL")
                        val activeUrl = publicUrl ?: privateUrl ?: hardcoreUrl
                        buildConfigField("String", "BETA_URL", activeUrl.orEmpty().quoted())
                        val type = when {
                            publicUrl != null -> "public"
                            privateUrl != null -> "private"
                            else -> "hardcore"
                        }
                        buildConfigField(
                            "String",
                            "APP_CENTER_HASH",
                            project.getPropertyOrNull("APP_CENTER_HASH_${type.uppercase()}")
                                .orEmpty().quoted()
                        )
                        buildConfigField("String", "BETA_TYPE", type.quoted())
                        buildConfigField("boolean", "DEBUG_VERSION", "true")
                        if (publicUrl == null) {
                            buildConfigField("boolean", "DEBUG_PRIVATE_VERSION", "true")
                        }
                        project.gradle.taskGraph.whenReady {
                            if (allTasks.any { it.name.lowercase().contains("beta") }) {
                                if (listOfNotNull(publicUrl, privateUrl, hardcoreUrl).size != 1) {
                                    throw GradleException(
                                        "Beta build requires EXACTLY ONE URL configuration. " +
                                            "Please check your env/properties and provide a single one of: " +
                                            "BETA_PUBLIC_URL, BETA_PRIVATE_URL, or BETA_HARDCORE_URL."
                                    )
                                }
                            }
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
                        manifestPlaceholders += "applicationName" to "org.telegram.messenger.apptestenv.ApplicationLoaderImpl"
                    }
                }
                create("afatApk") {
                    dimension = "outputFormat"
                }
                create("bundle") {
                    dimension = "outputFormat"
                    if (setupBuildConfigs) {
                        buildConfigField("boolean", "BUNDLE", "true")
                    }
                }
                create("bundleMinApi23") {
                    dimension = "outputFormat"
                    minSdk = 23
                    if (setupBuildConfigs) {
                        buildConfigField("boolean", "BUNDLE", "true")
                    }
                }
            }
        }
    }
}
