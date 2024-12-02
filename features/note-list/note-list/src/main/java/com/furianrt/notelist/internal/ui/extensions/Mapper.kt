package com.furianrt.notelist.internal.ui.extensions

import androidx.compose.foundation.text.input.TextFieldState
import com.furianrt.core.buildImmutableList
import com.furianrt.core.mapImmutable
import com.furianrt.notelistui.entities.UiNoteContent
import com.furianrt.notelistui.extensions.toRegularUiNoteTag
import com.furianrt.notelistui.extensions.toUiNoteMedia
import com.furianrt.domain.entities.LocalNote
import com.furianrt.notelist.internal.ui.entities.NoteListScreenNote
import com.furianrt.uikit.extensions.toDateString

internal fun List<LocalNote>.toMainScreenNotes() = mapImmutable(LocalNote::toMainScreenNote)

internal fun LocalNote.toMainScreenNote() = NoteListScreenNote(
    id = id,
    date = date.toDateString(),
    tags = tags.take(3).mapImmutable { it.toRegularUiNoteTag(isRemovable = false) },
    content = buildImmutableList {
        val mediaBlock = content.firstOrNull { it is LocalNote.Content.MediaBlock }
        if (mediaBlock != null) {
            add(
                UiNoteContent.MediaBlock(
                    id = mediaBlock.id,
                    media = content
                        .filterIsInstance<LocalNote.Content.MediaBlock>()
                        .flatMap(LocalNote.Content.MediaBlock::media)
                        .mapImmutable(LocalNote.Content.Media::toUiNoteMedia),
                ),
            )
        }
        val title = content.firstOrNull { it is LocalNote.Content.Title }
        if (title != null) {
            add(
                UiNoteContent.Title(
                    id = title.id,
                    state = TextFieldState(
                        initialText = content
                            .filterIsInstance<LocalNote.Content.Title>()
                            .joinToString(separator = "\n", transform = { it.text }),
                    ),
                ),
            )
        }
    },
)
