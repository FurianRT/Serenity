package com.furianrt.storage.internal.database.reminders.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.furianrt.storage.internal.database.reminders.entities.EntryReminder
import kotlinx.coroutines.flow.Flow

@Dao
internal interface RemindersDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entry: EntryReminder)

    @Update
    suspend fun update(entry: EntryReminder)

    @Query("SELECT * FROM ${EntryReminder.TABLE_NAME} WHERE ${EntryReminder.FIELD_ID} = :id")
    fun getReminderFlow(id: String): Flow<EntryReminder?>

    @Query("SELECT * FROM ${EntryReminder.TABLE_NAME} WHERE ${EntryReminder.FIELD_ID} = :id")
    suspend fun getReminder(id: String): EntryReminder?

    @Query("SELECT * FROM ${EntryReminder.TABLE_NAME}")
    fun getAllRemindersFlow(): Flow<List<EntryReminder>>

    @Query("SELECT * FROM ${EntryReminder.TABLE_NAME}")
    suspend fun getAllReminders(): List<EntryReminder>

    @Query("DELETE FROM ${EntryReminder.TABLE_NAME} WHERE ${EntryReminder.FIELD_ID} = :id")
    suspend fun deleteReminder(id: String)
}