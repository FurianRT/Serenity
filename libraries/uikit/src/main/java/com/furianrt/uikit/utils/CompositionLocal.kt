package com.furianrt.uikit.utils

import androidx.compose.runtime.compositionLocalOf

val LocalAuth = compositionLocalOf<IsAuthorizedProvider> {
    error("Missing IsAuthorizedProvider")
}