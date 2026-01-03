package com.furianrt.serenity

import com.furianrt.uikit.entities.UiThemeColor
import com.furianrt.uikit.theme.NoteFont

internal data class MainState(
    val appColor: UiThemeColor = UiThemeColor.STORM_IN_THE_NIGHT_BLUE_LIGHT,
    val appFont: NoteFont = NoteFont.NotoSans,
    val isScreenLocked: Boolean = false,
    val isOnboardingNeeded: Boolean = false,
)

internal sealed interface MainEvent {
    data object OnUnlockScreenRequest : MainEvent
    data object OnOnboardingCompleted : MainEvent
}
