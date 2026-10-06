package com.furianrt.statistics.internal.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.furianrt.statistics.R
import com.furianrt.statistics.internal.ui.components.GeneralStatsBlock
import com.furianrt.statistics.internal.ui.components.NoteStreakBlock
import com.furianrt.statistics.internal.ui.components.PeriodTabsLayout
import com.furianrt.uikit.components.AppBackground
import com.furianrt.uikit.components.DefaultToolbar
import com.furianrt.uikit.components.MovableToolbarScaffold
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.collectLatest
import java.time.ZonedDateTime

private const val TIME_PERIOD_ITEM_KEY = "time_period"
private const val GENERAL_STATS_ITEM_KEY = "general_stats"
private const val STREAK_STATS_ITEM_KEY = "streak_stats"

@Composable
internal fun StatsScreen(
    openCreateNoteRequest: (date: ZonedDateTime) -> Unit,
    onCloseRequest: () -> Unit,
) {
    val viewModel: StatsViewModel = hiltViewModel()
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val onCloseRequestState by rememberUpdatedState(onCloseRequest)
    val openCreateNoteRequestState by rememberUpdatedState(openCreateNoteRequest)

    LaunchedEffect(Unit) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->
                when (effect) {
                    is StatsEffect.CloseScreen -> onCloseRequestState()
                    is StatsEffect.OpenNoteCreateScreen -> openCreateNoteRequestState(effect.date)
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
    uiState: StatsState,
    onEvent: (event: StatsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val hazeState = rememberHazeState()

    MovableToolbarScaffold(
        modifier = modifier,
        listState = listState,
        enabled = false,
        toolbar = {
            DefaultToolbar(
                modifier = Modifier.statusBarsPadding(),
                title = stringResource(R.string.stats_screen_title),
                onBackClick = { onEvent(StatsEvent.OnButtonBackClick) },
            )
        }
    ) { topPadding ->
        AppBackground(
            modifier = Modifier.hazeSource(hazeState),
            theme = uiState.theme,
        )
        when (uiState.content) {
            is StatsState.Content.Success -> SuccessContent(
                uiState = uiState.content,
                listState = listState,
                contentPadding = PaddingValues(
                    top = topPadding + 16.dp,
                    bottom = 24.dp,
                    start = 16.dp,
                    end = 16.dp,
                ),
                hazeState = hazeState,
                onEvent = onEvent,
            )

            is StatsState.Content.Loading -> LoadingContent()
        }
    }
}

@Composable
private fun SuccessContent(
    uiState: StatsState.Content.Success,
    onEvent: (event: StatsEvent) -> Unit,
    hazeState: HazeState,
    listState: LazyListState,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState,
        contentPadding = contentPadding,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item(
            key = TIME_PERIOD_ITEM_KEY,
            contentType = TIME_PERIOD_ITEM_KEY,
        ) {
            PeriodTabsLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem(),
                periods = uiState.periods,
                selectedPeriod = uiState.selectedPeriod,
                hazeState = hazeState,
                onClick = { onEvent(StatsEvent.OnPeriodSelected(it)) },
            )
        }

        item(
            key = GENERAL_STATS_ITEM_KEY,
            contentType = GENERAL_STATS_ITEM_KEY,
        ) {
            GeneralStatsBlock(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem(),
                stats = uiState.generalStats,
                hazeState = hazeState,
                onNotesClick = { onEvent(StatsEvent.OnNotesStatClick) },
                onGalleryClick = { onEvent(StatsEvent.OnGalleryStatClick) },
                onMoodClick = { onEvent(StatsEvent.OnMoodStatClick) },
            )
        }
        item(
            key = STREAK_STATS_ITEM_KEY,
            contentType = STREAK_STATS_ITEM_KEY,
        ) {
            NoteStreakBlock(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem(),
                stats = uiState.streakStats,
                hazeState = hazeState,
                onDayClick = { onEvent(StatsEvent.OnStreakDayClick(it)) },
            )
        }
    }
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    )
}
