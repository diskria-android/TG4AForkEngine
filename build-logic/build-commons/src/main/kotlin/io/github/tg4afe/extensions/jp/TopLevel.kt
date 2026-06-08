package io.github.tg4afe.extensions.jp

import com.palantir.javapoet.AnnotationSpec
import com.palantir.javapoet.ArrayTypeName
import com.palantir.javapoet.ClassName
import com.palantir.javapoet.CodeBlock
import com.palantir.javapoet.FieldSpec
import com.palantir.javapoet.JavaFile
import com.palantir.javapoet.MethodSpec
import com.palantir.javapoet.ParameterSpec
import com.palantir.javapoet.ParameterizedTypeName
import com.palantir.javapoet.TypeName
import com.palantir.javapoet.TypeSpec
import com.palantir.javapoet.TypeVariableName
import com.palantir.javapoet.WildcardTypeName
import javax.lang.model.element.Modifier

typealias JPCodeBlockBuilder = CodeBlock.Builder
typealias JPCodeBlock = CodeBlock

typealias JPAnnotationBuilder = AnnotationSpec.Builder
typealias JPAnnotation = AnnotationSpec

typealias JPFieldBuilder = FieldSpec.Builder
typealias JPField = FieldSpec

typealias JPMethodBuilder = MethodSpec.Builder
typealias JPMethod = MethodSpec

typealias JPParameterBuilder = ParameterSpec.Builder
typealias JPParameter = ParameterSpec

typealias JPClassBuilder = TypeSpec.Builder
typealias JPClass = TypeSpec

typealias JPFile = JavaFile

typealias JPTypeName = TypeName
typealias JPClassName = ClassName
typealias JPParameterizedTypeName = ParameterizedTypeName
typealias JPWildcardTypeName = WildcardTypeName
typealias JPTypeVariableName = TypeVariableName
typealias JPArrayTypeName = ArrayTypeName

typealias JPModifier = Modifier

val JPBoolean: JPTypeName = JPTypeName.BOOLEAN
val JPByte: JPTypeName = JPTypeName.BYTE
val JPShort: JPTypeName = JPTypeName.SHORT
val JPInt: JPTypeName = JPTypeName.INT
val JPLong: JPTypeName = JPTypeName.LONG
val JPChar: JPTypeName = JPTypeName.CHAR
val JPFloat: JPTypeName = JPTypeName.FLOAT
val JPDouble: JPTypeName = JPTypeName.DOUBLE
val JPVoid: JPTypeName = JPTypeName.VOID

val JPObject: JPClassName = JPClassName.OBJECT
val JPString: JPClassName = JPClassName.get(String::class.java)
val JPList: JPClassName = JPClassName.get(List::class.java)
val JPSet: JPClassName = JPClassName.get(Set::class.java)
val JPMap: JPClassName = JPClassName.get(Map::class.java)

val JPContext: JPClassName = ClassName.get("android.content", "Context")
