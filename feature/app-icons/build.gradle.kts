import io.github.tg4afe.extensions.android.requireProperty

plugins {
    id("com.android.library")
    id("android-conventions")
}

android {
    namespace = project.requireProperty("NAMESPACE") + ".feature.app.icons"
}
