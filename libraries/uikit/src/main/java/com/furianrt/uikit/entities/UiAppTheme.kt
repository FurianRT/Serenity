package com.furianrt.uikit.entities

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import com.furianrt.domain.entities.AppTheme
import com.furianrt.uikit.theme.getDefaultDarkColorScheme
import com.furianrt.uikit.theme.getDefaultLightColorScheme

sealed class UiAppTheme(
    open val id: String,
    open val image: UiThemeImage?,
    open val colorScheme: ColorScheme,
    open val isLight: Boolean,
) {
    data class Regular(
        val color: UiThemeColor,
    ) : UiAppTheme(
        id = color.id,
        image = color.image,
        colorScheme = color.colorScheme,
        isLight = color.isLight,
    )

    data class Custom(
        override val id: String,
        override val image: UiThemeImage,
        override val colorScheme: ColorScheme,
        override val isLight: Boolean,
    ) : UiAppTheme(
        id = id,
        image = image,
        colorScheme = colorScheme,
        isLight = isLight,
    )

    companion object {
        val defaultTheme: UiAppTheme = Regular(
            color = UiThemeColor.defaultTheme,
        )
    }
}

fun AppTheme.toUiAppTheme() = when (this) {
    is AppTheme.Regular -> UiAppTheme.Regular(
        color = UiThemeColor.fromId(id),
    )

    is AppTheme.Custom -> UiAppTheme.Custom(
        id = background.id,
        image = UiThemeImage(
            source = UiThemeImage.Source.Link(
                uri = background.uri,
            ),
            scaleType = UiThemeImage.ScaleType.CROP_ALIGN_CENTER,
        ),
        colorScheme = if (background.isLight) {
            getDefaultLightColorScheme(
                surface = Color(background.primaryColor),
                primaryContainer = Color(background.accentColor),
            )
        } else {
            getDefaultDarkColorScheme(
                surface = Color(background.primaryColor),
                primaryContainer = Color(background.accentColor),
            )
        },
        isLight = background.isLight,
    )
}
