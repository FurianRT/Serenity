package com.furianrt.backup.internal.domain

import com.furianrt.backup.internal.domain.entities.AuthResult
import com.furianrt.backup.internal.domain.repositories.BackupRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
internal class AuthorizeUseCase @Inject constructor(
    private val backupRepository: BackupRepository,
) {
    suspend operator fun invoke(): AuthResult = backupRepository.authorize().fold(
        onSuccess = { result ->
            val accessToken = result.accessToken
            when {
                result.hasResolution() -> {
                    val intentSender = result.pendingIntent?.intentSender
                    if (intentSender != null) {
                        AuthResult.Resolution(intentSender)
                    } else {
                        AuthResult.Failure(IllegalStateException())
                    }
                }

                accessToken != null -> AuthResult.Success(accessToken)

                else -> AuthResult.Failure(IllegalStateException())
            }
        },
        onFailure = { AuthResult.Failure(it) },
    )
}