package com.furianrt.gallery.internal.ui.mappers

import com.furianrt.domain.entities.NoteMedia
import com.furianrt.gallery.internal.ui.entities.DateFilter
import com.furianrt.gallery.internal.ui.entities.ListItem
import com.furianrt.uikit.extensions.toDateString
import java.time.LocalDate
import java.time.ZonedDateTime

private const val MAX_ROTATION = 4f

internal fun List<NoteMedia>.toListItems(): List<ListItem> = groupBy { it.noteDate.toTitleItem() }
    .flatMap { (title, media) ->
        buildList {
            add(title)
            addAll(media.map(NoteMedia::toPhotoItem))
        }
    }

internal fun DateFilter.toDateString(): String = if (end != null) {
    start.formatDate() + " - " + end.formatDate()
} else {
    start.formatDate()
}

private fun LocalDate.formatDate(): String = if (LocalDate.now().year == year) {
    toDateString(pattern = "d MMM")
} else {
    toDateString(pattern = "d MMM yyyy")
}

private fun NoteMedia.toPhotoItem() = ListItem.Photo(
    id = id,
    uri = uri,
    label = noteDate.toDateString(pattern = "d MMM yyyy"),
    rotation = getStableRotationFromUuid(id),
)

private fun ZonedDateTime.toTitleItem() = ListItem.Title(
    text = toDateString(pattern = "LLLL yyyy").replaceFirstChar(Char::uppercase),
)

private fun getStableRotationFromUuid(uuidString: String): Float {
    val hashCode = uuidString.hashCode().toLong()
    val intRange = Int.MAX_VALUE.toLong() - Int.MIN_VALUE.toLong()
    val normalized = (hashCode - Int.MIN_VALUE) / intRange.toFloat()
    return normalized * (MAX_ROTATION * 2) - MAX_ROTATION
}
