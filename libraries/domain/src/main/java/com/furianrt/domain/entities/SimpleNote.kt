package com.furianrt.domain.entities

import java.time.ZonedDateTime

class SimpleNote(
    val id: String,
    val font: NoteFontFamily?,
    val fontColor: NoteFontColor?,
    val fontSize: Int,
    val backgroundId: String?,
    val backgroundImageId: String?,
    val moodId: String?,
    val date: ZonedDateTime,
    val isPinned: Boolean,
)
