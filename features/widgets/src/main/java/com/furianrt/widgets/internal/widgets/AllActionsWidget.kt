package com.furianrt.widgets.internal.widgets

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
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
import androidx.glance.layout.RowScope
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.unit.ColorProvider
import com.furianrt.common.SerenityDeeplink
import com.furianrt.uikit.R as uiR
import com.furianrt.widgets.internal.widgets.actions.OpenAppAction
import com.furianrt.widgets.internal.widgets.actions.OpenEntryAction

private val MEDIUM_MIN_WIDTH = 240.dp
private val LARGE_MIN_WIDTH = 320.dp

internal class AllActionsWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId,
    ) {
        provideContent {
            WidgetContent()
        }
    }

    @Composable
    private fun WidgetContent() {
        val size = LocalSize.current
        when {
            size.width >= LARGE_MIN_WIDTH -> LargeContent()
            size.width >= MEDIUM_MIN_WIDTH -> MediumContent()
            else -> SmallContent()
        }
    }

    @Composable
    private fun SmallContent() {
        WidgetContainer {
            Spacer(modifier = GlanceModifier.defaultWeight())
            AddButton()
            Spacer(modifier = GlanceModifier.defaultWeight())
            PhotoButton()
            Spacer(modifier = GlanceModifier.defaultWeight())
        }
    }

    @Composable
    private fun MediumContent() {
        WidgetContainer {
            Spacer(modifier = GlanceModifier.defaultWeight())
            AddButton()
            Spacer(modifier = GlanceModifier.defaultWeight())
            PhotoButton()
            Spacer(modifier = GlanceModifier.defaultWeight())
            VideoButton()
            Spacer(modifier = GlanceModifier.defaultWeight())
            VoiceButton()
            Spacer(modifier = GlanceModifier.defaultWeight())
        }
    }

    @Composable
    private fun LargeContent() {
        WidgetContainer(horizontalPadding = 16.dp) {
            OpenAppButton()
            Spacer(modifier = GlanceModifier.defaultWeight())
            AddButton()
            Spacer(modifier = GlanceModifier.defaultWeight())
            PhotoButton()
            Spacer(modifier = GlanceModifier.defaultWeight())
            VideoButton()
            Spacer(modifier = GlanceModifier.defaultWeight())
            VoiceButton()
        }
    }

    @Composable
    private fun WidgetContainer(
        horizontalPadding: Dp = 0.dp,
        content: @Composable RowScope.() -> Unit,
    ) {
        Row(
            modifier = GlanceModifier
                .fillMaxSize()
                .cornerRadius(24.dp)
                .background(GlanceTheme.colors.widgetBackground)
                .padding(horizontal = horizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally,
            content = { content() },
        )
    }

    @Composable
    private fun OpenAppButton() {
        val widgetSize = LocalSize.current

        val iconSize = when {
            widgetSize.height >= 72.dp -> 36.dp
            widgetSize.height >= 60.dp -> 32.dp
            else -> 28.dp
        }
        Box(
            modifier = GlanceModifier
                .cornerRadius(64.dp)
                .clickable(onClick = actionRunCallback<OpenAppAction>())
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                modifier = GlanceModifier.size(iconSize),
                provider = ImageProvider(uiR.drawable.app_logo_small),
                colorFilter = ColorFilter.tint(GlanceTheme.colors.onSurface),
                contentDescription = null,
            )
        }
    }

    @Composable
    private fun AddButton() {
        ActionButton(
            background = GlanceTheme.colors.inversePrimary,
            icon = uiR.drawable.ic_add,
            iconTint = GlanceTheme.colors.onSurface,
            deeplink = SerenityDeeplink.NEW_ENTRY,
        )
    }

    @Composable
    private fun PhotoButton() {
        ActionButton(
            background = GlanceTheme.colors.secondaryContainer,
            icon = uiR.drawable.ic_camera,
            iconTint = GlanceTheme.colors.onSecondaryContainer,
            deeplink = SerenityDeeplink.NEW_PHOTO,
            iconPadding = 3.dp,
        )
    }

    @Composable
    private fun VideoButton() {
        ActionButton(
            background = GlanceTheme.colors.secondaryContainer,
            icon = uiR.drawable.ic_video,
            iconTint = GlanceTheme.colors.onSecondaryContainer,
            deeplink = SerenityDeeplink.NEW_VIDEO,
        )
    }

    @Composable
    private fun VoiceButton() {
        ActionButton(
            background = GlanceTheme.colors.secondaryContainer,
            icon = uiR.drawable.ic_microphone,
            iconTint = GlanceTheme.colors.onSecondaryContainer,
            deeplink = SerenityDeeplink.NEW_VOICE,
            iconPadding = 3.dp,
        )
    }

    @Composable
    private fun ActionButton(
        background: ColorProvider,
        icon: Int,
        iconTint: ColorProvider,
        deeplink: String,
        iconPadding: Dp = 0.dp,
    ) {
        val widgetSize = LocalSize.current

        val (iconSize, buttonPadding) = when {
            widgetSize.height >= 72.dp -> 30.dp to 14.dp
            widgetSize.height >= 60.dp -> 24.dp to 12.dp
            else -> 20.dp to 7.dp
        }
        Box(
            modifier = GlanceModifier
                .background(background)
                .cornerRadius(20.dp)
                .clickable(
                    onClick = actionRunCallback<OpenEntryAction>(
                        parameters = actionParametersOf(
                            OpenEntryAction.DEEPLINK_KEY to deeplink,
                        ),
                    ),
                )
                .padding(buttonPadding),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                modifier = GlanceModifier
                    .padding(iconPadding)
                    .size(iconSize),
                provider = ImageProvider(icon),
                colorFilter = ColorFilter.tint(iconTint),
                contentDescription = null,
            )
        }
    }
}

class AllActionsWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = AllActionsWidget()
}