package com.furianrt.permissions.extensions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

fun Context.openAppSettingsScreen() = startActivity(
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null),
    ).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
)

fun Context.openNotificationsSettingsScreen() = startActivity(
    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
        putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
)
