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
    compileOnly("com.android.tools.build:gradle:9.2.1")

    compileOnly("com.palantir.javapoet:javapoet:0.16.0")
}
