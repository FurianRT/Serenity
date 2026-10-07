package com.furianrt.statistics.internal.domain.entities

internal enum class TimePeriod(val days: Long) {
    SEVEN_DAYS(7L),
    ONE_MONTH(30L),
    SIX_MONTH(180L),
    ONE_YEAR(365L),
    ALL_TIME(0L);

    companion object {
        fun fromDays(days: Long?) = when(days) {
            SEVEN_DAYS.days -> SEVEN_DAYS
            ONE_MONTH.days -> ONE_MONTH
            SIX_MONTH.days -> SIX_MONTH
            ONE_YEAR.days -> ONE_YEAR
            ALL_TIME.days -> ALL_TIME
            else -> SEVEN_DAYS
        }
    }
}