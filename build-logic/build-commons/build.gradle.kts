plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("buildCommons") {
            id = "build-commons"
            implementationClass = "io.github.tg4afe.BuildCommonsPlugin"
        }
    }
}

dependencies {
    compileOnly(gradleApi())
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.appgallery.gradle.plugin)

    compileOnly("com.palantir.javapoet:javapoet:0.16.0")
}
