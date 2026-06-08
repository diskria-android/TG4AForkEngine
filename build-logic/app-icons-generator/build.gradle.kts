plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("appIconsGenerator") {
            id = "app-icons-generator"
            implementationClass = "io.github.tg4afe.AppIconsGeneratorPlugin"
        }
    }
}

dependencies {
    compileOnly(gradleApi())
    compileOnly("com.android.tools.build:gradle:9.2.1")
    implementation("com.commit451:resourcespoet:2.3.1")
    implementation("com.palantir.javapoet:javapoet:0.16.0")

    implementation(project(":build-commons"))
}
