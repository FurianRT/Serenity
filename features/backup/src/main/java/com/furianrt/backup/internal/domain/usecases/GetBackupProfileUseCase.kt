package com.furianrt.backup.internal.domain.usecases

import com.furianrt.domain.entities.BackupProfile
import com.furianrt.domain.repositories.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

internal class GetBackupProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
) {
    operator fun invoke(): Flow<BackupProfile?> = profileRepository.getBackupProfile()
        .distinctUntilChanged()
}