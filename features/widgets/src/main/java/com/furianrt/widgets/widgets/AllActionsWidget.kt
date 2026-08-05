package com.furianrt.widgets.widgets

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
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
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.unit.ColorProvider
import com.furianrt.common.SerenityDeeplink
import com.furianrt.uikit.entities.UiThemeColor
import com.furianrt.widgets.di.WidgetEntryPoint
import com.furianrt.widgets.widgets.actions.OpenEntryAction
import com.furianrt.widgets.widgets.components.Separator
import dagger.hilt.android.EntryPointAccessors
import com.furianrt.uikit.R as uiR

private enum class WidgetSize(val size: DpSize) {
    SMALL(DpSize(120.dp, 60.dp)),
    MEDIUM(DpSize(200.dp, 60.dp));

    companion object {
        fun fromSize(size: DpSize): WidgetSize = if (size.width > SMALL.size.width) {
            MEDIUM
        } else {
            SMALL
        }
    }
}

internal class AllActionsWidget : GlanceAppWidget() {
    override suspend fun provideGlance(
        context: Context,
        id: GlanceId,
    ) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WidgetEntryPoint::class.java
        )

        val appearanceRepository = entryPoint.appearanceRepository()

        provideContent {
            val appColorId by appearanceRepository.getAppThemeColorId().collectAsState()
            WidgetContent(
                theme = appColorId
                    ?.let(UiThemeColor::fromId) ?: UiThemeColor.STORM_IN_THE_NIGHT_BLUE_LIGHT,
            )
        }
    }

    override val sizeMode = SizeMode.Responsive(
        setOf(
            WidgetSize.SMALL.size,
            WidgetSize.MEDIUM.size,
        )
    )

    @Composable
    private fun WidgetContent(
        theme: UiThemeColor,
    ) {
        val iconsColorProvider = remember(theme.primary) {
            object : ColorProvider {
                override fun getColor(context: Context): Color = theme.primary
            }
        }

        val widgetSize = WidgetSize.fromSize(LocalSize.current)

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(12.dp),
        ) {
            Box(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(theme.surface)
                    .cornerRadius(32.dp),
                contentAlignment = Alignment.Center,
            ) {
                theme.image?.let { image ->
                    Image(
                        modifier = GlanceModifier.fillMaxSize(),
                        provider = ImageProvider(image.resId),
                        contentScale = ContentScale.Crop,
                        contentDescription = null,
                    )
                }
                Row(
                    modifier = GlanceModifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(GlanceModifier.defaultWeight())
                    Box(
                        modifier = GlanceModifier
                            .cornerRadius(64.dp)
                            .clickable(
                                onClick = actionRunCallback<OpenEntryAction>(
                                    parameters = actionParametersOf(
                                        OpenEntryAction.DEEPLINK_KEY to SerenityDeeplink.NEW_ENTRY,
                                    )
                                )
                            )
                            .padding(8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            modifier = GlanceModifier.size(28.dp),
                            provider = ImageProvider(uiR.drawable.ic_add),
                            colorFilter = ColorFilter.tint(iconsColorProvider),
                            contentDescription = null,
                        )
                    }
                    Separator(
                        modifier = GlanceModifier
                            .padding(vertical = 14.dp)
                            .defaultWeight(),
                        color = theme.primary.copy(alpha = 0.08f)
                    )
                    Box(
                        modifier = GlanceModifier
                            .cornerRadius(64.dp)
                            .clickable(
                                onClick = actionRunCallback<OpenEntryAction>(
                                    parameters = actionParametersOf(
                                        OpenEntryAction.DEEPLINK_KEY to SerenityDeeplink.NEW_PHOTO,
                                    )
                                )
                            )
                            .padding(8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            modifier = GlanceModifier
                                .padding(3.dp)
                                .size(28.dp),
                            provider = ImageProvider(uiR.drawable.ic_camera),
                            colorFilter = ColorFilter.tint(iconsColorProvider),
                            contentDescription = null,
                        )
                    }
                    if (widgetSize == WidgetSize.MEDIUM) {
                        Separator(
                            modifier = GlanceModifier
                                .padding(vertical = 14.dp)
                                .defaultWeight(),
                            color = theme.primary.copy(alpha = 0.08f)
                        )
                        Box(
                            modifier = GlanceModifier
                                .cornerRadius(64.dp)
                                .clickable(
                                    onClick = actionRunCallback<OpenEntryAction>(
                                        parameters = actionParametersOf(
                                            OpenEntryAction.DEEPLINK_KEY to SerenityDeeplink.NEW_VIDEO,
                                        )
                                    )
                                )
                                .padding(8.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Image(
                                modifier = GlanceModifier.size(28.dp),
                                provider = ImageProvider(uiR.drawable.ic_video),
                                colorFilter = ColorFilter.tint(iconsColorProvider),
                                contentDescription = null,
                            )
                        }
                    }
                    if (widgetSize == WidgetSize.MEDIUM) {
                        Separator(
                            modifier = GlanceModifier
                                .padding(vertical = 14.dp)
                                .defaultWeight(),
                            color = theme.primary.copy(alpha = 0.08f)
                        )
                        Box(
                            modifier = GlanceModifier
                                .cornerRadius(64.dp)
                                .clickable(
                                    onClick = actionRunCallback<OpenEntryAction>(
                                        parameters = actionParametersOf(
                                            OpenEntryAction.DEEPLINK_KEY to SerenityDeeplink.NEW_VOICE,
                                        )
                                    )
                                )
                                .padding(8.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Image(
                                modifier = GlanceModifier
                                    .padding(3.dp)
                                    .size(28.dp),
                                provider = ImageProvider(uiR.drawable.ic_microphone),
                                colorFilter = ColorFilter.tint(iconsColorProvider),
                                contentDescription = null,
                            )
                        }
                    }
                    Spacer(GlanceModifier.defaultWeight())
                }
            }
        }
    }
}

internal class AllActionsWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = AllActionsWidget()
}
