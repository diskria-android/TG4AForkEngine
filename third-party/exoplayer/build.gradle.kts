import io.github.tg4afe.extensions.android.requireProperty

plugins {
    alias(libs.plugins.android.library)
    id("android-conventions")
}

android {
    namespace = project.requireProperty("NAMESPACE") + ".third_party.exoplayer"
}

dependencies {
    implementation("androidx.mediarouter:mediarouter:1.7.0")

    api("com.google.guava:guava:31.1-android")
    compileOnlyApi("org.checkerframework:checker-qual:2.5.2")
    compileOnlyApi("org.checkerframework:checker-compat-qual:2.5.0")

    implementation(project(":core:utils"))
    implementation(project(":core:multithreading"))
    implementation(project(":core:logging"))
    implementation(project(":core:cpp"))
}
