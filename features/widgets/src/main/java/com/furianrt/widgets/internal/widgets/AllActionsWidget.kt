package com.furianrt.widgets.internal.widgets

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import com.furianrt.common.SerenityDeeplink
import com.furianrt.widgets.internal.widgets.actions.OpenAppAction
import com.furianrt.widgets.internal.widgets.actions.OpenEntryAction
import com.furianrt.uikit.R as uiR

private enum class WidgetSize(val size: DpSize) {
    SMALL(DpSize(120.dp, 60.dp)),
    MEDIUM(DpSize(200.dp, 60.dp)),
    LARGE(DpSize(320.dp, 60.dp));

    companion object {
        fun fromSize(size: DpSize): WidgetSize = when {
            size.width <= SMALL.size.width -> SMALL
            size.width <= MEDIUM.size.width -> MEDIUM
            else -> LARGE
        }
    }
}

internal class AllActionsWidget : GlanceAppWidget() {
    override suspend fun provideGlance(
        context: Context,
        id: GlanceId,
    ) {
        provideContent {
            WidgetContent()
        }
    }

    override val sizeMode = SizeMode.Responsive(
        setOf(
            WidgetSize.LARGE.size,
            WidgetSize.MEDIUM.size,
            WidgetSize.SMALL.size,
        )
    )

    @Composable
    private fun WidgetContent() {
        val widgetSize = WidgetSize.fromSize(LocalSize.current)

        Row(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.widgetBackground)
                .padding(
                    horizontal = when (widgetSize) {
                        WidgetSize.SMALL -> 0.dp
                        WidgetSize.MEDIUM -> 8.dp
                        WidgetSize.LARGE -> 16.dp
                    }
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (widgetSize == WidgetSize.SMALL) {
                Spacer(GlanceModifier.defaultWeight())
            }
            if (widgetSize == WidgetSize.LARGE) {
                Box(
                    modifier = GlanceModifier
                        .cornerRadius(64.dp)
                        .clickable(onClick = actionRunCallback<OpenAppAction>())
                        .padding(8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        modifier = GlanceModifier.size(36.dp),
                        provider = ImageProvider(uiR.drawable.app_logo_small),
                        colorFilter = ColorFilter.tint(GlanceTheme.colors.onSurface),
                        contentDescription = null,
                    )
                }
                Spacer(GlanceModifier.size(8.dp))
                Spacer(GlanceModifier.defaultWeight())
            }
            Box(
                modifier = GlanceModifier
                    .background(GlanceTheme.colors.inversePrimary)
                    .cornerRadius(64.dp)
                    .clickable(
                        onClick = actionRunCallback<OpenEntryAction>(
                            parameters = actionParametersOf(
                                OpenEntryAction.DEEPLINK_KEY to SerenityDeeplink.NEW_ENTRY,
                            )
                        )
                    )
                    .padding(14.dp),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    modifier = GlanceModifier.size(30.dp),
                    provider = ImageProvider(uiR.drawable.ic_add),
                    colorFilter = ColorFilter.tint(GlanceTheme.colors.onSurface),
                    contentDescription = null,
                )
            }
            Spacer(GlanceModifier.defaultWeight())
            Box(
                modifier = GlanceModifier
                    .background(GlanceTheme.colors.secondaryContainer)
                    .cornerRadius(20.dp)
                    .clickable(
                        onClick = actionRunCallback<OpenEntryAction>(
                            parameters = actionParametersOf(
                                OpenEntryAction.DEEPLINK_KEY to SerenityDeeplink.NEW_PHOTO,
                            )
                        )
                    )
                    .padding(14.dp),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    modifier = GlanceModifier
                        .padding(3.dp)
                        .size(30.dp),
                    provider = ImageProvider(uiR.drawable.ic_camera),
                    colorFilter = ColorFilter.tint(GlanceTheme.colors.onSecondaryContainer),
                    contentDescription = null,
                )
            }
            if (widgetSize == WidgetSize.MEDIUM || widgetSize == WidgetSize.LARGE) {
                Spacer(GlanceModifier.defaultWeight())
                Box(
                    modifier = GlanceModifier
                        .background(GlanceTheme.colors.secondaryContainer)
                        .cornerRadius(20.dp)
                        .clickable(
                            onClick = actionRunCallback<OpenEntryAction>(
                                parameters = actionParametersOf(
                                    OpenEntryAction.DEEPLINK_KEY to SerenityDeeplink.NEW_VIDEO,
                                )
                            )
                        )
                        .padding(14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        modifier = GlanceModifier.size(30.dp),
                        provider = ImageProvider(uiR.drawable.ic_video),
                        colorFilter = ColorFilter.tint(GlanceTheme.colors.onSecondaryContainer),
                        contentDescription = null,
                    )
                }
            }
            if (widgetSize == WidgetSize.MEDIUM || widgetSize == WidgetSize.LARGE) {
                Spacer(GlanceModifier.defaultWeight())
                Box(
                    modifier = GlanceModifier
                        .background(GlanceTheme.colors.secondaryContainer)
                        .cornerRadius(20.dp)
                        .clickable(
                            onClick = actionRunCallback<OpenEntryAction>(
                                parameters = actionParametersOf(
                                    OpenEntryAction.DEEPLINK_KEY to SerenityDeeplink.NEW_VOICE,
                                )
                            )
                        )
                        .padding(14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        modifier = GlanceModifier
                            .padding(3.dp)
                            .size(30.dp),
                        provider = ImageProvider(uiR.drawable.ic_microphone),
                        colorFilter = ColorFilter.tint(GlanceTheme.colors.onSecondaryContainer),
                        contentDescription = null,
                    )
                }
            }
            if (widgetSize == WidgetSize.SMALL) {
                Spacer(GlanceModifier.defaultWeight())
            }
        }
    }
}

internal class AllActionsWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = AllActionsWidget()
}
