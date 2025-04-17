package com.furianrt.backup.internal.domain.usecases

import android.content.Intent
import com.furianrt.backup.internal.domain.exceptions.AuthException
import com.furianrt.backup.internal.domain.repositories.BackupRepository
import com.furianrt.domain.entities.BackupProfile
import com.furianrt.domain.repositories.DeviceInfoRepository
import com.furianrt.domain.repositories.ProfileRepository
import javax.inject.Inject

internal class SignInUseCase @Inject constructor(
    private val backupRepository: BackupRepository,
    private val profileRepository: ProfileRepository,
    private val deviceInfoRepository: DeviceInfoRepository,
    private val signOutUseCase: SignOutUseCase,
) {
    suspend operator fun invoke(accessToken: String?): Result<Unit> {
        if (accessToken == null) {
            return Result.failure(AuthException.InvalidAccessTokenException())
        }

        backupRepository.updateAccessToken(accessToken)

        if (!deviceInfoRepository.hasNetworkConnection()) {
            return Result.failure(AuthException.NetworkException())
        }

        val email = backupRepository.getUserEmail()
            .onFailure { it.printStackTrace() }
            .getOrNull()

        if (email == null) {
            signOutUseCase(email = null)
            return Result.failure(AuthException.FetchEmailException())
        }

        profileRepository.saveBackupProfile(BackupProfile(email))

        return Result.success(Unit)
    }

    suspend operator fun invoke(intent: Intent?): Result<Unit> {
        val accessToken = backupRepository.getAuthorizationResult(intent)?.accessToken
        return invoke(accessToken)
    }
}