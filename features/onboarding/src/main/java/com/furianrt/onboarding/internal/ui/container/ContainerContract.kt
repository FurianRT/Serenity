package com.furianrt.onboarding.internal.ui.container

import com.furianrt.onboarding.internal.ui.container.model.OnboardingPage
import com.furianrt.uikit.entities.UiAppTheme

internal sealed interface ContainerState {
    data object Loading : ContainerState
    data class Success(
        val page: OnboardingPage,
        val appTheme: UiAppTheme,
    ) : ContainerState
}

internal sealed interface ContainerEvent {
    data class OnMainButtonClick(
        val selectedTheme: UiAppTheme,
    ) : ContainerEvent

    data object OnSkipButtonClick : ContainerEvent
    data object OnNotificationsPermissionSelected : ContainerEvent
}

internal sealed interface ContainerEffect {
    data object CloseScreen : ContainerEffect
    data object RequestNotificationsPermission : ContainerEffect
    data object ShowNotificationsPermissionsDeniedDialog : ContainerEffect
}
