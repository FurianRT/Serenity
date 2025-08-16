package com.furianrt.notepage.internal.ui.page.entities

import androidx.compose.runtime.Immutable
import com.furianrt.notelistui.entities.UiNoteBackground
import com.furianrt.notelistui.entities.UiNoteContent
import com.furianrt.notelistui.entities.UiNoteFontColor
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.notelistui.entities.UiNoteTag
import com.furianrt.notepage.internal.ui.stickers.entities.StickerItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.util.UUID

@Immutable
internal data class NoteItem(
    val id: String = UUID.randomUUID().toString(),
    val tags: ImmutableList<UiNoteTag> = persistentListOf(),
    val stickers: ImmutableList<StickerItem> = persistentListOf(),
    val content: ImmutableList<UiNoteContent> = persistentListOf(),
    val fontFamily: UiNoteFontFamily? = null,
    val fontColor: UiNoteFontColor? = null,
    val fontSize: Int = 16,
    val background: UiNoteBackground?,
)