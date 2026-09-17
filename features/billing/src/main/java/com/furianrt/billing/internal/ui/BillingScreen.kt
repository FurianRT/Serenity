package com.furianrt.billing.internal.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun BillingScreen(
    onCloseRequest: () -> Unit,
) {
    val viewModel: BillingViewModel = hiltViewModel()
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val onCloseRequestState by rememberUpdatedState(onCloseRequest)

    LaunchedEffect(Unit) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->
                when (effect) {
                    BillingEffect.CloseScreen -> onCloseRequestState()
                }
            }
    }

    Content(
        uiState = uiState,
        onEvent = viewModel::onEvent,
    )
}

@Composable
private fun Content(
    uiState: BillingState,
    onEvent: (event: BillingEvent) -> Unit,
    modifier: Modifier = Modifier,
) {

}
