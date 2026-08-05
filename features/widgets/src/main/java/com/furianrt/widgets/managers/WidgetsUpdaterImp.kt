package com.furianrt.widgets.managers

import android.content.Context
import androidx.glance.appwidget.updateAll
import com.furianrt.domain.WidgetsUpdater
import com.furianrt.widgets.widgets.AllActionsWidget
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class WidgetsUpdaterImp @Inject constructor(
    @ApplicationContext private val context: Context,
) : WidgetsUpdater {
    override suspend fun updateWidgets() {
        AllActionsWidget().updateAll(context)
    }
}