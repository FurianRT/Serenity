package com.furianrt.core

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

inline fun <T, R> Iterable<T>.mapImmutable(transform: (T) -> R): ImmutableList<R> =
    map(transform).toImmutableList()

inline fun <E> buildImmutableList(builderAction: MutableList<E>.() -> Unit): ImmutableList<E> =
    buildList(builderAction).toImmutableList()

inline fun <reified R> Iterable<*>.findInstance(predicate: (R) -> Boolean): R? {
    for (element in this) {
        if (element is R && predicate(element)) {
            return element
        }
    }
    return null
}

fun <T> Iterable<T>.lastIndexOf(predicate: (T) -> Boolean): Int {
    var lastIndex = -1
    for ((index, item) in this.withIndex()) {
        if (predicate(item))
            lastIndex = index
    }
    return lastIndex
}

inline fun <T> List<T>.indexOfFirstOrNull(predicate: (T) -> Boolean): Int? =
    indexOfFirst(predicate).takeIf { it != -1 }

inline fun <T> Iterable<T>.hasItem(predicate: (T) -> Boolean): Boolean = find(predicate) != null