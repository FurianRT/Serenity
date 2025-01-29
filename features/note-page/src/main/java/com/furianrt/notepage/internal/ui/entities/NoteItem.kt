package com.furianrt.notepage.internal.ui.entities

import androidx.compose.runtime.Immutable
import com.furianrt.notelistui.entities.UiNoteContent
import com.furianrt.notelistui.entities.UiNoteFontColor
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.notelistui.entities.UiNoteTag
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.util.UUID

@Immutable
internal data class NoteItem(
    val id: String = UUID.randomUUID().toString(),
    val tags: ImmutableList<UiNoteTag> = persistentListOf(),
    val stickers: ImmutableList<StickerItem> = persistentListOf(),
    val content: ImmutableList<UiNoteContent> = persistentListOf(),
    val fontFamily: UiNoteFontFamily = UiNoteFontFamily.QUICK_SAND,
    val fontColor: UiNoteFontColor = UiNoteFontColor.WHITE,
    val fontSize: Int = 15,
)