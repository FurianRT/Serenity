package com.furianrt.uikit.utils

interface IsAuthorizedProvider {
    suspend fun isAuthorized(): Boolean
}