plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("androidConventions") {
            id = "android-conventions"
            implementationClass = "io.github.tg4afe.AndroidConventionsPlugin"
        }
    }
}

dependencies {
    compileOnly(gradleApi())
    compileOnly(libs.android.plugin)
    implementation(project(":build-commons"))
}
