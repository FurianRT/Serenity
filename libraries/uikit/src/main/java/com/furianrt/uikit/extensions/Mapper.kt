package com.furianrt.uikit.extensions

import com.furianrt.storage.api.entities.Note
import com.furianrt.uikit.entities.UiNote

fun Note.toUiNote() = UiNote(
    id = id,
    time = time,
    title = title,
)
