package com.furianrt.mediaselector.internal.ui.entities

internal sealed interface SelectionState {
    data class Selected(val order: Int) : SelectionState
    data object Default : SelectionState
}