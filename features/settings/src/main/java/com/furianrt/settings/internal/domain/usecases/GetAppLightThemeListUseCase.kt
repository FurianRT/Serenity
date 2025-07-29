package com.furianrt.settings.internal.domain.usecases

import com.furianrt.uikit.entities.UiThemeColor
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import javax.inject.Inject

internal class GetAppLightThemeListUseCase @Inject constructor() {
    operator fun invoke(): ImmutableList<UiThemeColor> = persistentListOf(
        UiThemeColor.LIGHT_BLUE,
        UiThemeColor.GREEN_LIGHT,
        UiThemeColor.PINK_LIGHT,
        UiThemeColor.PURPLE_LIGHT,
        UiThemeColor.ORANGE_LIGHT,
    )
}