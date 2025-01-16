package com.furianrt.search.internal.ui.extensions

import com.furianrt.core.mapImmutable
import com.furianrt.domain.entities.LocalNote
import com.furianrt.domain.entities.LocalTag
import com.furianrt.notelistui.extensions.getShortUiContent
import com.furianrt.notelistui.extensions.toRegularUiNoteTag
import com.furianrt.notelistui.extensions.toUiNoteFontColor
import com.furianrt.search.internal.ui.entities.SearchListItem
import com.furianrt.search.internal.ui.entities.SelectedFilter
import com.furianrt.uikit.extensions.toDateString

internal fun List<LocalTag>.toTagsList() = SearchListItem.TagsList(
    tags = mapImmutable(LocalTag::toTagsListItem),
)

internal fun LocalNote.toNoteItem() = SearchListItem.Note(
    id = id,
    date = date.toDateString(),
    tags = tags.mapImmutable(LocalNote.Tag::toRegularUiNoteTag),
    fontColor = fontColor.toUiNoteFontColor(),
    content = content.getShortUiContent(),
)

internal fun LocalTag.toSelectedTag(isSelected: Boolean = false) = SelectedFilter.Tag(
    title = title,
    isSelected = isSelected,
)

private fun LocalTag.toTagsListItem() = SearchListItem.TagsList.Tag(
    title = title,
    count = noteIds.count(),
)
