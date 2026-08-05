package com.furianrt.widgets.internal.widgets.actions

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback

internal class OpenEntryAction : ActionCallback {

    companion object {
        val DEEPLINK_KEY = ActionParameters.Key<String>("deeplink")
    }

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            parameters[DEEPLINK_KEY]!!.toUri(),
        ).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        context.startActivity(intent)
    }
}