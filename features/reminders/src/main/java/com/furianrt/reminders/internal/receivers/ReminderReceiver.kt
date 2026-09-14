package com.furianrt.reminders.internal.receivers

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.furianrt.common.ErrorTracker
import com.furianrt.common.NotificationChannels
import com.furianrt.common.RootActivityIntentProvider
import com.furianrt.common.SerenityDeeplink
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
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

@AndroidEntryPoint
internal class ReminderReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_REMINDER_ID = "reminder_id"
        const val EXTRA_NOTIFICATION_ID = "reminder_notification_id"
    }

    @Inject
    lateinit var remindersRepository: RemindersRepository

    @Inject
    lateinit var reminderScheduler: ReminderScheduler

    @Inject
    lateinit var dispatchers: DispatchersProvider

    @Inject
    lateinit var permissionsUtils: PermissionsUtils

    @Inject
    lateinit var rootActivityIntentProvider: RootActivityIntentProvider

    @Inject
    lateinit var notificationManager: NotificationManagerCompat

    @Inject
    lateinit var errorTracker: ErrorTracker

    private val scope by lazy { CoroutineScope(dispatchers.main + SupervisorJob()) }

    @OptIn(ExperimentalAtomicApi::class)
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getStringExtra(EXTRA_REMINDER_ID) ?: return
        val pendingResult = goAsync()

        val isFinished = AtomicBoolean(false)

        scope.launch {
            try {
                val reminder = remindersRepository.getReminder(id) ?: return@launch
                if (permissionsUtils.hasNotificationsPermission()) {
                    showNotification(context, reminder)
                }
                reminderScheduler.schedule(reminder)
            } finally {
                if (isFinished.compareAndSet(expectedValue = false, newValue = true)) {
                    try {
                        pendingResult.finish()
                    } catch (e: Exception) {
                        errorTracker.trackNonFatalError(e)
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(
        context: Context,
        reminder: Reminder,
    ) {
        val title = reminder.title ?: context.getString(
            R.string.reminders_default_reminder_notification_title,
            LocalDate.now()
                .dayOfWeek
                .getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()),
        )

        val notificationId = reminder.id.hashCode()

        val notification = NotificationCompat
            .Builder(context, NotificationChannels.REMINDERS_CHANNEL_ID)
            .setContentIntent(createNotificationIntent(context))
            .setCustomContentView(createSmallContent(context, notificationId, title))
            .setCustomBigContentView(createBigContent(context, notificationId, title))
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setSmallIcon(uiR.drawable.notification_small_logo)
            .setContentTitle(title)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .build()

        notificationManager.notify(reminder.id.hashCode(), notification)
    }

    private fun createSmallContent(
        context: Context,
        notificationId: Int,
        title: String,
    ) = RemoteViews(
        context.packageName,
        R.layout.layout_notification_reminder_small_content,
    ).apply {
        setTextViewText(R.id.title, title)

        setOnClickPendingIntent(
            R.id.note,
            context.createOpenEntryPendingIntent(
                deeplink = SerenityDeeplink.NEW_ENTRY,
                notificationId = notificationId,
            ),
        )
        setOnClickPendingIntent(
            R.id.photo,
            context.createOpenEntryPendingIntent(
                deeplink = SerenityDeeplink.NEW_PHOTO,
                notificationId = notificationId,
            ),
        )
        setOnClickPendingIntent(
            R.id.video,
            context.createOpenEntryPendingIntent(
                deeplink = SerenityDeeplink.NEW_VIDEO,
                notificationId = notificationId,
            ),
        )
        setOnClickPendingIntent(
            R.id.voice,
            context.createOpenEntryPendingIntent(
                deeplink = SerenityDeeplink.NEW_VOICE,
                notificationId = notificationId,
            ),
        )
    }

    private fun createBigContent(
        context: Context,
        notificationId: Int,
        title: String,
    ) = RemoteViews(
        context.packageName,
        R.layout.layout_notification_reminder_big_content,
    ).apply {
        setTextViewText(R.id.title, title)

        setOnClickPendingIntent(
            R.id.note,
            context.createOpenEntryPendingIntent(
                deeplink = SerenityDeeplink.NEW_ENTRY,
                notificationId = notificationId,
            ),
        )
        setOnClickPendingIntent(
            R.id.photo,
            context.createOpenEntryPendingIntent(
                deeplink = SerenityDeeplink.NEW_PHOTO,
                notificationId = notificationId,
            ),
        )
        setOnClickPendingIntent(
            R.id.video,
            context.createOpenEntryPendingIntent(
                deeplink = SerenityDeeplink.NEW_VIDEO,
                notificationId = notificationId,
            ),
        )
        setOnClickPendingIntent(
            R.id.voice,
            context.createOpenEntryPendingIntent(
                deeplink = SerenityDeeplink.NEW_VOICE,
                notificationId = notificationId,
            ),
        )
    }

    private fun createNotificationIntent(context: Context) = PendingIntent.getActivity(
        context,
        0,
        rootActivityIntentProvider.provide(),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

    private fun Context.createOpenEntryPendingIntent(
        deeplink: String,
        notificationId: Int,
    ) = PendingIntent.getActivity(
        this,
        deeplink.hashCode(),
        Intent(Intent.ACTION_VIEW, deeplink.toUri()).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra(EXTRA_NOTIFICATION_ID, notificationId)
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )
}
