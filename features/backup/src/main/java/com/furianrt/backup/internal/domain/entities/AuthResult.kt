package com.furianrt.backup.internal.domain.entities

import android.content.IntentSender

internal sealed interface AuthResult {
    data class Success(val accessToken: String) : AuthResult
    data class Resolution(val intentSender: IntentSender) : AuthResult
    data class Failure(val error: Throwable) : AuthResult
}