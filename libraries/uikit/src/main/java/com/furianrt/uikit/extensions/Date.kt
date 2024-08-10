package com.furianrt.uikit.extensions

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val DATE_PATTERN = "dd LLL yyyy"

fun Long.toDateString(): String {
    val instant = Instant.ofEpochMilli(this)
    val localDateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern(DATE_PATTERN, Locale.US)
    return localDateTime.format(formatter)
}