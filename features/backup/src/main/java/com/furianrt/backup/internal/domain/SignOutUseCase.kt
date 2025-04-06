package com.furianrt.backup.internal.domain

import com.furianrt.backup.internal.domain.repositories.BackupRepository
import com.furianrt.domain.repositories.ProfileRepository
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
internal class SignOutUseCase @Inject constructor(
    private val backupRepository: BackupRepository,
    private val profileRepository: ProfileRepository,
) {
    suspend operator fun invoke(email: String): Result<Unit> = backupRepository.signOut()
        .onSuccess {
            profileRepository.deleteBackupProfile(email)
        }
}