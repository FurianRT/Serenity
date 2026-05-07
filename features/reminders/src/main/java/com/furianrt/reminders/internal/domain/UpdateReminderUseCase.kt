package com.furianrt.reminders.internal.domain

import com.furianrt.domain.entities.Reminder
import com.furianrt.domain.repositories.RemindersRepository
import com.furianrt.reminders.internal.schedulers.ReminderScheduler
import java.time.DayOfWeek
import java.time.LocalTime
import javax.inject.Inject

internal class UpdateReminderUseCase @Inject constructor(
    private val remindersRepository: RemindersRepository,
    private val reminderScheduler: ReminderScheduler,
) {
    suspend operator fun invoke(
        id: String,
        title: String?,
        time: LocalTime,
        daysOfWeek: Set<DayOfWeek>,
    ) {
        val reminder = Reminder(
            id = id,
            title = title?.trim()?.takeIf { it.isNotBlank() },
            time = time,
            daysOfWeek = daysOfWeek,
        )
        remindersRepository.updateReminder(reminder)
        reminderScheduler.schedule(reminder)
    }
}