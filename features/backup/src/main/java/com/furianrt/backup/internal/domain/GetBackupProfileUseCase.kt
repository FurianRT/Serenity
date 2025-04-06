package com.furianrt.backup.internal.domain

import com.furianrt.backup.internal.domain.repositories.BackupRepository
import com.furianrt.domain.entities.BackupProfile
import com.furianrt.domain.repositories.ProfileRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@ViewModelScoped
internal class GetBackupProfileUseCase @Inject constructor(
    private val backupRepository: BackupRepository,
    private val profileRepository: ProfileRepository,
) {
    operator fun invoke(): Flow<BackupProfile?> = profileRepository.getBackupProfile()
        .distinctUntilChanged()
        .onEach { profile ->
            if (profile == null) {
                return@onEach
            }
            val isAuthorized = backupRepository.authorize()
                .map { !it.hasResolution() }
                .getOrDefault(false)

            if (!isAuthorized) {
                profileRepository.deleteBackupProfile(profile.email)
            }
        }
}