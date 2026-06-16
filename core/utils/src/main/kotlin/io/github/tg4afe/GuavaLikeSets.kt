@file:JvmName("Sets")

package io.github.tg4afe

fun <T> newHashSet(vararg values: T): HashSet<T> =
    HashSet(values.toList())
