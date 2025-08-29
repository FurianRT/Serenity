package com.furianrt.settings.internal.domain.usecases

import com.furianrt.uikit.entities.UiThemeColor
import javax.inject.Inject

internal class GetAppLightThemeListUseCase @Inject constructor() {
    operator fun invoke(): List<UiThemeColor> = listOf(
        UiThemeColor.LIGHT_WHITE,
        UiThemeColor.LIGHT_BLUE,
        UiThemeColor.GREEN_LIGHT,
        UiThemeColor.PINK_LIGHT,
        UiThemeColor.PURPLE_LIGHT,
        UiThemeColor.ORANGE_LIGHT,
    )
}