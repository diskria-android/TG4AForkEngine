package io.github.tg4afe

import groovy.namespace.QName
import groovy.util.Node
import groovy.util.NodeList
import groovy.xml.XmlParser
import groovy.xml.XmlUtil
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class SetupActivityAliasesTask : DefaultTask() {

    @get:Nested
    abstract val icons: ListProperty<AppIconTaskInput>

    @get:InputFile
    abstract val mergedManifest: RegularFileProperty

    @get:OutputFile
    abstract val updatedManifest: RegularFileProperty

    @TaskAction
    fun transformManifest() {
        val originalFile = mergedManifest.get().asFile
        val outputFile = updatedManifest.get().asFile

        val nameAttr = androidAttr("name")

        val manifestNode = XmlParser().parse(originalFile)
        val applicationNode = manifestNode.getNodesByName("application").singleOrNull()
            ?: throw GradleException("<application> tag not found in manifest")

        val (targetActivityNode, launcherFilterNode) = applicationNode
            .getNodesByName("activity")
            .firstNotNullOfOrNull { activityNode ->
                val intentFilterNodes = activityNode.getNodesByName("intent-filter")
                intentFilterNodes.firstOrNull { intentFilterNode ->
                    val isMainAction = intentFilterNode.getNodesByName("action").any {
                        it.findAttr(nameAttr) == "android.intent.action.MAIN"
                    }
                    val isLauncherCategory = intentFilterNode.getNodesByName("category").any {
                        it.findAttr(nameAttr) == "android.intent.category.LAUNCHER"
                    }
                    isMainAction && isLauncherCategory
                }?.let { activityNode to it }
            } ?: throw GradleException("Target <activity> tag not found in manifest")
        val targetActivityName = targetActivityNode.findAttr(nameAttr)
            ?: throw GradleException("android:name in target <activity> not found in manifest")

        icons.get().forEach { icon ->
            val attrs = buildList {
                addAll(
                    listOf(
                        nameAttr to icon.componentCls,
                        androidAttr("targetActivity") to targetActivityName,
                        androidAttr("enabled") to icon.isDefault.toString(),
                        androidAttr("exported") to "true",
                    )
                )
                if (!icon.isDefault) {
                    addAll(
                        listOf(
                            androidAttr("icon") to icon.icon.resourceReference,
                            androidAttr("roundIcon") to icon.roundIcon.resourceReference,
                        )
                    )
                }
            }
            applicationNode.addNode("activity-alias", attrs) {
                append(launcherFilterNode.copy())
                targetActivityNode.getNodesByName("meta-data").forEach { metaDataNode ->
                    append(metaDataNode.copy())
                }
            }
        }
        targetActivityNode.remove(launcherFilterNode)

        outputFile.parentFile?.mkdirs()
        outputFile.writeText(XmlUtil.serialize(manifestNode))
    }

    private fun Node.getNodesByName(name: String): List<Node> =
        (get(name) as? NodeList)?.filterIsInstance<Node>().orEmpty()

    private fun Node.addNode(
        name: String,
        attrs: List<Pair<AndroidAttrName, String>>,
        builder: Node.() -> Unit
    ) {
        appendNode(name, attrs.toMap()).apply(builder)
    }

    private fun Node.copy(): Node =
        clone() as Node

    private fun Node.findAttr(name: AndroidAttrName): String? =
        attribute(name) as? String

    private fun androidAttr(name: String, tools: Boolean = false): AndroidAttrName =
        if (tools) {
            QToolsName(name)
        } else {
            QAndroidName(name)
        }

    sealed interface AndroidAttrName
    class QAndroidName(name: String) :
        QName("http://schemas.android.com/apk/res/android", name, "android"),
        AndroidAttrName

    class QToolsName(name: String) :
        QName("http://schemas.android.com/tools", name, "tools"),
        AndroidAttrName
}
