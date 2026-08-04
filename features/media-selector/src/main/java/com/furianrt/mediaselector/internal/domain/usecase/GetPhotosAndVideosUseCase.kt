package com.furianrt.mediaselector.internal.domain.usecase

import com.furianrt.domain.entities.DeviceMedia
import com.furianrt.domain.repositories.MediaRepository
import com.furianrt.mediaselector.internal.domain.TempMediaHolder
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
internal class GetPhotosAndVideosUseCase @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val tempMediaHolder: TempMediaHolder,
) {
    suspend operator fun invoke(
        allowVideo: Boolean,
        albumId: String? = null,
    ): List<DeviceMedia> = tempMediaHolder.getAll() + mediaRepository.getDeviceMediaList(
        allowVideo = allowVideo,
        albumId = albumId,
    )
}