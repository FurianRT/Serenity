package com.furianrt.billing.internal.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.billing.R
import com.furianrt.uikit.R as uiR
import com.furianrt.billing.internal.data.SerenityProductId
import com.furianrt.billing.internal.domain.entities.SerenityPlusPlan
import com.furianrt.billing.internal.domain.entities.SyncResult
import com.furianrt.billing.internal.domain.repository.BillingRepository
import com.furianrt.billing.internal.ui.entities.BenefitItem
import com.furianrt.billing.internal.ui.entities.ErrorMessageType
import com.furianrt.billing.internal.ui.mappers.SubscriptionPlanMapper
import com.furianrt.common.ErrorTracker
import com.furianrt.common.SerenityTermsLink
import com.furianrt.core.combine
import com.furianrt.domain.managers.ResourcesManager
import com.furianrt.domain.repositories.AppearanceRepository
import com.furianrt.uikit.entities.UiThemeColor
import com.furianrt.uikit.extensions.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
internal class BillingViewModel @Inject constructor(
    appearanceRepository: AppearanceRepository,
    private val billingRepository: BillingRepository,
    private val resourcesManager: ResourcesManager,
    private val errorTracker: ErrorTracker,
    private val mapper: SubscriptionPlanMapper,
) : ViewModel() {

    private val benefits = listOf(
        BenefitItem.CUSTOM_STICKERS,
        BenefitItem.CUSTOM_BACKGROUNDS,
        BenefitItem.MOOD_INSIGHTS,
        BenefitItem.EXPORT_PDF,
        BenefitItem.MORE_FEATURES,
    )

    private val selectedPlanIdState =
        MutableStateFlow(SerenityProductId.YEARLY_SUBSCRIPTIONS_PRODUCT_ID)
    private val showButtonProgressState = MutableStateFlow(false)
    private val errorMessageState = MutableStateFlow<ErrorMessageType?>(null)

    val state: StateFlow<BillingState> = combine(
        billingRepository.hasSerenityPlus()
            .onEach { hasSerenityPlus ->
                if (hasSerenityPlus) {
                    _effect.tryEmit(BillingEffect.CloseScreen)
                }
            },
        billingRepository.getSerenityPlusPlans(),
        selectedPlanIdState,
        showButtonProgressState,
        errorMessageState,
        appearanceRepository.getAppThemeColorId(),
    ) { _, plans, selectedPlanId, showButtonProgress, errorMessage, appThemeColorId ->
        buildState(
            plans = plans,
            selectedPlanId = selectedPlanId,
            showButtonProgress = showButtonProgress,
            errorMessage = errorMessage,
            appThemeColorId = appThemeColorId,
        )
    }.flowOn(
        context = Dispatchers.Default,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = buildState(
            plans = emptyList(),
            selectedPlanId = selectedPlanIdState.value,
            showButtonProgress = showButtonProgressState.value,
            errorMessage = errorMessageState.value,
            appThemeColorId = appearanceRepository.getAppThemeColorId().value,
        )
    )

    private val _effect = MutableSharedFlow<BillingEffect>(extraBufferCapacity = 5)
    val effect: SharedFlow<BillingEffect> = _effect.asSharedFlow()

    init {
        syncPurchases()
    }

    fun onEvent(event: BillingEvent) {
        when (event) {
            is BillingEvent.OnButtonCloseClick -> _effect.tryEmit(BillingEffect.CloseScreen)
            is BillingEvent.OnSubscribeClick -> onSubscribeClick()
            is BillingEvent.OnPlanSelected -> onPlanSelected(event.id)
            is BillingEvent.OnPrivacyPolicyClick -> onPrivacyPolicyClick()
            is BillingEvent.OnTermsClick -> onTermsClick()
            is BillingEvent.OnRetryClick -> onRetryClick()
        }
    }

    private fun onSubscribeClick() {
        showButtonProgressState.update { true }
        launch {
            billingRepository.launchBillingFlow(selectedPlanIdState.value)
                .onSuccess { showButtonProgressState.update { false } }
                .onFailure { error ->
                    showButtonProgressState.update { false }
                    errorTracker.trackNonFatalError(error)
                    _effect.tryEmit(BillingEffect.ShowGeneralErrorMessage)
                }
        }
    }

    private fun onPlanSelected(id: String) {
        selectedPlanIdState.update { id }
    }

    private fun onPrivacyPolicyClick() {
        _effect.tryEmit(BillingEffect.OpenLink(SerenityTermsLink.PRIVACY_POLICY_LINK))
    }

    private fun onTermsClick() {
        _effect.tryEmit(BillingEffect.OpenLink(SerenityTermsLink.TERMS_AND_CONDITIONS_LINK))
    }

    private fun onRetryClick() {
        syncPurchases()
    }

    private fun syncPurchases() {
        errorMessageState.update { null }
        launch {
            when (billingRepository.sync()) {
                is SyncResult.Ok -> errorMessageState.update { null }
                is SyncResult.BillingUnavailable -> errorMessageState.update {
                    ErrorMessageType.BILLING_UNAVAILABLE
                }

                is SyncResult.Other -> errorMessageState.update {
                    ErrorMessageType.GENERAL
                }
            }
        }
    }

    private fun buildState(
        plans: List<SerenityPlusPlan>,
        selectedPlanId: String,
        showButtonProgress: Boolean,
        errorMessage: ErrorMessageType?,
        appThemeColorId: String?,
    ) = BillingState(
        benefits = benefits,
        showButtonProgress = showButtonProgress || plans.isEmpty(),
        theme = UiThemeColor.fromId(appThemeColorId),
        content = when {
            errorMessage != null -> BillingState.Content.Failure(errorMessage)
            plans.isEmpty() -> BillingState.Content.Loading
            else -> {
                val selectedPlan = plans.first { it.productId == selectedPlanId }
                BillingState.Content.Success(
                    plans = plans.map(mapper::map),
                    selectedPlanId = selectedPlanId,
                    buttonTitle = if (selectedPlan.hasTrial) {
                        resourcesManager.getString(R.string.billing_start_trial_title)
                    } else {
                        resourcesManager.getString(uiR.string.action_continue)
                    },
                )
            }
        },
    )
}