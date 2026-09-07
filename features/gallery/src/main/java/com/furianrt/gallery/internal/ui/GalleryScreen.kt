package com.furianrt.gallery.internal.ui

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.furianrt.gallery.R
import com.furianrt.uikit.R as uiR
import com.furianrt.gallery.internal.ui.composables.PhotoItem
import com.furianrt.gallery.internal.ui.composables.TitleItem
import com.furianrt.gallery.internal.ui.composables.Toolbar
import com.furianrt.gallery.internal.ui.entities.ListItem
import com.furianrt.uikit.components.AppBackground
import com.furianrt.uikit.components.MovableToolbarScaffold
import com.furianrt.uikit.components.MultiChoiceCalendar
import com.furianrt.uikit.components.RegularButton
import com.furianrt.uikit.components.SelectedDate
import com.furianrt.uikit.components.SkipFirstEffect
import com.furianrt.uikit.entities.UiThemeColor
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import com.furianrt.uikit.utils.brighterBy
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate

@Immutable
private data class CalendarState(
    val start: SelectedDate?,
    val end: SelectedDate?,
    val datesWithPhotos: Set<LocalDate>,
)

@Composable
internal fun GalleryScreen(
    openCreateNoteRequest: () -> Unit,
    openMediaViewRequest: (mediaId: String, startDate: LocalDate?, endDate: LocalDate?) -> Unit,
    onCloseRequest: () -> Unit,
) {
    val viewModel: GalleryViewModel = hiltViewModel()
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val hazeState = rememberHazeState()
    val listState = rememberLazyGridState()

    val openCreateNoteRequestState by rememberUpdatedState(openCreateNoteRequest)
    val openMediaViewRequestState by rememberUpdatedState(openMediaViewRequest)
    val onCloseRequestState by rememberUpdatedState(onCloseRequest)

    var calendarState: CalendarState? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->
                when (effect) {
                    is GalleryEffect.CloseScreen -> onCloseRequestState()
                    is GalleryEffect.OpenCreateNoteScreen -> openCreateNoteRequestState()
                    is GalleryEffect.OpenMediaViewScreen -> {
                        openMediaViewRequestState(effect.mediaId, effect.startDate, effect.endDate)
                    }

                    is GalleryEffect.ShowDateSelector -> {
                        calendarState = CalendarState(
                            start = effect.start?.let { SelectedDate(it) },
                            end = effect.end?.let { SelectedDate(it) },
                            datesWithPhotos = effect.datesWithPhotos,
                        )
                    }
                }
            }
    }

    SkipFirstEffect(uiState.dateFilter) {
        listState.scrollToItem(0)
    }

    ScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        listState = listState,
        hazeState = hazeState,
    )

    calendarState?.let { state ->
        MultiChoiceCalendar(
            startDate = state.start,
            endDate = state.end,
            hazeState = hazeState,
            hasNotes = state.datesWithPhotos::contains,
            onDismissRequest = { calendarState = null },
            onDateSelected = { startDate, endDate ->
                viewModel.onEvent(GalleryEvent.OnDateFilterSelected(startDate.date, endDate?.date))
            },
        )
    }
}

@Composable
private fun ScreenContent(
    uiState: GalleryState,
    listState: LazyGridState,
    hazeState: HazeState,
    onEvent: (event: GalleryEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val bottomInsetPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    MovableToolbarScaffold(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        listState = listState,
        enabled = false,
        toolbar = {
            Toolbar(
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .statusBarsPadding(),
                dateFilter = uiState.dateFilter,
                hazeState = hazeState,
                onEvent = onEvent,
            )
        }
    ) { topPadding ->
        AppBackground(
            modifier = Modifier.hazeSource(hazeState, zIndex = 0f),
            theme = uiState.theme,
        )
        AnimatedContent(
            modifier = Modifier.hazeSource(hazeState, zIndex = 1f),
            targetState = uiState.content,
            transitionSpec = {
                if (initialState is GalleryState.Content.Loading) {
                    EnterTransition.None.togetherWith(ExitTransition.None)
                } else {
                    fadeIn().togetherWith(fadeOut())
                }
            },
            contentKey = { it::class.simpleName },
        ) { targetState ->
            when (targetState) {
                is GalleryState.Content.Loading -> LoadingContent()
                is GalleryState.Content.Empty -> EmptyContent(
                    hazeState = hazeState,
                    onEvent = onEvent,
                )

                is GalleryState.Content.Success -> SuccessContent(
                    uiState = targetState,
                    onEvent = onEvent,
                    listState = listState,
                    hazeState = hazeState,
                    contentPadding = PaddingValues(
                        top = topPadding + 12.dp,
                        bottom = bottomInsetPadding + 24.dp,
                        start = 24.dp,
                        end = 24.dp,
                    ),
                )
            }
        }
    }
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize(),
    )
}

@Composable
private fun EmptyContent(
    hazeState: HazeState,
    onEvent: (event: GalleryEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.padding(bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .hazeEffect(
                        state = hazeState,
                        style = HazeDefaults.style(
                            backgroundColor = MaterialTheme.colorScheme.surface,
                            blurRadius = 12.dp,
                            noiseFactor = 0f,
                            tint = HazeTint(Color.Transparent),
                        ),
                    )
                    .padding(horizontal = 4.dp),
                text = stringResource(R.string.gallery_screen_empty_state_title),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.size(12.dp))
            Text(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .hazeEffect(
                        state = hazeState,
                        style = HazeDefaults.style(
                            backgroundColor = MaterialTheme.colorScheme.surface,
                            blurRadius = 12.dp,
                            noiseFactor = 0f,
                            tint = HazeTint(Color.Transparent),
                        ),
                    )
                    .padding(horizontal = 4.dp)
                    .alpha(0.5f),
                text = stringResource(R.string.gallery_screen_empty_state_body),
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 0.9f,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.size(32.dp))
            RegularButton(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(),
                text = stringResource(R.string.gallery_screen_empty_state_button),
                icon = painterResource(uiR.drawable.ic_add),
                onClick = { onEvent(GalleryEvent.OnCreateNoteClick) },
            )
        }
    }
}

@Composable
private fun SuccessContent(
    uiState: GalleryState.Content.Success,
    onEvent: (event: GalleryEvent) -> Unit,
    listState: LazyGridState,
    contentPadding: PaddingValues,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
) {
    val colorScheme = MaterialTheme.colorScheme

    val listSpan = 3
    val photoBackground = remember(colorScheme) { colorScheme.surface.brighterBy(0.15f) }

    LazyVerticalGrid(
        modifier = modifier.fillMaxSize(),
        state = listState,
        columns = GridCells.Fixed(listSpan),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        itemsIndexed(
            items = uiState.items,
            key = { _, item -> item.id },
            span = { _, item ->
                when (item) {
                    is ListItem.Title -> GridItemSpan(listSpan)
                    is ListItem.Photo -> GridItemSpan(1)
                }
            },
            contentType = { _, item -> item::class.simpleName },
        ) { index, item ->
            when (item) {
                is ListItem.Title -> TitleItem(
                    modifier = Modifier
                        .padding(
                            top = if (index != 0) 20.dp else 0.dp,
                            bottom = 16.dp,
                        )
                        .animateItem(),
                    item = item,
                    hazeState = hazeState,
                )

                is ListItem.Photo -> PhotoItem(
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .animateItem(),
                    item = item,
                    background = photoBackground,
                    onClick = { onEvent(GalleryEvent.OnPhotoItemClick(it)) },
                )
            }
        }
    }
}

@PreviewWithBackground
@Composable
private fun SuccessPreview() {
    SerenityTheme {
        ScreenContent(
            uiState = GalleryState(
                theme = UiThemeColor.defaultTheme,
                dateFilter = "30 Sep 1992",
                content = GalleryState.Content.Success(
                    items = buildList {
                        repeat(30) { index ->
                            if (index % 5 == 0) {
                                add(ListItem.Title("$index Sep 2026"))
                            } else {
                                add(
                                    ListItem.Photo(
                                        id = index.toString(),
                                        uri = Uri.EMPTY,
                                        label = "22 Feb 2003",
                                        rotation = 0f,
                                    )
                                )
                            }
                        }
                    }
                ),
            ),
            listState = rememberLazyGridState(),
            hazeState = rememberHazeState(),
            onEvent = {},
        )
    }
}

@PreviewWithBackground
@Composable
private fun EmptyPreview() {
    SerenityTheme {
        ScreenContent(
            uiState = GalleryState(
                theme = UiThemeColor.defaultTheme,
                dateFilter = "30 Sep 1992",
                content = GalleryState.Content.Empty,
            ),
            listState = rememberLazyGridState(),
            hazeState = rememberHazeState(),
            onEvent = {},
        )
    }
}
