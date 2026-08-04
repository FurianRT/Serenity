package com.furianrt.mediaselector.internal.domain

import com.furianrt.domain.entities.DeviceMedia
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class TempMediaHolder @Inject constructor() {

    private val selectedMedia = mutableListOf<DeviceMedia>()

    fun getAll(): List<DeviceMedia> = selectedMedia

    fun add(media: DeviceMedia) {
        selectedMedia.add(0, media)
    }

    fun clear() {
        selectedMedia.clear()
    }
}