package com.furianrt.core

inline fun <reified R> Iterable<*>.findInstance(predicate: (R) -> Boolean = { true }): R? {
    for (element in this) {
        if (element is R && predicate(element)) {
            return element
        }
    }
    return null
}

inline fun <T> List<T>.indexOfFirstOrNull(predicate: (T) -> Boolean): Int? =
    indexOfFirst(predicate).takeIf { it != -1 }

fun <T> Collection<T>?.deepEqualTo(
    other: Collection<T>?,
    comparator: (old: T, new: T) -> Boolean = { old, new -> old == new },
): Boolean {
    if (this == other) {
        return true
    }
    if (this?.size != other?.size) {
        return false
    }
    return orEmpty().asSequence()
        .zip(other.orEmpty().asSequence())
        .all { (old, new) -> comparator(old, new) }
}