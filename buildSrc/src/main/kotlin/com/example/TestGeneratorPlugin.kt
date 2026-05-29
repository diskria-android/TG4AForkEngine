package com.example

import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class TestGeneratorPlugin : Plugin<Project> {

    override fun apply(project: Project) {

        val generateSchemeTask = project.tasks.register(
            "generateScheme",
            GenerateSchemeTask::class.java
        ) {
            tlSourcesDir = File(project.rootDir, "TMessagesProj/src/main/java/org/telegram/tgnet")
            tlSourcesDirectDir = File(project.rootDir, /* forky comment "TMessagesProj_AppStandalone/src/main/java/org/telegram/tgnet" */ /* forky code start */ "TMessagesProj/src/direct/java/org/telegram/tgnet" /* forky code end */)
            resourcesDir = /* forky comment project.file("tlscheme") */ /* forky code start */ File(project.rootDir, "buildSrc/tlscheme") /* forky code end */
            outputDir = project.file(/* forky comment "src/androidTest/kotlin" */ /* forky code start */ "src/androidTestAppTestEnv/kotlin" /* forky code end */)
        }

        project.afterEvaluate {
            project.tasks.matching {
                val name = it.name
                name.contains("preBuild")
            }.configureEach {
                // forky comment line println("🔗 Hooking generateTests before: ${this.name}")
                dependsOn(generateSchemeTask)
            }
        }

    }
}