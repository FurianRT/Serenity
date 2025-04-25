package com.furianrt.notecreate.internal.ui.entites

import androidx.compose.runtime.Immutable
import com.furianrt.notelistui.entities.UiNoteFontColor
import com.furianrt.notelistui.entities.UiNoteFontFamily
import java.time.ZonedDateTime

@Immutable
internal data class NoteItem(
    val id: String,
    val fontFamily: UiNoteFontFamily,
    val fontColor: UiNoteFontColor,
    val fontSize: Int,
    val date: ZonedDateTime,
    val isPinned: Boolean,
)