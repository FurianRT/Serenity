package com.furianrt.billing.internal.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
internal class BillingViewModel @Inject constructor(

) : ViewModel() {

    val state: StateFlow<BillingState> = MutableStateFlow(BillingState)

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