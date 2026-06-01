import com.example.ProductFlavors
import com.example.requireProperty

plugins {
    id("com.android.library")
}

repositories {
    mavenCentral()
    google()
}

configurations.configureEach {
    exclude("androidx.recyclerview", "recyclerview")
}

ProductFlavors.configure(project, setupBuildConfigs = true)
android {
    namespace = project.requireProperty("NAMESPACE") + ".ui.recycler.view"
}

dependencies {
    implementation("androidx.core:core:1.16.0")
    implementation("androidx.biometric:biometric:1.1.0")
}
