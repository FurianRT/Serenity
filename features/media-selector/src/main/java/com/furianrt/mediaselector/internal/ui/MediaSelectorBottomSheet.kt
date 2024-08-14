package com.furianrt.mediaselector.internal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MediaSelectorBottomSheetInternal(
    state: SheetState,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: MediaSelectorViewModel = hiltViewModel()
    val uiState = viewModel.state.collectAsStateWithLifecycle().value
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(viewModel.effect) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->

            }
    }

    ModalBottomSheet(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.tertiaryContainer,
        tonalElevation = 4.dp,
        sheetState = state,
        contentWindowInsets = { WindowInsets(0) },
        dragHandle = { DragHandle() },
        onDismissRequest = onDismissRequest,
    ) {
        when (uiState) {
            is MediaSelectorUiState.Success -> SuccessContent(uiState)
            is MediaSelectorUiState.Loading -> LoadingContent()
            is MediaSelectorUiState.Empty -> EmptyContent()
        }
    }
}

@Composable
private fun SuccessContent(
    uiState: MediaSelectorUiState,
) {
    Spacer(
        modifier = Modifier
            .fillMaxSize(),
    )
}

@Composable
private fun LoadingContent() {
    Spacer(
        modifier = Modifier.fillMaxSize(),
    )
}

@Composable
private fun EmptyContent() {
    Spacer(
        modifier = Modifier.fillMaxSize(),
    )
}

@Composable
private fun DragHandle() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Spacer(
            modifier = Modifier
                .size(40.dp, 4.dp)
                .background(
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(8.dp),
                ),
        )
    }
}

@Preview
@Composable
private fun SuccessContentPreview() {
    SuccessContent(
        uiState = MediaSelectorUiState.Success,
    )
}
