package com.furianrt.notepage.internal.ui.page.entities

import androidx.compose.runtime.Immutable
import com.furianrt.domain.entities.NoteLocation
import com.furianrt.notelistui.entities.UiNoteBackground
import com.furianrt.notelistui.entities.UiNoteContent
import com.furianrt.notelistui.entities.UiNoteFontColor
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.notelistui.entities.UiNoteTag
import com.furianrt.notepage.internal.ui.stickers.entities.StickerItem
import kotlinx.collections.immutable.ImmutableList

@Immutable
internal data class NoteItem(
    val id: String,
    val tags: ImmutableList<UiNoteTag>,
    val stickers: ImmutableList<StickerItem>,
    val content: ImmutableList<UiNoteContent>,
    val fontFamily: UiNoteFontFamily?,
    val fontColor: UiNoteFontColor?,
    val fontSize: Int,
    val background: UiNoteBackground?,
    val moodId: String?,
    val location: NoteLocation?,
)