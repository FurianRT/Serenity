package com.furianrt.serenity.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.furianrt.assistant.api.AssistantLogo
import com.furianrt.core.buildImmutableList
import com.furianrt.notecontent.entities.UiNoteContent
import com.furianrt.serenity.ui.MainScrollState.ScrollDirection
import com.furianrt.serenity.ui.composables.BottomNavigationBar
import com.furianrt.serenity.ui.composables.NoteListItem
import com.furianrt.serenity.ui.composables.Toolbar
import com.furianrt.serenity.ui.entities.MainScreenNote
import com.furianrt.uikit.components.pullRefresh
import com.furianrt.uikit.components.rememberPullRefreshState
import com.furianrt.uikit.extensions.addSerenityBackground
import com.furianrt.uikit.extensions.drawBottomShadow
import com.furianrt.uikit.theme.SerenityTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ScrollStrategy

private const val SHOW_SCROLL_TO_TOP_MIN_ITEM_INDEX = 3
private const val ANIM_PULL_SUCCESS_DURATION = 400

@Composable
internal fun MainScreen(
    navHostController: NavHostController,
    screenState: MainScreenState = rememberMainState(),
) {
    val viewModel: MainViewModel = hiltViewModel()
    val uiState = viewModel.state.collectAsStateWithLifecycle().value

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is MainEffect.ScrollToTop -> screenState.scrollToTop()
                is MainEffect.OpenScreen -> navHostController.navigate("Note/${effect.noteId}")
            }
        }
    }

    MainScreenContent(
        modifier = Modifier
            .fillMaxSize()
            .addSerenityBackground()
            .systemBarsPadding()
            .clipToBounds(),
        uiState = uiState,
        screenState = screenState,
        onEvent = viewModel::onEvent,
    )
}

@Composable
private fun MainScreenContent(
    uiState: MainUiState,
    onEvent: (event: MainEvent) -> Unit,
    modifier: Modifier = Modifier,
    screenState: MainScreenState = rememberMainState(),
) {
    val scope = rememberCoroutineScope()
    val scale = remember { Animatable(0f) }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomCenter,
    ) {
        val haptic = LocalHapticFeedback.current
        val pullRefreshState = rememberPullRefreshState(
            refreshing = false,
            refreshThreshold = 32.dp,
            onRefresh = {},
            onThresholdPassed = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                scope.launch {
                    scale.animateTo(
                        targetValue = 0.1f,
                        animationSpec = tween(durationMillis = ANIM_PULL_SUCCESS_DURATION / 2),
                    )
                    scale.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(durationMillis = ANIM_PULL_SUCCESS_DURATION / 2),
                    )
                }
            },
        )

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            AssistantLogo(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .size(48.dp)
                    .drawWithContent {
                        clipRect(
                            right = size.width * 2f,
                            left = -size.width,
                            top = -size.height,
                            bottom = pullRefreshState.position + 2.dp.toPx(),
                        ) {
                            this@drawWithContent.drawContent()
                        }
                    }
                    .graphicsLayer {
                        val pullScaleRange = 0.2f
                        val pullScale = 1f - pullScaleRange * (1f - pullRefreshState.progress)
                        val resultScale = pullScale + scale.value
                        scaleX = resultScale
                        scaleY = resultScale
                        alpha = pullRefreshState.progress
                    },
            )
        }
        CollapsingToolbarScaffold(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(state = pullRefreshState, enabled = uiState.assistantHint == null)
                .nestedScroll(screenState.scrollConnection),
            state = screenState.toolbarState,
            scrollStrategy = ScrollStrategy.EnterAlwaysCollapsed,
            toolbarModifier = Modifier.drawBehind {
                if (screenState.listState.firstVisibleItemScrollOffset > 0) {
                    drawBottomShadow()
                }
            },
            toolbar = {
                Toolbar(
                    toolbarScaffoldState = screenState.toolbarState,
                    listState = screenState.listState,
                    assistantHint = uiState.assistantHint,
                    onSettingsClick = { onEvent(MainEvent.OnSettingsClick) },
                    onSearchClick = { onEvent(MainEvent.OnSearchClick) },
                    onAssistantHintCLick = { onEvent(MainEvent.OnAssistantHintClick) },
                )
            },
        ) {
            Spacer(modifier = Modifier)
            when (uiState) {
                is MainUiState.Loading -> MainLoading()
                is MainUiState.Empty -> MainEmpty()
                is MainUiState.Success -> MainSuccess(
                    notes = uiState.notes,
                    listState = screenState.listState,
                    onEvent = onEvent,
                )
            }
        }

        val needToShowScrollUpButton by remember {
            derivedStateOf {
                screenState.listState.firstVisibleItemIndex > SHOW_SCROLL_TO_TOP_MIN_ITEM_INDEX
            }
        }

        val needToHideNavigation by remember(screenState.scrollState.scrollDirection) {
            derivedStateOf {
                uiState.hasNotes && screenState.scrollState.scrollDirection == ScrollDirection.DOWN
            }
        }

        BottomNavigationBar(
            onScrollToTopClick = { onEvent(MainEvent.OnScrollToTopClick) },
            needToHideNavigation = { needToHideNavigation },
            needToShowScrollUpButton = { needToShowScrollUpButton },
            onAddNoteClick = { onEvent(MainEvent.OnAddNoteClick) },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MainSuccess(
    notes: ImmutableList<MainScreenNote>,
    listState: LazyListState,
    onEvent: (event: MainEvent) -> Unit,
) {
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(start = 8.dp, end = 8.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(count = notes.count(), key = { notes[it].id }) { index ->
            NoteListItem(
                modifier = Modifier.animateItemPlacement(),
                note = notes[index],
                onClick = { onEvent(MainEvent.OnNoteClick(it)) },
                onTagClick = { onEvent(MainEvent.OnNoteTagClick(it)) },
            )
        }
    }
}

@Composable
private fun MainEmpty() {
}

@Composable
private fun MainLoading() {
}

@Preview
@Composable
private fun MainScreenSuccessPreview() {
    SerenityTheme {
        MainScreenContent(
            uiState = MainUiState.Success(
                notes = generatePreviewNotes(),
                assistantHint = "Hi, i’m your personal AI powered assistant. I can do a lot of things. Let me show you!",
            ),
            onEvent = {},
        )
    }
}

private fun generatePreviewNotes() = buildImmutableList {
    for (i in 0..5) {
        add(
            MainScreenNote(
                id = i.toString(),
                timestamp = 0,
                tags = persistentListOf(),
                content = persistentListOf(
                    UiNoteContent.Title(
                        id = "1",
                        position = 0,
                        text = "Kotlin is a modern programming language with a " +
                            "lot more syntactic sugar compared to Java, and as such " +
                            "there is equally more black magic",
                    ),
                ),
            ),
        )
    }
}
