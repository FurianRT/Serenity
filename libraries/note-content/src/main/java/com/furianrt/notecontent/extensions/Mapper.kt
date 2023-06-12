package com.furianrt.notecontent.extensions

import com.furianrt.notecontent.entities.UiNote
import com.furianrt.storage.api.entities.LocalNote

fun LocalNote.toUiNote() = UiNote(
    id = id,
    time = time,
    title = title,
)
