package com.furianrt.onboarding.internal.ui.container.model

internal sealed class OnboardingPage(
    open val buttonState: OnboardingButtonState,
) {
    data class Greeting(
        override val buttonState: OnboardingButtonState,
    ) : OnboardingPage(buttonState)

    data class Theme(
        override val buttonState: OnboardingButtonState,
    ) : OnboardingPage(buttonState)

    data class Notification(
        override val buttonState: OnboardingButtonState,
    ) : OnboardingPage(buttonState)

    data class Complete(
        override val buttonState: OnboardingButtonState,
    ) : OnboardingPage(buttonState)
}