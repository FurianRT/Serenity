package com.furianrt.reminders.internal.domain

import com.furianrt.domain.entities.Reminder
import com.furianrt.domain.repositories.RemindersRepository
import com.furianrt.reminders.internal.schedulers.ReminderScheduler
import java.time.DayOfWeek
import java.time.LocalTime
import java.util.UUID
import javax.inject.Inject

internal class CreateReminderUseCase @Inject constructor(
    private val remindersRepository: RemindersRepository,
    private val reminderScheduler: ReminderScheduler,
) {
    suspend operator fun invoke(
        title: String?,
        time: LocalTime,
        daysOfWeek: Set<DayOfWeek>,
    ) {
        val reminder = Reminder(
            id = UUID.randomUUID().toString(),
            title = title?.trim()?.takeIf { it.isNotBlank() },
            time = time,
            daysOfWeek = daysOfWeek.ifEmpty {
                setOf(
                    DayOfWeek.MONDAY,
                    DayOfWeek.TUESDAY,
                    DayOfWeek.WEDNESDAY,
                    DayOfWeek.THURSDAY,
                    DayOfWeek.FRIDAY,
                    DayOfWeek.SATURDAY,
                    DayOfWeek.SUNDAY,
                )
            },
        )
        remindersRepository.insertReminder(reminder)
        reminderScheduler.schedule(reminder)
    }
}