package com.furianrt.serenity.extensions

import com.furianrt.domain.entities.ThemeColor
import com.furianrt.uikit.entities.UiThemeColor

fun ThemeColor.toUiThemeColor() = when (this) {
    ThemeColor.GREEN -> UiThemeColor.GREEN
    ThemeColor.BLACK -> UiThemeColor.BLACK
    ThemeColor.BLUE -> UiThemeColor.BLUE
}