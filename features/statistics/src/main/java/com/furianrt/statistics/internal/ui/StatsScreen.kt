package com.furianrt.statistics.internal.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.furianrt.statistics.R
import com.furianrt.statistics.internal.domain.entities.TimePeriod
import com.furianrt.statistics.internal.ui.components.GeneralStatsBlock
import com.furianrt.statistics.internal.ui.components.MoodInsightsBlock
import com.furianrt.statistics.internal.ui.components.NoteStreakBlock
import com.furianrt.statistics.internal.ui.components.PeriodTabsLayout
import com.furianrt.statistics.internal.ui.entities.GeneralStats
import com.furianrt.statistics.internal.ui.entities.MoodStats
import com.furianrt.statistics.internal.ui.entities.StreakDay
import com.furianrt.statistics.internal.ui.entities.StreakStats
import com.furianrt.uikit.components.AppBackground
import com.furianrt.uikit.components.DefaultToolbar
import com.furianrt.uikit.components.MovableToolbarScaffold
import com.furianrt.uikit.entities.UiThemeColor
import com.furianrt.uikit.theme.SerenityTheme
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.ZonedDateTime

private const val TIME_PERIOD_ITEM_KEY = "time_period"
private const val GENERAL_STATS_ITEM_KEY = "general_stats"
private const val STREAK_STATS_ITEM_KEY = "streak_stats"
private const val MOOD_STATS_ITEM_KEY = "mood_stats"
private const val MOOD_BLOCK_INDEX = 3

@Composable
internal fun StatsScreen(
    openCreateNoteRequest: (date: ZonedDateTime) -> Unit,
    openGalleryRequest: (startDate: LocalDate?) -> Unit,
    openNoteSearchRequest: (startDate: LocalDate?, endDate: LocalDate?) -> Unit,
    openBillingScreen: () -> Unit,
    onCloseRequest: () -> Unit,
) {
    val viewModel: StatsViewModel = hiltViewModel()
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val listState = rememberLazyListState()

    val onCloseRequestState by rememberUpdatedState(onCloseRequest)
    val openCreateNoteRequestState by rememberUpdatedState(openCreateNoteRequest)
    val openGalleryRequestState by rememberUpdatedState(openGalleryRequest)
    val openNoteSearchRequestState by rememberUpdatedState(openNoteSearchRequest)
    val openBillingScreenState by rememberUpdatedState(openBillingScreen)

    LaunchedEffect(Unit) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->
                when (effect) {
                    is StatsEffect.CloseScreen -> onCloseRequestState()
                    is StatsEffect.OpenNoteCreateScreen -> openCreateNoteRequestState(effect.date)
                    is StatsEffect.OpenGalleryRequest -> openGalleryRequestState(effect.startDate)
                    is StatsEffect.OpenBillingScreen -> openBillingScreenState()
                    is StatsEffect.OpenNoteSearchRequest -> {
                        openNoteSearchRequestState(effect.startDate, effect.endDate)
                    }

                    is StatsEffect.ScrollToMoodBlock -> {
                        listState.animateScrollToItem(MOOD_BLOCK_INDEX)
                    }
                }
            }
    }

    Content(
        uiState = uiState,
        listState = listState,
        onEvent = viewModel::onEvent,
    )
}

@Composable
private fun Content(
    uiState: StatsState,
    listState: LazyListState,
    onEvent: (event: StatsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {

    val hazeState = rememberHazeState()

    val bottomInsetPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

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
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .widthIn(max = 450.dp),
                uiState = uiState.content,
                listState = listState,
                contentPadding = PaddingValues(
                    top = topPadding + 12.dp,
                    bottom = bottomInsetPadding + 24.dp,
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
        verticalArrangement = Arrangement.spacedBy(16.dp),
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
        item(
            key = MOOD_STATS_ITEM_KEY,
            contentType = MOOD_STATS_ITEM_KEY,
        ) {
            MoodInsightsBlock(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem(),
                stats = uiState.moodStats,
                hazeState = hazeState,
                onSerenityPlusClick = { onEvent(StatsEvent.OnSerenityPlusClick) },
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

@Preview
@Composable
private fun Preview() {
    SerenityTheme {
        Content(
            uiState = StatsState(
                theme = UiThemeColor.defaultTheme,
                content = StatsState.Content.Success(
                    periods = TimePeriod.entries.toList(),
                    selectedPeriod = TimePeriod.ONE_MONTH,
                    generalStats = GeneralStats(
                        notesCount = 142,
                        notesChange = 12f,
                        mediaCount = 251,
                        mediaChange = -17.2f,
                        averageMood = 4.8f,
                        moodChange = -0.2f,
                    ),
                    streakStats = StreakStats(
                        currentStreak = 23,
                        longestStreak = 32,
                        streakDays = buildList {
                            repeat(7) { index ->
                                add(
                                    StreakDay(
                                        hasNotes = index % 2 == 0,
                                        date = LocalDate.now(),
                                    ),
                                )
                            }
                        },
                    ),
                    moodStats = MoodStats(
                        bestDaysOfWeek = listOf("Sunday", "Friday"),
                        notesCount = 242,
                        pieChartData = listOf(
                            MoodStats.Mood(
                                level = MoodStats.Level.TERRIBLE,
                                percent = 0f,
                            ),
                            MoodStats.Mood(
                                level = MoodStats.Level.BAD,
                                percent = 10f,
                            ),
                            MoodStats.Mood(
                                level = MoodStats.Level.SAD,
                                percent = 15f,
                            ),
                            MoodStats.Mood(
                                level = MoodStats.Level.NORMAL,
                                percent = 20f,
                            ),
                            MoodStats.Mood(
                                level = MoodStats.Level.GOOD,
                                percent = 25f,
                            ),
                            MoodStats.Mood(
                                level = MoodStats.Level.PERFECT,
                                percent = 25f,
                            ),
                        ),
                        chartData = buildList {
                            repeat(5) { index ->
                                add(
                                    MoodStats.ChartEntry(
                                        date = ZonedDateTime.now().plusDays(index.toLong()),
                                        averageMood = index + 1f,
                                    ),
                                )
                            }
                        },
                    ),
                ),
            ),
            listState = rememberLazyListState(),
            onEvent = {},
        )
    }
}
