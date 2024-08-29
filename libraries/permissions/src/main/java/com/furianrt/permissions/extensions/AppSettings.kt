package com.furianrt.permissions.extensions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

fun Context.openAppSettingsScreen() = startActivity(
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null),
    )
)