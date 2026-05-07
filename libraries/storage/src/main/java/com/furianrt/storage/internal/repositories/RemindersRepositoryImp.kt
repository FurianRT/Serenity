package com.furianrt.storage.internal.repositories

import com.furianrt.core.deepMap
import com.furianrt.domain.entities.Reminder
import com.furianrt.domain.repositories.RemindersRepository
import com.furianrt.storage.internal.database.reminders.dao.RemindersDao
import com.furianrt.storage.internal.database.reminders.entities.EntryReminder
import com.furianrt.storage.internal.database.reminders.mappers.toDomain
import com.furianrt.storage.internal.database.reminders.mappers.toEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class RemindersRepositoryImp @Inject constructor(
    private val remindersDao: RemindersDao,
) : RemindersRepository {
    override suspend fun insertReminder(reminder: Reminder) {
        remindersDao.insert(reminder.toEntry())
    }

    override suspend fun updateReminder(reminder: Reminder) {
        remindersDao.update(reminder.toEntry())
    }

    override fun getReminderFlow(id: String): Flow<Reminder?> = remindersDao.getReminderFlow(id)
        .map { it?.toDomain() }

    override suspend fun getReminder(id: String): Reminder? = remindersDao.getReminder(id)
        ?.toDomain()

    override fun getAllRemindersFlow(): Flow<List<Reminder>> = remindersDao.getAllRemindersFlow()
        .deepMap(EntryReminder::toDomain)

    override suspend fun getAllReminders(): List<Reminder> = remindersDao.getAllReminders()
        .map(EntryReminder::toDomain)

    override suspend fun deleteReminder(id: String) {
        remindersDao.deleteReminder(id)
    }
}