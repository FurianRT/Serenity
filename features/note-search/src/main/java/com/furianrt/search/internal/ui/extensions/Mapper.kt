package com.furianrt.search.internal.ui.extensions

import com.furianrt.domain.entities.LocalNote
import com.furianrt.domain.entities.LocalTag
import com.furianrt.domain.entities.NoteFontFamily
import com.furianrt.notelistui.entities.LocationState
import com.furianrt.notelistui.extensions.getShortUiContent
import com.furianrt.notelistui.extensions.toLocationState
import com.furianrt.notelistui.extensions.toRegularUiNoteTag
import com.furianrt.notelistui.extensions.toUiNoteFontFamily
import com.furianrt.search.internal.ui.entities.SearchListItem
import com.furianrt.search.internal.ui.entities.SelectedFilter
import com.furianrt.uikit.extensions.toDateString
import java.time.LocalDate

internal fun List<LocalTag>.toTagsList() = SearchListItem.TagsList(
    tags = map(LocalTag::toTagsListItem),
)

internal fun LocalNote.toNoteItem(
    isSelected: Boolean,
    appFontFamily: NoteFontFamily,
): SearchListItem.Note {
    val localDateNow = LocalDate.now()
    val localDate = date.toLocalDate()
    return SearchListItem.Note(
        id = id,
        date = when {
            localDateNow == localDate -> SearchListItem.Note.Date.Today
            localDateNow.minusDays(1) == localDate -> SearchListItem.Note.Date.Yesterday
            else -> SearchListItem.Note.Date.Other(date.toDateString())
        },
        tags = tags.map(LocalNote.Tag::toRegularUiNoteTag),
        isSelected = isSelected,
        fontFamily = fontFamily?.toUiNoteFontFamily(),
        fontSize = fontSize,
        moodId = moodId,
        locationState = location?.toLocationState() ?: LocationState.Empty,
        content = content.getShortUiContent((fontFamily ?: appFontFamily).toUiNoteFontFamily()),
    )
}

internal fun LocalTag.toSelectedTag(isSelected: Boolean = false) = SelectedFilter.Tag(
    title = title,
    isSelected = isSelected,
)

private fun LocalTag.toTagsListItem() = SearchListItem.TagsList.Tag(
    title = title,
    count = noteIds.count(),
)
