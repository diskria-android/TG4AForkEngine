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
    compileOnly(libs.android.plugin)
    compileOnly(libs.appgallery.connect.plugin)

    compileOnly("com.palantir.javapoet:javapoet:0.16.0")
}
