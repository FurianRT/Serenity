package com.furianrt.reminders.internal.domain

import com.furianrt.domain.entities.Reminder
import com.furianrt.domain.repositories.RemindersRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetAllRemindersUseCase @Inject constructor(
    private val remindersRepository: RemindersRepository,
) {
    operator fun invoke(): Flow<List<Reminder>> = remindersRepository.getAllRemindersFlow()
}