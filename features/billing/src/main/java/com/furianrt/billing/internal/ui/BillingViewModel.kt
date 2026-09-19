package com.furianrt.billing.internal.ui

import androidx.lifecycle.ViewModel
import com.furianrt.billing.internal.ui.entities.SubscriptionPlan
import com.furianrt.domain.repositories.AppearanceRepository
import com.furianrt.uikit.entities.UiThemeColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
internal class BillingViewModel @Inject constructor(
    private val appearanceRepository: AppearanceRepository,
) : ViewModel() {

    val state: StateFlow<BillingState> = MutableStateFlow(
        BillingState(
            theme = UiThemeColor.fromId(appearanceRepository.getAppThemeColorId().value),
            plans = listOf(
                SubscriptionPlan.Yearly(
                    finalPrice = 0f,
                    oldPrice = 0f,
                    discount = 10,
                    pricePerMonth = "",
                    iSelected = true,
                ),
                SubscriptionPlan.Monthly(
                    finalPrice = 0f,
                    oldPrice = 0f,
                    discount = 10,
                    iSelected = false,
                ),
                SubscriptionPlan.Permanent(
                    finalPrice = 0f,
                    oldPrice = 0f,
                    discount = 10,
                    iSelected = false,
                ),
            ),
        )
    )

    private val _effect = MutableSharedFlow<BillingEffect>(extraBufferCapacity = 5)
    val effect: SharedFlow<BillingEffect> = _effect.asSharedFlow()

    fun onEvent(event: BillingEvent) {
        when (event) {
            is BillingEvent.OnButtonCloseClick -> {
                _effect.tryEmit(BillingEffect.CloseScreen)
            }
        }
    }
}