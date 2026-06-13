import com.android.build.api.variant.ApplicationVariantBuilder
import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import com.google.gms.googleservices.GoogleServicesTask
import io.github.tg4afe.AGCPatches
import io.github.tg4afe.appIcons
import io.github.tg4afe.drawableRes
import io.github.tg4afe.extensions.android.getFileLocations
import io.github.tg4afe.extensions.android.getPropertyOrNull
import io.github.tg4afe.extensions.android.requireProperty
import io.github.tg4afe.extensions.capitalized
import io.github.tg4afe.mipmapRes
import io.github.tg4afe.register

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.appgallery.connect)

    id("android-conventions")
    id("app-icons-generator")
    id("test-generator") apply false
}

android {
    namespace = project.requireProperty("NAMESPACE")

    lint {
        disable += setOf(
            "BlockedPrivateApi",
            "OldTargetApi",
        )
        checkReleaseBuilds = false
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }

    signingConfigs {
        create("release") {
            storeFile = file("config/release.keystore")
            storePassword = project.getPropertyOrNull("RELEASE_KEYSTORE_PASSWORD")
            keyAlias = project.getPropertyOrNull("RELEASE_KEY_ALIAS")
            keyPassword = project.getPropertyOrNull("RELEASE_KEYSTORE_PASSWORD")
        }
    }

    defaultConfig {
        targetSdk = 35
        applicationId = project.requireProperty("APP_ID")
        versionName = project.requireProperty("APP_VERSION_NAME")

        proguardFiles += listOf(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            file("config/proguard-rules.pro"),
        )

        appIcons {
            register("default") {
                icon = "ic_launcher".mipmapRes()
                roundIcon = "ic_launcher_round".mipmapRes()
                background = "icon_background_sa".drawableRes()
                foreground = "icon_foreground_sa".mipmapRes()
                default = true
            }
            register("vintage") {
                icon = "icon_6_launcher".mipmapRes()
                roundIcon = "icon_6_launcher_round".mipmapRes()
                background = "icon_6_background_sa".drawableRes()
                foreground = "icon_6_foreground_sa".mipmapRes()
            }
            register("aqua") {
                icon = "icon_4_launcher".mipmapRes()
                roundIcon = "icon_4_launcher_round".mipmapRes()
                background = "icon_4_background_sa".drawableRes()
                foreground = "icon_foreground_sa".mipmapRes()
            }
            register("premium") {
                icon = "icon_3_launcher".mipmapRes()
                roundIcon = "icon_3_launcher_round".mipmapRes()
                background = "icon_3_background_sa".drawableRes()
                foreground = "icon_3_foreground_sa".mipmapRes()
                premium = true
            }
            register("turbo") {
                icon = "icon_5_launcher".mipmapRes()
                roundIcon = "icon_5_launcher_round".mipmapRes()
                background = "icon_5_background_sa".drawableRes()
                foreground = "icon_5_foreground_sa".mipmapRes()
                premium = true
            }
            register("nox") {
                icon = "icon_2_launcher".mipmapRes()
                roundIcon = "icon_2_launcher_round".mipmapRes()
                background = "icon_2_background_sa".mipmapRes()
                foreground = "icon_foreground_sa".mipmapRes()
                premium = true
            }
        }
    }

    productFlavors {
        configureEach {
            configure<CrashlyticsExtension> {
                mappingFileUploadEnabled = false
            }
        }
        named("google") {
            isDefault = true
        }
        named("direct") {
            applicationIdSuffix = ".web"
            appIcons {
                getByName("default") {
                    icon = "ic_launcher_sa".mipmapRes()
                    roundIcon = "ic_launcher_sa".mipmapRes()
                }
                getByName("vintage") {
                    icon = "icon_6_launcher_sa".mipmapRes()
                    roundIcon = "icon_6_launcher_sa".mipmapRes()
                }
                getByName("aqua") {
                    icon = "icon_4_launcher_sa".mipmapRes()
                    roundIcon = "icon_4_launcher_sa".mipmapRes()
                }
                getByName("premium") {
                    icon = "icon_3_launcher_sa".mipmapRes()
                    roundIcon = "icon_3_launcher_sa".mipmapRes()
                }
                getByName("turbo") {
                    icon = "icon_5_launcher_sa".mipmapRes()
                    roundIcon = "icon_5_launcher_sa".mipmapRes()
                }
                getByName("nox") {
                    icon = "icon_2_launcher_sa".mipmapRes()
                    roundIcon = "icon_2_launcher_sa".mipmapRes()
                }
            }
        }
        named("beta") {
            applicationIdSuffix = ".beta"
            configure<CrashlyticsExtension> {
                mappingFileUploadEnabled = true
            }
        }
        named("afatApk") {
            versionCode = project.requireProperty("APP_VERSION_CODE").toInt() * 10 + 9
            isDefault = true
        }
        named("bundle") {
            versionCode = project.requireProperty("APP_VERSION_CODE").toInt() * 10 + 1
        }
        named("bundleMinApi23") {
            versionCode = project.requireProperty("APP_VERSION_CODE").toInt() * 10 + 2
        }
    }

    buildTypes {
        debug {
            configure<CrashlyticsExtension> {
                mappingFileUploadEnabled = false
            }
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
        }
    }
}

val variants = mutableMapOf<String, ApplicationVariantBuilder>()
androidComponents {
    beforeVariants { variantBuilder ->
        if (variantBuilder.enable) {
            variants[variantBuilder.name] = variantBuilder
        }
    }
    onVariants { variant ->
        val taskVariantPart = variant.name.capitalized()
        tasks.matching { it.name == "process${taskVariantPart}GoogleServices" }.configureEach {
            val task = this as GoogleServicesTask
            val fileName = GoogleServicesTask.JSON_FILE_NAME

            val candidateFiles = variant.getFileLocations(fileName).map { file("config/$it") }
            val existingFile = candidateFiles.firstOrNull { it.exists() }
                ?: throw GradleException("$fileName not found in $candidateFiles")
            task.googleServicesJsonFiles.set(listOf(existingFile))
        }

        val distributionType = variant.productFlavors.toMap()["distributionType"].orEmpty()
        tasks.matching {
            it.name == "process${taskVariantPart}AGCPlugin"
        }.configureEach {
            enabled = distributionType == "huawei"
        }

        tasks.matching {
            it.name == "injectCrashlyticsMappingFileId$taskVariantPart" ||
                it.name == "injectCrashlyticsVersionControlInfo$taskVariantPart"
        }.configureEach {
            enabled = distributionType == "beta" && variant.buildType != "debug"
        }

        if (distributionType == "appTestEnv") {
            plugins.apply("test-generator")
        }
    }
}

AGCPatches.applyAll { variants.getValue(it) }

configurations.configureEach {
    exclude("androidx.recyclerview", "recyclerview")
}

dependencies {
    // AndroidX & Backward Compatibility
    implementation("androidx.core:core:1.16.0")
    implementation("androidx.dynamicanimation:dynamicanimation:1.0.0")
    implementation("androidx.palette:palette:1.0.0")
    implementation("androidx.sharetarget:sharetarget:1.2.0")
    implementation("androidx.fragment:fragment:1.8.9")

    // Photo Editor/Processing & QR Scan
    implementation("androidx.exifinterface:exifinterface:1.3.6")
    implementation("com.google.android.gms:play-services-mlkit-subject-segmentation:16.0.0-beta1")
    implementation("com.google.android.gms:play-services-mlkit-image-labeling:16.0.8")
    implementation("com.google.android.gms:play-services-vision:20.1.3")

    // Passkey TODO Remove
    implementation("androidx.credentials:credentials:1.6.0-alpha04")
    implementation("androidx.credentials:credentials-play-services-auth:1.6.0-alpha04")

    // Google Play Billing for Telegram Premium, Stars, etc.
    implementation("com.android.billingclient:billing:7.1.1")
    constraints {
        implementation("com.google.android.gms:play-services-location:21.0.1")
    }

    // FCM
    implementation("com.google.firebase:firebase-messaging:22.0.0")
    constraints {
        implementation("com.google.firebase:firebase-datatransport:18.1.0")
    }

    // Google Assistant
    implementation("com.google.firebase:firebase-appindexing:20.0.0")

    // API
    implementation("com.google.firebase:firebase-config:21.0.1")

    // Sign in with Google
    implementation("com.google.android.gms:play-services-auth:20.4.0")

    // Language Detector
    implementation("com.google.mlkit:language-id:17.0.6")

    // CaptchaController
    implementation("com.google.android.recaptcha:recaptcha:18.7.1") // transitive: Play Integrity

    // File Logging
    implementation("com.google.code.gson:gson:2.11.0")

    // ExoPlayer
    compileOnly("org.checkerframework:checker-qual:2.5.2")
    compileOnly("org.checkerframework:checker-compat-qual:2.5.0")
    implementation("com.google.guava:guava:31.1-android")

    // org.telegram.messenger.video
    implementation("com.googlecode.mp4parser:isoparser:1.0.6")

    // Payment
    implementation("com.google.android.gms:play-services-wallet:19.1.0")
    constraints {
        implementation("com.google.android.gms:play-services-maps:18.1.0")
    }
    implementation("com.stripe:stripe-android:2.0.2")

    // Chrome Cast
    implementation("com.google.android.gms:play-services-cast-framework:21.4.0")
    implementation("org.nanohttpd:nanohttpd:2.3.1")
    constraints {
        implementation("androidx.mediarouter:mediarouter:1.7.0")
    }

    // MarkdownParser
    implementation("io.noties.markwon:core:4.6.2")
    implementation("io.noties.markwon:ext-strikethrough:4.6.2")
    implementation("io.noties.markwon:ext-tables:4.6.2")
    implementation("io.noties.markwon:html:4.6.2")
    implementation("io.noties.markwon:inline-parser:4.6.2")
    implementation("io.noties.markwon:ext-latex:4.6.2")

    implementation(files("libs/libgsaverification-client.aar"))

    // [Huawei Only] HMS
    huaweiImplementation("com.huawei.hms:push:6.5.0.300")
    huaweiImplementation("com.huawei.hms:maps:6.6.0.300")
    huaweiImplementation("com.huawei.hms:location:6.4.0.300")

    // [Beta Only] In-App Updates
    betaImplementation("com.microsoft.appcenter:appcenter-distribute:3.3.1")
    betaImplementation("com.microsoft.appcenter:appcenter-crashes:3.3.1")
    betaImplementation("com.microsoft.appcenter:appcenter-analytics:3.3.1")

    // [Beta Only] Crash Reporting
    betaImplementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    betaImplementation("com.google.firebase:firebase-crashlytics:18.6.0")

    // [AppTestEnv Only] Instrumented Tests
    androidTestAppTestEnvImplementation("junit:junit:4.13.2")
    androidTestAppTestEnvImplementation("androidx.test.ext:junit:1.1.5")
    androidTestAppTestEnvImplementation("androidx.test:runner:1.5.2")
    androidTestAppTestEnvImplementation("org.jetbrains.kotlin:kotlin-test:2.4.0")
    androidTestAppTestEnvImplementation("org.jetbrains.kotlin:kotlin-test-junit:2.4.0")
    androidTestAppTestEnvImplementation("com.appmattus.fixture:fixture:1.2.0")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")

    implementation(project(":native-core"))
    implementation(project(":third-party:recycler-view"))
    implementation(project(":feature:app-icons"))
}
