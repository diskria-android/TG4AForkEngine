package io.github.tg4afe

import com.android.build.api.variant.ComponentIdentity
import com.huawei.agconnect.agcp.ConfigFileLocation
import com.huawei.agconnect.crash.symbol.lib.log.AGCLogger
import groovy.lang.ExpandoMetaClass
import io.github.tg4afe.extensions.android.getFlavorDirNames
import org.codehaus.groovy.runtime.InvokerHelper
import org.codehaus.groovy.runtime.MethodClosure
import kotlin.reflect.KClass

object AGCPatches {

    class ConfigFileLocationPatch private constructor(
        targetClass: Class<*>,
        val getVariant: (String) -> ComponentIdentity
    ) : ExpandoMetaClass(targetClass, true, true) {

        @Suppress("unused")
        private inner class Interceptor {
            fun getLocations(variantName: String): List<String> {
                val variant = getVariant(variantName)
                val distributionType = variant.productFlavors.toMap()["distributionType"].orEmpty()
                return if (distributionType == "huawei") {
                    variant
                        .getFlavorDirNames(dimensionFilter = "distributionType")
                        .map { "config/$it" }
                } else {
                    emptyList()
                }
            }
        }

        init {
            val interceptor = Interceptor()
            registerStaticMethod("getLocations", MethodClosure(interceptor, "getLocations"))
            initialize()
        }

        companion object {
            fun apply(variantProvider: (String) -> ComponentIdentity) {
                InvokerHelper.getMetaRegistry().setMetaClass(
                    ConfigFileLocation::class.java,
                    ConfigFileLocationPatch(ConfigFileLocation::class.java, variantProvider)
                )
            }
        }
    }

    class LoggerPatch private constructor(
        targetClass: Class<*>
    ) : ExpandoMetaClass(targetClass, true, true) {

        @Suppress("unused")
        private class Interceptor {
            fun info(message: String) {}
            fun warn(message: String) {}
        }

        init {
            val interceptor = Interceptor()
            registerStaticMethod("info", MethodClosure(interceptor, "info"))
            registerStaticMethod("warn", MethodClosure(interceptor, "warn"))
            initialize()
        }

        companion object {
            fun apply() {
                InvokerHelper.getMetaRegistry().setMetaClass(
                    AGCLogger::class.java,
                    LoggerPatch(AGCLogger::class.java)
                )
            }
        }
    }

    fun applyAll(variantProvider: (String) -> ComponentIdentity) {
        ConfigFileLocationPatch.apply(variantProvider)
        LoggerPatch.apply()
    }
}
