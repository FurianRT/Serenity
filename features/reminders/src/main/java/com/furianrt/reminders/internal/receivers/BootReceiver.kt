package com.furianrt.reminders.internal.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.furianrt.core.DispatchersProvider
import com.furianrt.domain.repositories.RemindersRepository
import com.furianrt.reminders.internal.schedulers.ReminderScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
internal class BootReceiver : BroadcastReceiver() {
    @Inject
    lateinit var remindersRepository: RemindersRepository

    @Inject
    lateinit var reminderScheduler: ReminderScheduler

    @Inject
    lateinit var dispatchers: DispatchersProvider

    private val scope by lazy { CoroutineScope(dispatchers.io + SupervisorJob()) }

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val pendingResult = goAsync()
            scope.launch {
                try {
                    remindersRepository.getAllReminders().forEach { reminder ->
                        reminderScheduler.schedule(reminder)
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}