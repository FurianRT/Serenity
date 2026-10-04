package com.furianrt.statistics.internal.domain.entities

internal enum class TimePeriod(val days: Long) {
    SEVEN_DAYS(7L),
    ONE_MONTH(30L),
    SIX_MONTH(180L),
    ONE_YEAR(365L),
    ALL_TIME(0L),
}