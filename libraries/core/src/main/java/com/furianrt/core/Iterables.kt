package com.furianrt.core

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

inline fun <T, R> Iterable<T>.mapImmutable(transform: (T) -> R): ImmutableList<R> =
    map(transform).toImmutableList()

inline fun <E> buildImmutableList(builderAction: MutableList<E>.() -> Unit): ImmutableList<E> =
    buildList(builderAction).toImmutableList()
