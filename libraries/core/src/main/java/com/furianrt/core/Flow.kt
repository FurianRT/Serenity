package com.furianrt.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

inline fun <T, R> Flow<Iterable<T>>.deepMap(
    crossinline transform: (value: T) -> R,
): Flow<List<R>> = map { it.map(transform) }

inline fun <reified T> MutableStateFlow<in T>.updateState(function: (T) -> T) {
    val state = value
    if (state is T) {
        update { function(state) }
    }
}
