package com.furianrt.uikit.extensions

import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val DATE_PATTERN = "dd LLL yyyy"
private const val TIME_PATTERN = "m:ss"

fun ZonedDateTime.toDateString(pattern: String = DATE_PATTERN): String {
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.US)
    return format(formatter)
}

fun Int.toTimeString(pattern: String = TIME_PATTERN): String = toLong().toTimeString(pattern)

fun Long.toTimeString(pattern: String = TIME_PATTERN): String {
    val instant = Instant.ofEpochMilli(this)
    val localTime = LocalTime.ofInstant(instant, ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.US)
    return localTime.format(formatter)
}
