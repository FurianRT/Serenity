package com.furianrt.widgets.internal.widgets.actions

import android.content.Context
import android.content.Intent
import androidx.annotation.Keep
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback

@Keep
internal class OpenAppAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        val intent = context.packageManager
            .getLaunchIntentForPackage(context.packageName)
            ?.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

        if (intent != null) {
            context.startActivity(intent)
        }
    }
}
