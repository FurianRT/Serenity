package com.furianrt.onboarding.internal.ui.container.model

internal data class OnboardingButtonState(
    val mainButtonTitle: String,
    val skipButton: ButtonState,
) {
    sealed interface ButtonState {
        data class Visible(
            val title: String,
        ) : ButtonState

        data object Gone : ButtonState
    }
}