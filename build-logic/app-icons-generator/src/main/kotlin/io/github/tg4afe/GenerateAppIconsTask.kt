package io.github.tg4afe

import com.commit451.resourcespoet.ResourcesPoet
import com.palantir.javapoet.ClassName
import com.palantir.javapoet.CodeBlock
import com.palantir.javapoet.JavaFile
import com.palantir.javapoet.MethodSpec
import com.palantir.javapoet.TypeSpec
import io.github.tg4afe.extensions.jp.JPBoolean
import io.github.tg4afe.extensions.jp.JPClassName
import io.github.tg4afe.extensions.jp.JPContext
import io.github.tg4afe.extensions.jp.JPInt
import io.github.tg4afe.extensions.jp.JPModifier
import io.github.tg4afe.extensions.jp.JPString
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class GenerateAppIconsTask : DefaultTask() {

    @get:Nested
    abstract val icons: ListProperty<AppIconTaskInput>

    @get:OutputDirectory
    abstract val resOutputDir: DirectoryProperty

    @get:OutputDirectory
    abstract val javaOutputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        generateStringsXml()
        generateControllerClass()
    }

    private fun generateStringsXml() {
        val valuesFolder = resOutputDir.get().dir("values").asFile
        valuesFolder.mkdirs()
        val stringsXmlFile = File(valuesFolder, "strings.xml")
        ResourcesPoet.create().apply {
            icons.get().forEach { icon ->
                addString(icon.stringResourceName, icon.name, translatable = false)
            }
            indent(true)
        }.build(stringsXmlFile)
    }

    private fun generateControllerClass() {
        val defaultIconName = icons.get().first { it.isDefault }
        val controllerClassName = ClassName.get("org.telegram.ui", "LauncherIconController")
        val enumClassName = controllerClassName.nestedClass("LauncherIcon")
        val enumClass = buildEnumClass(enumClassName)
        val controllerClass = TypeSpec.classBuilder(controllerClassName).apply {
            addModifiers(JPModifier.PUBLIC)
            addType(enumClass)

            val packageManagerClassName = ClassName.get("android.content.pm", "PackageManager")
            addMethod(
                MethodSpec.methodBuilder("setIcon").apply {
                    addModifiers(JPModifier.PUBLIC, JPModifier.STATIC)
                    addParameter(JPContext, "context")
                    addParameter(enumClassName, "icon")
                    addStatement(
                        $$"$T packageManager = context.getPackageManager()",
                        packageManagerClassName
                    )
                    beginControlFlow("for (LauncherIcon launcherIcon : LauncherIcon.values())")
                    addStatement(
                        $$"packageManager.setComponentEnabledSetting(launcherIcon.getComponentName(context), launcherIcon == icon ? $T.COMPONENT_ENABLED_STATE_ENABLED : $T.COMPONENT_ENABLED_STATE_DISABLED, $T.DONT_KILL_APP)",
                        packageManagerClassName,
                        packageManagerClassName,
                        packageManagerClassName
                    )
                    endControlFlow()
                }.build()
            )
            addMethod(
                MethodSpec.methodBuilder("isEnabled").apply {
                    addModifiers(JPModifier.PUBLIC, JPModifier.STATIC)
                    returns(JPBoolean)
                    addParameter(JPContext, "context")
                    addParameter(enumClassName, "icon")
                    addStatement(
                        $$"$T state = context.getPackageManager().getComponentEnabledSetting(icon.getComponentName(context))",
                        JPInt
                    )
                    addStatement(
                        $$"return state == $T.COMPONENT_ENABLED_STATE_ENABLED || state == $T.COMPONENT_ENABLED_STATE_DEFAULT && icon == $T.$L",
                        packageManagerClassName,
                        packageManagerClassName,
                        enumClassName,
                        defaultIconName.enumName,
                    )
                }.build()
            )
            addMethod(
                MethodSpec.methodBuilder("tryFixLauncherIconIfNeeded").apply {
                    addModifiers(JPModifier.PUBLIC, JPModifier.STATIC)
                    addParameter(JPContext, "context")
                    beginControlFlow($$"for ($T icon : $T.values())", enumClassName, enumClassName)
                    addStatement("if (isEnabled(context, icon)) return")
                    endControlFlow()
                    addStatement(
                        $$"setIcon(context, $T.$L)",
                        enumClassName,
                        defaultIconName.enumName
                    )
                }.build()
            )
        }.build()
        val javaFile = JavaFile.builder("org.telegram.ui", controllerClass).apply {
            indent("    ")
        }.build()
        javaFile.writeToFile(javaOutputDir.get().asFile)
    }

    private fun buildEnumClass(className: JPClassName): TypeSpec =
        TypeSpec.enumBuilder(className).apply {
            addModifiers(JPModifier.PUBLIC)
            val rClassName = ClassName.get("org.telegram.messenger.feature.app_icons", "R")
            icons.get().forEach { icon ->
                val argumentsCodeBlock = CodeBlock.of(
                    $$"$S, $T.$L, $T.$L, $T.string.$L, $L",
                    icon.componentCls,
                    rClassName, icon.background.codeReference,
                    rClassName, icon.foreground.codeReference,
                    rClassName, icon.stringResourceName,
                    icon.isPremium,
                )
                addEnumConstant(
                    icon.enumName,
                    TypeSpec.anonymousClassBuilder(argumentsCodeBlock).build()
                )
            }
            addField(JPString, "componentCls", JPModifier.PUBLIC, JPModifier.FINAL)
            addField(JPInt, "background", JPModifier.PUBLIC, JPModifier.FINAL)
            addField(JPInt, "foreground", JPModifier.PUBLIC, JPModifier.FINAL)
            addField(JPInt, "title", JPModifier.PUBLIC, JPModifier.FINAL)
            addField(JPBoolean, "premium", JPModifier.PUBLIC, JPModifier.FINAL)
            addMethod(
                MethodSpec.constructorBuilder().apply {
                    addParameter(JPString, "componentCls")
                    addStatement("this.componentCls = componentCls")
                    addParameter(JPInt, "background")
                    addStatement("this.background = background")
                    addParameter(JPInt, "foreground")
                    addStatement("this.foreground = foreground")
                    addParameter(JPInt, "title")
                    addStatement("this.title = title")
                    addParameter(JPBoolean, "premium")
                    addStatement("this.premium = premium")
                }.build()
            )

            val componentNameClassName = ClassName.get("android.content", "ComponentName")
            addField(componentNameClassName, "componentName", JPModifier.PRIVATE)
            addMethod(
                MethodSpec.methodBuilder("getComponentName").apply {
                    addModifiers(JPModifier.PUBLIC)
                    returns(componentNameClassName)
                    addParameter(JPContext, "context")
                    beginControlFlow("if (componentName == null)")
                    addStatement("componentName = new ComponentName(context.getPackageName(), componentCls)")
                    endControlFlow()
                    addStatement("return componentName")
                }.build()
            )
        }.build()
}
