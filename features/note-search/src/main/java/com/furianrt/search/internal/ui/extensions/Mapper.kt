package com.furianrt.search.internal.ui.extensions

import com.furianrt.core.mapImmutable
import com.furianrt.domain.entities.LocalNote
import com.furianrt.domain.entities.LocalTag
import com.furianrt.notelistui.extensions.toRegularUiNoteTag
import com.furianrt.notelistui.extensions.toUiNoteContent
import com.furianrt.search.internal.ui.entities.SearchListItem
import com.furianrt.uikit.extensions.toDateString

internal fun List<LocalTag>.toFiltersList() = SearchListItem.FiltersList(
    items = mapImmutable(LocalTag::toTagItem),
)

internal fun LocalNote.toNoteItem() = SearchListItem.Note(
    id = id,
    date = date.toDateString(),
    tags = tags.mapImmutable(LocalNote.Tag::toRegularUiNoteTag),
    content = content.mapImmutable(LocalNote.Content::toUiNoteContent),
)

private fun LocalTag.toTagItem() = SearchListItem.FiltersList.Filter.Tag(
    title = title,
    count = noteIds.count(),
)
