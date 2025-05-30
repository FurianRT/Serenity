package com.furianrt.settings.internal.domain

import com.furianrt.uikit.entities.UiThemeColor
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

internal class GetAppThemeListUseCase @Inject constructor() {
    operator fun invoke(): Flow<ImmutableList<UiThemeColor>> = flowOf(buildThemesList())

    private fun buildThemesList(): ImmutableList<UiThemeColor> = persistentListOf(
        UiThemeColor.SCANDI_GRANDPA_GRAY_DARK,
        UiThemeColor.DISTANT_CASTLE_GREEN,
        UiThemeColor.EUPHORIA_BLUE_DARK,
        UiThemeColor.STORM_IN_THE_NIGHT_BLUE,
        UiThemeColor.STORM_IN_THE_NIGHT_BLUE_LIGHT,
        UiThemeColor.EUPHORIA_BLUE,
        UiThemeColor.UNICORN_2012_BLUE,
        UiThemeColor.EUPHORIA_PINK,
        UiThemeColor.UNICORN_2012_PINK_DARK,
        UiThemeColor.EUPHORIA_VIOLET,
        UiThemeColor.VAMPIRE_RED_DARK,
        UiThemeColor.VAMPIRE_BLACK,
        UiThemeColor.IRIS_RED,
        UiThemeColor.IRIS_RED_DARK,
        UiThemeColor.BEIGE_CENSORSHIP_BLACK,
        UiThemeColor.BEIGE_CENSORSHIP_BROWN_DARK,
    )
}