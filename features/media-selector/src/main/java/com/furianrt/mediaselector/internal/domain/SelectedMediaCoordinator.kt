package com.furianrt.mediaselector.internal.domain

import com.furianrt.mediaselector.internal.ui.entities.MediaItem
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
internal class SelectedMediaCoordinator @Inject constructor() {

    private val selectedMedia = mutableListOf<MediaItem>()

    fun getSelectedMedia(): List<MediaItem> = selectedMedia

    fun hasSelectedMedia(): Boolean = selectedMedia.isNotEmpty()

    fun selectMedia(media: MediaItem) {
        selectedMedia.add(media)
    }

    fun unselectMedia(media: MediaItem) {
        selectedMedia.removeAll { it.id == media.id }
    }

    fun unselectAllMedia() {
        selectedMedia.clear()
    }
}