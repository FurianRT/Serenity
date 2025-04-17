package com.furianrt.backup.internal.domain.usecases

import com.furianrt.backup.internal.domain.repositories.BackupRepository
import com.furianrt.domain.repositories.ProfileRepository
import javax.inject.Inject

internal class SignOutUseCase @Inject constructor(
    private val backupRepository: BackupRepository,
    private val profileRepository: ProfileRepository,
) {
    suspend operator fun invoke(email: String?): Result<Unit> = backupRepository.signOut()
        .onSuccess {
            email?.let { profileRepository.deleteBackupProfile(email) }
        }
}