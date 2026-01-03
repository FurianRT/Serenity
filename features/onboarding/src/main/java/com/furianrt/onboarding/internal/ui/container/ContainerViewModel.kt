package com.furianrt.onboarding.internal.ui.container

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.domain.managers.ResourcesManager
import com.furianrt.domain.repositories.AppearanceRepository
import com.furianrt.onboarding.R
import com.furianrt.onboarding.internal.ui.container.model.OnboardingButtonState
import com.furianrt.onboarding.internal.ui.container.model.OnboardingPage
import com.furianrt.permissions.utils.PermissionsUtils
import com.furianrt.uikit.entities.UiThemeColor
import com.furianrt.uikit.extensions.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import com.furianrt.uikit.R as uiR

private const val MAIN_CLICK_DEBOUNCE = 350L

@HiltViewModel
internal class ContainerViewModel @Inject constructor(
    private val resourcesManager: ResourcesManager,
    private val appearanceRepository: AppearanceRepository,
    private val permissionsUtils: PermissionsUtils,
) : ViewModel() {

    private val selectedPageState = MutableStateFlow<OnboardingPage>(
        OnboardingPage.Greeting(
            buttonState = OnboardingButtonState(
                mainButtonTitle = resourcesManager.getString(
                    R.string.onboarding_start_button_title,
                ),
                skipButton = OnboardingButtonState.ButtonState.Visible(
                    title = resourcesManager.getString(uiR.string.action_not_now),
                ),
            ),
        )
    )

    val state: StateFlow<ContainerState> = combine(
        selectedPageState,
        appearanceRepository.getAppThemeColorId(),
        ::buildState,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ContainerState.Loading,
    )

    private val _effect = MutableSharedFlow<ContainerEffect>(extraBufferCapacity = 5)
    val effect: SharedFlow<ContainerEffect> = _effect.asSharedFlow()

    private var mainCLickJob: Job? = null

    fun onEvent(event: ContainerEvent) {
        when (event) {
            is ContainerEvent.OnMainButtonClick -> onMainButtonClick(event.selectedThemeColor)
            is ContainerEvent.OnSkipButtonClick -> onSkipButtonClick()
            is ContainerEvent.OnNotificationsPermissionSelected -> checkNotificationsPermission()
        }
    }

    private fun onMainButtonClick(selectedThemeColor: UiThemeColor) {
        if (mainCLickJob?.isCompleted != false) {
            mainCLickJob = launch {
                when (selectedPageState.value) {
                    is OnboardingPage.Greeting -> onGreetingMainButtonClick()
                    is OnboardingPage.Theme -> onThemeMainButtonClick(selectedThemeColor)
                    is OnboardingPage.Notification -> onNotificationsMainButtonClick()
                    is OnboardingPage.Complete -> onCompleteMainButtonClick()
                }
                delay(MAIN_CLICK_DEBOUNCE)
            }
        }
    }

    private fun onGreetingMainButtonClick() {
        showThemePage()
    }

    private fun onThemeMainButtonClick(selectedThemeColor: UiThemeColor) {
        launch {
            appearanceRepository.updateAppThemeColor(selectedThemeColor.id)
            showNotificationsPage()
        }
    }

    private fun onNotificationsMainButtonClick() {
        _effect.tryEmit(ContainerEffect.RequestNotificationsPermission)
    }

    private fun onCompleteMainButtonClick() {
        _effect.tryEmit(ContainerEffect.CloseScreen)
    }

    private fun onSkipButtonClick() {
        when (selectedPageState.value) {
            is OnboardingPage.Greeting -> _effect.tryEmit(ContainerEffect.CloseScreen)
            is OnboardingPage.Notification -> showCompletePage()
            else -> Unit
        }
    }

    private fun showThemePage() {
        selectedPageState.update {
            OnboardingPage.Theme(
                buttonState = OnboardingButtonState(
                    mainButtonTitle = resourcesManager.getString(uiR.string.action_select),
                    skipButton = OnboardingButtonState.ButtonState.Gone,
                ),
            )
        }
    }

    private fun showNotificationsPage() {
        selectedPageState.update {
            OnboardingPage.Notification(
                buttonState = OnboardingButtonState(
                    mainButtonTitle = resourcesManager.getString(uiR.string.action_allow),
                    skipButton = OnboardingButtonState.ButtonState.Visible(
                        title = resourcesManager.getString(uiR.string.action_skip)
                    ),
                ),
            )
        }
    }

    private fun showCompletePage() {
        selectedPageState.update {
            OnboardingPage.Complete(
                buttonState = OnboardingButtonState(
                    mainButtonTitle = resourcesManager.getString(
                        R.string.onboarding_go_to_diary_button_title,
                    ),
                    skipButton = OnboardingButtonState.ButtonState.Gone,
                ),
            )
        }
    }

    private fun checkNotificationsPermission() {
        if (permissionsUtils.hasNotificationsPermission()) {
            showCompletePage()
        } else {
            _effect.tryEmit(ContainerEffect.ShowNotificationsPermissionsDeniedDialog)
        }
    }


    private fun buildState(
        selectedPage: OnboardingPage,
        appThemeColorId: String?,
    ): ContainerState = ContainerState.Success(
        page = selectedPage,
        appThemeColor = UiThemeColor.fromId(appThemeColorId),
    )
}