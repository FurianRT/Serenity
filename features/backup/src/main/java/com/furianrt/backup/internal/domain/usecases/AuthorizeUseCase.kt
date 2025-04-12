package com.furianrt.backup.internal.domain.usecases

import com.furianrt.backup.internal.domain.entities.AuthResult
import com.furianrt.backup.internal.domain.repositories.BackupRepository
import javax.inject.Inject

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