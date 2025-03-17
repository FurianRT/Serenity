package com.furianrt.settings.internal.entities

import com.furianrt.uikit.entities.UiThemeColor
import kotlinx.collections.immutable.ImmutableList

internal data class AppTheme(
    val title: String,
    val colors: ImmutableList<UiThemeColor>,
)