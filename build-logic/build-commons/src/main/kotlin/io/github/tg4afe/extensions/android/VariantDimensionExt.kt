package io.github.tg4afe.extensions.android

import com.android.build.api.dsl.VariantDimension
import io.github.tg4afe.extensions.quoted
import org.gradle.api.GradleException

fun VariantDimension.buildConfigFields(fields: Map<String, Any>) {
    fields.forEach { (name, rawValue) ->
        val (type, value) = when (rawValue) {
            is Boolean -> "boolean" to rawValue.toString()
            is Int -> "int" to rawValue.toString()
            is Long -> "long" to "${rawValue}L"
            is Float -> "float" to "${rawValue}f"
            is Double -> "double" to rawValue.toString()
            is String -> "String" to rawValue.quoted()
            else -> throw GradleException(
                "Unknown build config field type '${rawValue::class.simpleName}' for field '$name'"
            )
        }
        buildConfigField(type, name, value)
    }
}
