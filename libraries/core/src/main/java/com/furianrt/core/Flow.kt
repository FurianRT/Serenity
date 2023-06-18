package com.furianrt.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

inline fun <T, R> Flow<Iterable<T>>.deepMap(
    crossinline transform: (value: T) -> R,
): Flow<List<R>> = map { it.map(transform) }
