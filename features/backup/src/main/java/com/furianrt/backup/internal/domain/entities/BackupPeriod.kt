package com.furianrt.backup.internal.domain.entities

import java.util.concurrent.TimeUnit

internal sealed class BackupPeriod(val value: Long) {
    data object OneDay : BackupPeriod(ONE_DAY_PERIOD)
    data object TreeDays : BackupPeriod(THREE_DAYS_PERIOD)
    data object OneWeek : BackupPeriod(ONE_WEEK_PERIOD)
    data object TwoWeeks : BackupPeriod(TWO_WEEKS_PERIOD)
    data object OneMonth : BackupPeriod(ONE_MONTH_PERIOD)

    fun getTimeUnit(): Pair<Long, TimeUnit> = when (this) {
        is OneDay -> 1L to TimeUnit.DAYS
        is TreeDays -> 3L to TimeUnit.DAYS
        is OneWeek -> 7L to TimeUnit.DAYS
        is TwoWeeks -> 14L to TimeUnit.DAYS
        is OneMonth -> 30L to TimeUnit.DAYS
    }

    companion object {
        private const val ONE_DAY_PERIOD = 60L * 60L * 24L * 1000L
        private const val THREE_DAYS_PERIOD = ONE_DAY_PERIOD * 3L
        private const val ONE_WEEK_PERIOD = ONE_DAY_PERIOD * 7L
        private const val TWO_WEEKS_PERIOD = ONE_WEEK_PERIOD * 2L
        private const val ONE_MONTH_PERIOD = ONE_DAY_PERIOD * 30L

        fun fromValue(value: Long?): BackupPeriod? = when (value) {
            ONE_DAY_PERIOD -> OneDay
            THREE_DAYS_PERIOD -> TreeDays
            ONE_WEEK_PERIOD -> OneWeek
            TWO_WEEKS_PERIOD -> TwoWeeks
            ONE_MONTH_PERIOD -> OneMonth
            else -> null
        }
    }
}