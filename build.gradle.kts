plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    id("com.google.gms.google-services") version "4.4.4" apply false
    alias(libs.plugins.appgallery) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    id("io.github.forky") version "0.1.0"
}
