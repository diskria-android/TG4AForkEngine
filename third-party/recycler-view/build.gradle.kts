import io.github.tg4afe.extensions.android.requireProperty

plugins {
    id("com.android.library")
    id("android-conventions")
}

android {
    namespace = project.requireProperty("NAMESPACE") + ".thirdparty.recycler.view"
}

dependencies {
    implementation("androidx.core:core:1.16.0")
    implementation("androidx.biometric:biometric:1.1.0")
}
