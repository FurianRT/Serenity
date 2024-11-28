package com.furianrt.uikit.utils

import androidx.compose.runtime.compositionLocalOf

val LocalAuth = compositionLocalOf<IsAuthorizedProvider> {
    object : IsAuthorizedProvider {
        override suspend fun isAuthorized() = true
    }
}