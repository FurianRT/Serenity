package com.furianrt.serenity.ui

import com.furianrt.uikit.entities.UiNote

sealed interface MainState {
    object Loading : MainState
    object Empty : MainState
    data class Success(val notes: List<UiNote>) : MainState
}

sealed interface MainEvent {
    object OnScrollToTopClick : MainEvent
    object OnSettingsClick : MainEvent
}

sealed interface MainEffect {
    object ScrollToTop : MainEffect
}
