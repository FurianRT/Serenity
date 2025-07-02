package com.furianrt.backup.internal.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.furianrt.backup.R
import com.furianrt.uikit.R as uiR
import com.furianrt.backup.api.RootActivityIntentProvider
import com.furianrt.backup.internal.domain.BackupDataManager
import com.furianrt.backup.internal.domain.RestoreDataManager
import com.furianrt.core.DispatchersProvider
import com.furianrt.domain.repositories.MediaRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@AndroidEntryPoint
internal class NotesSyncService : Service(), CoroutineScope {

    companion object {
        private const val FOREGROUND_ID = 1
        private const val PENDING_INTENT_REQUEST_CODE = 1
        private const val CHANNEL_ID = "backup_channel"

        internal const val EXTRA_IS_BACKUP = "is_backup"
    }

    @Inject
    lateinit var dispatchers: DispatchersProvider

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var rootActivityIntentProvider: RootActivityIntentProvider

    @Inject
    lateinit var backupDataManager: BackupDataManager

    @Inject
    lateinit var restoreDataManager: RestoreDataManager

    @Inject
    lateinit var mediaRepository: MediaRepository

    override val coroutineContext: CoroutineContext
        get() = dispatchers.io + SupervisorJob()

    private var isServiceRunning = false

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isServiceRunning && intent != null) {
            isServiceRunning = true
            val isBackup = intent.getBooleanExtra(EXTRA_IS_BACKUP, false)
            startForeground(
                FOREGROUND_ID,
                createInitialNotification(isBackup),
                FOREGROUND_SERVICE_TYPE_DATA_SYNC,
            )
            syncDataData(isBackup)
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun syncDataData(isBackup: Boolean) = launch {
        if (isBackup) {
            backupDataManager.startBackup()
        } else {
            restoreDataManager.startRestore()
        }
        close()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.backup_notifications_channel_name),
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            description = getString(R.string.backup_notifications_channel_description)
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun createInitialNotification(
        isBackup: Boolean,
    ): Notification = NotificationCompat.Builder(this, CHANNEL_ID)
        .apply {
            if (isBackup) {
                setContentTitle(getString(uiR.string.backup_in_progress))
                setContentText(getString(R.string.backup_notification_initial_description))
                setSmallIcon(R.drawable.ic_cloud_upload)
            } else {
                setContentTitle(getString(uiR.string.restore_in_progress))
                setContentText(getString(R.string.restore_notification_initial_description))
                setSmallIcon(R.drawable.ic_cloud_download)
            }
        }
        .setContentIntent(createNotificationIntent())
        .build()

    private fun createNotificationIntent() = PendingIntent.getActivity(
        applicationContext,
        PENDING_INTENT_REQUEST_CODE,
        rootActivityIntentProvider.provide(),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

    private fun close() {
        cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }
}