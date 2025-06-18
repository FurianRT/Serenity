package com.furianrt.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

inline fun <T, R> Flow<Iterable<T>>.deepMap(
    crossinline transform: (value: T) -> R,
): Flow<List<R>> = map { it.map(transform) }

inline fun <T> Flow<Iterable<T>>.deepFilter(
    crossinline predicate: (T) -> Boolean,
): Flow<List<T>> = map { it.filter(predicate) }

inline fun <reified T> MutableStateFlow<in T>.updateState(function: (T) -> T) {
    val state = value
    if (state is T) {
        update { function(state) }
    }
}

inline fun <reified R> StateFlow<*>.getState(): R? {
    val state = value
    return if (state is R) {
        state
    } else {
        null
    }
}

inline fun <reified R> StateFlow<*>.doWithState(action: (state: R) -> Unit) {
    val state = value
    if (state is R) {
        action(state)
    }
}

inline fun <T1, T2, T3, T4, T5, T6, R> combine(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    crossinline transform: suspend (T1, T2, T3, T4, T5, T6) -> R
): Flow<R> = kotlinx.coroutines.flow.combine(flow, flow2, flow3, flow4, flow5, flow6) { args: Array<*> ->
    @Suppress("UNCHECKED_CAST")
    transform(
        args[0] as T1,
        args[1] as T2,
        args[2] as T3,
        args[3] as T4,
        args[4] as T5,
        args[5] as T6,
    )
}
