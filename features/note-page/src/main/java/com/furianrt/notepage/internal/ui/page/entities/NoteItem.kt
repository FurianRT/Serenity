package com.furianrt.notepage.internal.ui.page.entities

import androidx.compose.runtime.Immutable
import com.furianrt.domain.entities.NoteLocation
import com.furianrt.notelistui.entities.UiNoteContent
import com.furianrt.notelistui.entities.UiNoteFontColor
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.notelistui.entities.UiNoteTag
import com.furianrt.notelistui.entities.UiNoteTheme
import com.furianrt.notepage.internal.ui.stickers.entities.StickerItem

@Immutable
internal data class NoteItem(
    val id: String,
    val tags: List<UiNoteTag>,
    val stickers: List<StickerItem>,
    val content: List<UiNoteContent>,
    val fontFamily: UiNoteFontFamily?,
    val fontColor: UiNoteFontColor?,
    val fontSize: Int,
    val theme: UiNoteTheme?,
    val moodId: String?,
    val location: NoteLocation?,
)