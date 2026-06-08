package io.github.tg4afe

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.Variant
import io.github.tg4afe.extensions.android.android
import io.github.tg4afe.extensions.android.androidComponents
import io.github.tg4afe.extensions.capitalized
import org.gradle.api.GradleException
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.register

class AppIconsGeneratorPlugin : Plugin<Project> {

    override fun apply(project: Project) = with(project) {
        val iconInputsMap = mutableMapOf<String, List<AppIconTaskInput>>()
        configureApplication(project, iconInputsMap)
        configureLibrary(project(":feature:app-icons"), iconInputsMap)
    }

    private fun configureApplication(
        project: Project,
        iconInputsMap: MutableMap<String, List<AppIconTaskInput>>
    ) = with(project) {
        val defaultExtension = objects.newInstance<AppIconsListExtension>()
        val flavorContainers = mutableMapOf<String, NamedDomainObjectContainer<AppIconConfig>>()
        android {
            (defaultConfig as ExtensionAware)
                .extensions.add(APP_ICONS_EXTENSION_NAME, defaultExtension)
            productFlavors.configureEach {
                val flavorContainer = objects.domainObjectContainer(AppIconConfig::class.java)
                extensions.add(APP_ICONS_EXTENSION_NAME, flavorContainer)
                flavorContainers[name] = flavorContainer
            }
        }
        defaultExtension.list.whenObjectAdded {
            flavorContainers.values.forEach { flavorContainer ->
                flavorContainer.maybeCreate(name).copyFrom(this)
            }
        }
        androidComponents.onVariants { variant ->
            val activeFlavorName = variant.productFlavors.firstOrNull()?.second
            val iconConfigs = defaultExtension.list.map { defaultIcon ->
                val finalConfig = defaultIcon.cloneConfig()
                if (activeFlavorName != null) {
                    flavorContainers[activeFlavorName]
                        ?.findByName(defaultIcon.name)
                        ?.let { finalConfig.mergeFrom(it) }
                }
                finalConfig
            }
            if (iconConfigs.isEmpty()) {
                throw GradleException("At least one app icon is required")
            }
            val singleIcon = iconConfigs.singleOrNull()
            if (singleIcon != null) {
                variant.addDefaultIconManifestPlaceholders(
                    singleIcon.validateIcon().resourceReference,
                    singleIcon.validateRoundIcon().resourceReference
                )
            } else {
                val iconTaskInputs = iconConfigs.map { it.mapToTaskInput() }
                val defaultIcon = iconTaskInputs.singleOrNull { it.isDefault }
                if (defaultIcon == null) {
                    throw GradleException("One and only one default app icon is required")
                }
                variant.addDefaultIconManifestPlaceholders(
                    defaultIcon.icon.resourceReference,
                    defaultIcon.roundIcon.resourceReference
                )
                val setupActivityAliasesTask = tasks.register<SetupActivityAliasesTask>(
                    "setup${variant.name.capitalized()}ActivityAliases"
                ) {
                    icons.set(iconTaskInputs)
                }
                variant.artifacts
                    .use(setupActivityAliasesTask)
                    .wiredWithFiles({ it.mergedManifest }, { it.updatedManifest })
                    .toTransform(SingleArtifact.MERGED_MANIFEST)
                iconInputsMap[variant.name] = iconTaskInputs
            }
        }
    }

    private fun Variant.addDefaultIconManifestPlaceholders(icon: String, roundIcon: String) {
        manifestPlaceholders.putAll(
            mapOf(
                "defaultIcon" to icon,
                "defaultRoundIcon" to roundIcon,
            )
        )
    }

    private fun configureLibrary(
        project: Project,
        iconInputsMap: Map<String, List<AppIconTaskInput>>
    ) = with(project) {
        plugins.withId("com.android.library") {
            androidComponents.onVariants { variant ->
                val generateAppIconsTask = tasks.register<GenerateAppIconsTask>(
                    "generate${variant.name.capitalized()}AppIcons"
                ) {
                    val iconsProvider = provider { iconInputsMap[variant.name] ?: emptyList() }
                    icons.set(iconsProvider)
                    resOutputDir.set(layout.buildDirectory.dir("generated/res/${variant.name}"))
                    javaOutputDir.set(layout.buildDirectory.dir("generated/java/${variant.name}"))
                    onlyIf { iconsProvider.get().isNotEmpty() }
                }
                with(variant.sources) {
                    res?.addGeneratedSourceDirectory(generateAppIconsTask) { it.resOutputDir }
                    java?.addGeneratedSourceDirectory(generateAppIconsTask) { it.javaOutputDir }
                }
            }
        }
    }

    private fun AppIconConfig.mapToTaskInput(): AppIconTaskInput =
        AppIconTaskInput(
            name = name,
            icon = validateIcon().mapToTaskInput(),
            roundIcon = validateRoundIcon().mapToTaskInput(),
            background = validateBackground().mapToTaskInput(),
            foreground = validateForeground().mapToTaskInput(),
            isDefault = default,
            isPremium = premium,
        )

    private fun IconResource.mapToTaskInput(): IconResourceTaskInput =
        IconResourceTaskInput(
            resourceReference = resourceReference,
            codeReference = "${type.value}.$name"
        )

    private val IconResource.resourceReference: String
        get() = "@${type.value}/$name"

    companion object {
        internal const val APP_ICONS_EXTENSION_NAME = "appIcons"
    }
}
