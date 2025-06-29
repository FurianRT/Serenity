package com.furianrt.backup.internal.domain.exceptions

internal sealed class AuthException(val code: Int): Throwable() {
    class UnknownErrorException : AuthException(CODE) {
        companion object {
            const val CODE = 800
        }
    }
    class NetworkException : AuthException(801)
    class InvalidAccessTokenException : AuthException(802)
    class SendIntentException : AuthException(803)
    class ActivityNotFoundException : AuthException(804)
    class ClearCredentialException : AuthException(805)
    class FetchEmailException : AuthException(806)
    class ResolutionCanceled : AuthException(807)
}