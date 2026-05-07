package com.furianrt.reminders.internal.domain

import com.furianrt.domain.entities.Reminder
import com.furianrt.domain.repositories.RemindersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

internal class GetReminderUseCase @Inject constructor(
    private val remindersRepository: RemindersRepository,
) {
    operator fun invoke(id: String?): Flow<Reminder?> = if (id != null) {
        remindersRepository.getReminderFlow(id)
    } else {
        flowOf(null)
    }
}
