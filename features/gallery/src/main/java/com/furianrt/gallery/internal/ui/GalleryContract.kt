package com.furianrt.gallery.internal.ui

import androidx.compose.runtime.Immutable
import com.furianrt.gallery.internal.ui.entities.ListItem
import com.furianrt.uikit.entities.UiThemeColor
import java.time.LocalDate

internal data class GalleryState(
    val theme: UiThemeColor,
    val dateFilter: String?,
    val content: Content,
) {
    sealed interface Content {
        data object Loading : Content
        data object Empty : Content

        @Immutable
        data class Success(
            val items: List<ListItem>,
        ) : Content
    }
}

internal sealed interface GalleryEvent {
    data object OnCloseClick : GalleryEvent
    data object OnCreateNoteClick : GalleryEvent
    data class OnPhotoItemClick(val item: ListItem.Photo) : GalleryEvent
    data object OnDateFiltersClick : GalleryEvent
    data object OnRemoveDateFilterClick : GalleryEvent
    data class OnDateFilterSelected(
        val start: LocalDate,
        val end: LocalDate?,
    ) : GalleryEvent
}

internal sealed interface GalleryEffect {
    data object CloseScreen : GalleryEffect
    data object OpenCreateNoteScreen : GalleryEffect
    data class ShowDateSelector(
        val start: LocalDate?,
        val end: LocalDate?,
        val datesWithPhotos: Set<LocalDate>,
    ) : GalleryEffect
}
