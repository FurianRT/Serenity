package com.furianrt.uikit.extensions

import androidx.lifecycle.SavedStateHandle

inline fun <reified T> SavedStateHandle.getOrPut(key: String, value: T): T {
    if (contains(key)) {
        return get(key) ?: value
    } else {
        this[key] = value
        return value
    }
}