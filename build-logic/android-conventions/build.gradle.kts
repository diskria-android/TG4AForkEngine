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
    compileOnly("com.android.tools.build:gradle:9.2.1")
    implementation(project(":build-commons"))
}
