package com.furianrt.backup.internal.domain.usecases

import com.furianrt.backup.internal.domain.entities.AuthResult
import com.furianrt.backup.internal.domain.exceptions.AuthException
import com.furianrt.backup.internal.domain.repositories.BackupRepository
import javax.inject.Inject

internal class AuthorizeUseCase @Inject constructor(
    private val backupRepository: BackupRepository,
    private val signOutUseCase: SignOutUseCase,
) {
    suspend operator fun invoke(): AuthResult {
        val authResult = backupRepository.authorize().getOrElse {
            return AuthResult.Failure(it)
        }
        if (authResult.hasResolution()) {
            val intentSender = authResult.pendingIntent?.intentSender
            return if (intentSender != null) {
                AuthResult.Resolution(intentSender)
            } else {
                AuthResult.Failure(IllegalStateException())
            }
        }
        val accessToken = authResult.accessToken
        if (accessToken != null && backupRepository.hasRequiredScopes(authResult)) {
            return AuthResult.Success(accessToken)
        }

        if (accessToken != null && !backupRepository.hasRequiredScopes(authResult)) {
            signOutUseCase(email = null, accessToken = accessToken)
            return AuthResult.ScopesError(AuthException.AuthScopesException())
        }

        return AuthResult.Failure(IllegalStateException())
    }
}