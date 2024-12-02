package com.furianrt.search.internal.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.furianrt.search.internal.ui.composables.Toolbar
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun SearchScreen(
    onCloseRequest: () -> Unit,
) {
    val viewModel: SearchViewModel = hiltViewModel()
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(viewModel.effect) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->

            }
    }
}

@Composable
private fun ScreenContent(
    uiState: SearchUiState,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            Toolbar(
                queryState = uiState.searchQuery,
                onBackClick = {},
                onCalendarClick = {},
            )
        },
    ) {
        Column(

        ) {

        }
    }
}
