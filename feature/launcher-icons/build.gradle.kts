import com.example.ProductFlavors
import com.example.requireProperty

plugins {
    id("com.android.library")
}

repositories {
    mavenCentral()
    google()
}

ProductFlavors.configure(project)
android {
    namespace = project.requireProperty("NAMESPACE") + ".feature.launcher.icons"
}
