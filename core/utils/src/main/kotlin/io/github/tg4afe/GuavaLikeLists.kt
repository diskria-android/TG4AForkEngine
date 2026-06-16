@file:JvmName("Lists")

package io.github.tg4afe

fun <T> newArrayList(vararg values: T): ArrayList<T> =
    ArrayList(values.toList())
