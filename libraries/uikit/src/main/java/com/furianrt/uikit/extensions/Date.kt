package com.furianrt.uikit.extensions

import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val DATE_PATTERN = "dd LLL yyyy"
private const val TIME_PATTERN = "m:ss"

fun Long.toDateString(): String {
    val instant = Instant.ofEpochMilli(this)
    val localDateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern(DATE_PATTERN, Locale.US)
    return localDateTime.format(formatter)
}

fun Int.toTimeString() = toLong().toTimeString()

fun Long.toTimeString(): String {
    val instant = Instant.ofEpochMilli(this)
    val localTime = LocalTime.ofInstant(instant, ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern(TIME_PATTERN, Locale.US)
    return localTime.format(formatter)
}