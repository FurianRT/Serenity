package com.furianrt.storage.internal.database.reminders.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.DayOfWeek
import java.time.LocalTime

@Entity(tableName = EntryReminder.TABLE_NAME)
internal class EntryReminder(
    @PrimaryKey
    @ColumnInfo(name = FIELD_ID)
    val id: String,

    @ColumnInfo(name = FIELD_TITLE)
    val title: String?,

    @ColumnInfo(name = FIELD_TIME)
    val time: LocalTime,

    @ColumnInfo(name = FIELD_DAYS_OF_WEEK)
    val daysOfWeek: Set<DayOfWeek>,
) {
    companion object {
        const val TABLE_NAME = "Reminders"
        const val FIELD_ID = "id"
        const val FIELD_TITLE = "title"
        const val FIELD_TIME = "time"
        const val FIELD_DAYS_OF_WEEK = "days_of_week"
    }
}
