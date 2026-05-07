package com.furianrt.reminders.internal.domain

import com.furianrt.domain.repositories.RemindersRepository
import com.furianrt.reminders.internal.schedulers.ReminderScheduler
import javax.inject.Inject

internal class DeleteReminderUseCase @Inject constructor(
    private val remindersRepository: RemindersRepository,
    private val reminderScheduler: ReminderScheduler,
) {
    suspend operator fun invoke(id: String) {
        val reminder = remindersRepository.getReminder(id)
        if (reminder != null) {
            reminderScheduler.cancel(reminder)
            remindersRepository.deleteReminder(id)
        }
    }
}