package com.furianrt.serenity

internal data class MainState(
    val isScreenLocked: Boolean = false,
)

internal sealed interface MainEvent {
    data object OnUnlockScreenRequest : MainEvent
}
