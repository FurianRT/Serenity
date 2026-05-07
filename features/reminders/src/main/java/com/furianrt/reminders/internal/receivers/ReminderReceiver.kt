package com.furianrt.reminders.internal.receivers

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.furianrt.common.NotificationChannels
import com.furianrt.core.DispatchersProvider
import com.furianrt.domain.entities.Reminder
import com.furianrt.domain.repositories.RemindersRepository
import com.furianrt.permissions.utils.PermissionsUtils
import com.furianrt.reminders.R
import com.furianrt.uikit.R as uiR
import com.furianrt.reminders.internal.schedulers.ReminderScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
internal class ReminderReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_REMINDER_ID = "reminder_id"
    }

    @Inject
    lateinit var remindersRepository: RemindersRepository

    @Inject
    lateinit var reminderScheduler: ReminderScheduler

    @Inject
    lateinit var dispatchers: DispatchersProvider

    @Inject
    lateinit var permissionsUtils: PermissionsUtils

    private val scope by lazy { CoroutineScope(dispatchers.io + SupervisorJob()) }

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        scope.launch {
            try {
                val id = intent.getStringExtra(EXTRA_REMINDER_ID) ?: return@launch
                val reminder = remindersRepository.getReminder(id) ?: return@launch
                showNotification(context, reminder)
                reminderScheduler.schedule(reminder)
            } finally {
                pendingResult.finish()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(
        context: Context,
        reminder: Reminder,
    ) {
        if (permissionsUtils.hasNotificationsPermission()) {
            val title = reminder.title ?: context.getString(
                R.string.reminders_default_reminder_notification_title,
            )
            val text = context.getString(
                R.string.reminders_default_reminder_notification_body,
            )
            val notification = NotificationCompat.Builder(
                context,
                NotificationChannels.REMINDERS_CHANNEL_ID,
            )
                .setSmallIcon(uiR.drawable.notification_small_logo)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .build()

            NotificationManagerCompat.from(context)
                .notify(reminder.id.hashCode(), notification)
        }
    }
}
