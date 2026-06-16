import io.github.tg4afe.extensions.android.requireProperty

plugins {
    alias(libs.plugins.android.library)
    id("android-conventions")
}

android {
    namespace = project.requireProperty("NAMESPACE") + ".third_party.webrtc"
}

dependencies {
    implementation("androidx.core:core:1.16.0")

    implementation(project(":core:logging"))
}
