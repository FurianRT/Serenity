package com.furianrt.domain.entities

import android.net.Uri
import java.time.ZonedDateTime

data class NoteCustomBackground(
    val id: String,
    val name: String,
    val uri: Uri,
    val primaryColor: Int,
    val accentColor: Int,
    val isLight: Boolean,
    val isHidden: Boolean,
    val addedDate: ZonedDateTime,
)
