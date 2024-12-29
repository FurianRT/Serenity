package com.furianrt.domain.entities

import java.time.ZonedDateTime

class SimpleNote(
    val id: String,
    val font: NoteFontFamily,
    val fontColor: NoteFontColor,
    val date: ZonedDateTime,
)
