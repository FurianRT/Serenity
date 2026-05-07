package com.furianrt.domain.repositories

import com.furianrt.domain.entities.Reminder
import kotlinx.coroutines.flow.Flow

interface RemindersRepository {
    suspend fun insertReminder(reminder: Reminder)
    suspend fun updateReminder(reminder: Reminder)
    fun getReminderFlow(id: String): Flow<Reminder?>
    suspend fun getReminder(id: String): Reminder?
    fun getAllRemindersFlow(): Flow<List<Reminder>>
    suspend fun getAllReminders(): List<Reminder>
    suspend fun deleteReminder(id: String)
}