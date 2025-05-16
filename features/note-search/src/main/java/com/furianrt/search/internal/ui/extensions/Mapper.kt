package com.furianrt.search.internal.ui.extensions

import com.furianrt.core.mapImmutable
import com.furianrt.domain.entities.LocalNote
import com.furianrt.domain.entities.LocalTag
import com.furianrt.notelistui.extensions.getShortUiContent
import com.furianrt.notelistui.extensions.toRegularUiNoteTag
import com.furianrt.notelistui.extensions.toUiNoteFontColor
import com.furianrt.notelistui.extensions.toUiNoteFontFamily
import com.furianrt.search.internal.ui.entities.SearchListItem
import com.furianrt.search.internal.ui.entities.SelectedFilter
import com.furianrt.uikit.extensions.toDateString
import java.time.LocalDate

internal fun List<LocalTag>.toTagsList() = SearchListItem.TagsList(
    tags = mapImmutable(LocalTag::toTagsListItem),
)

internal fun LocalNote.toNoteItem(isSelected: Boolean): SearchListItem.Note {
    val localDateNow = LocalDate.now()
    val localDate = date.toLocalDate()
    return SearchListItem.Note(
        id = id,
        date = when {
            localDateNow == localDate -> SearchListItem.Note.Date.Today
            localDateNow.minusDays(1) == localDate -> SearchListItem.Note.Date.Yesterday
            else -> SearchListItem.Note.Date.Other(date.toDateString())
        },
        tags = tags.mapImmutable(LocalNote.Tag::toRegularUiNoteTag),
        isSelected = isSelected,
        fontColor = fontColor.toUiNoteFontColor(),
        fontFamily = fontFamily.toUiNoteFontFamily(),
        fontSize = fontSize,
        content = content.getShortUiContent(fontFamily.toUiNoteFontFamily()),
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
