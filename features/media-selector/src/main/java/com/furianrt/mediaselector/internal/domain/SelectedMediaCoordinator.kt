package com.furianrt.mediaselector.internal.domain

import com.furianrt.mediaselector.internal.ui.entities.MediaItem
import java.io.Closeable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SelectedMediaCoordinator @Inject constructor() : Closeable {

    private val selectedMedia = mutableListOf<MediaItem>()

    fun getSelectedMedia(): List<MediaItem> = selectedMedia

    fun selectMedia(media: MediaItem) {
        selectedMedia.add(media)
    }

    fun unselectMedia(media: MediaItem) {
        selectedMedia.removeAll { it.id == media.id }
    }

    fun unselectAllMedia() {
        selectedMedia.clear()
    }

    override fun close() {
        selectedMedia.clear()
    }
}