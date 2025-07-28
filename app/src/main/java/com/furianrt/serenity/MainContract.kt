package com.furianrt.serenity

import com.furianrt.uikit.entities.UiThemeColor
import com.furianrt.uikit.theme.NoteFont

internal data class MainState(
    val appColor: UiThemeColor = UiThemeColor.DISTANT_CASTLE_GREEN,
    val appFont: NoteFont = NoteFont.QuickSand,
    val isScreenLocked: Boolean = false,
)

internal sealed interface MainEvent {
    data object OnUnlockScreenRequest : MainEvent
}
