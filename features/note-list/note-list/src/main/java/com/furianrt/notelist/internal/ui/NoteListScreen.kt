package com.furianrt.notelist.internal.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.LottieDynamicProperty
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.model.KeyPath
import com.furianrt.notelist.R
import com.furianrt.notelist.internal.ui.composables.BottomNavigationBar
import com.furianrt.notelist.internal.ui.composables.Toolbar
import com.furianrt.notelist.internal.ui.entities.NoteListScreenNote
import com.furianrt.notelistui.composables.ConfirmNotesDeleteDialog
import com.furianrt.notelistui.composables.NoteListItem
import com.furianrt.notelistui.composables.title.NoteTitleState
import com.furianrt.notelistui.entities.LocationState
import com.furianrt.notelistui.entities.UiNoteContent
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.uikit.components.MovableToolbarScaffold
import com.furianrt.uikit.components.SnackBar
import com.furianrt.uikit.constants.ToolbarConstants
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.extensions.dpToPx
import com.furianrt.uikit.theme.NoteFont
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.DialogIdentifier
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.collectLatest
import com.furianrt.uikit.R as uiR

private const val SHOW_SCROLL_TO_TOP_MIN_ITEM_INDEX = 3
private const val EMPTY_STATE_ANIM_DURATION = 2500

@Composable
internal fun NoteListScreen(
    openNoteViewScreen: (noteId: String, identifier: DialogIdentifier) -> Unit,
    openNoteCreateScreen: (identifier: DialogIdentifier) -> Unit,
    openNoteSearchScreen: () -> Unit,
    openSettingsScreen: () -> Unit,
) {
    val viewModel: NoteListViewModel = hiltViewModel()
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val hazeState = rememberHazeState()
    val snackBarHostState = remember { SnackbarHostState() }

    val screenState = rememberMainState()

    var showDeleteConfirmDialogState: Int? by remember { mutableStateOf(null) }

    val openNoteViewScreenState by rememberUpdatedState(openNoteViewScreen)
    val openNoteCreateScreenState by rememberUpdatedState(openNoteCreateScreen)
    val openNoteSearchScreenState by rememberUpdatedState(openNoteSearchScreen)
    val openSettingsScreenState by rememberUpdatedState(openSettingsScreen)

    LaunchedEffect(Unit) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->
                when (effect) {
                    is NoteListEffect.ScrollToTop -> screenState.scrollToTop()
                    is NoteListEffect.OpenSettingsScreen -> openSettingsScreenState()
                    is NoteListEffect.OpenNoteSearchScreen -> openNoteSearchScreenState()
                    is NoteListEffect.OpenNoteViewScreen -> {
                        openNoteViewScreenState(effect.noteId, effect.identifier)
                    }

                    is NoteListEffect.OpenNoteCreateScreen -> {
                        openNoteCreateScreenState(effect.identifier)
                    }

                    is NoteListEffect.ShowConfirmNoteDeleteDialog -> {
                        showDeleteConfirmDialogState = effect.notesCount
                    }

                    is NoteListEffect.ShowSyncProgressMessage -> {
                        snackBarHostState.currentSnackbarData?.dismiss()
                        snackBarHostState.showSnackbar(
                            message = effect.message,
                            duration = SnackbarDuration.Short,
                        )
                    }
                }
            }
    }

    MainScreenContent(
        modifier = Modifier.hazeSource(hazeState),
        uiState = uiState,
        snackBarHostState = snackBarHostState,
        screenState = screenState,
        onEvent = viewModel::onEvent,
    )

    showDeleteConfirmDialogState?.let { notesCount ->
        ConfirmNotesDeleteDialog(
            notesCount = notesCount,
            hazeState = hazeState,
            onConfirmClick = { viewModel.onEvent(NoteListEvent.OnConfirmDeleteSelectedNotesClick) },
            onDismissRequest = { showDeleteConfirmDialogState = null },
        )
    }
}

@Composable
private fun MainScreenContent(
    uiState: NoteListUiState,
    snackBarHostState: SnackbarHostState,
    onEvent: (event: NoteListEvent) -> Unit,
    modifier: Modifier = Modifier,
    screenState: NoteListScreenState = rememberMainState(),
) {
    val hazeState = rememberHazeState()

    val needToShowScrollUpButton by remember {
        derivedStateOf {
            screenState.listState.firstVisibleItemIndex > SHOW_SCROLL_TO_TOP_MIN_ITEM_INDEX
        }
    }

    val successState = uiState as? NoteListUiState.Success

    LaunchedEffect(uiState.enableSelection) {
        if (uiState.enableSelection) {
            screenState.toolbarState.expand()
        }
    }

    BackHandler(enabled = uiState.enableSelection) {
        onEvent(NoteListEvent.OnCloseSelectionClick)
    }

    MovableToolbarScaffold(
        modifier = modifier.background(MaterialTheme.colorScheme.surface),
        listState = screenState.listState
            .takeIf { uiState is NoteListUiState.Success } ?: rememberLazyListState(),
        state = screenState.toolbarState,
        enabled = !uiState.enableSelection,
        toolbar = {
            Toolbar(
                notesCount = successState?.notes?.count() ?: 0,
                selectedNotesCount = successState?.selectedNotesCount ?: 0,
                onSettingsClick = { onEvent(NoteListEvent.OnSettingsClick) },
                onSearchClick = { onEvent(NoteListEvent.OnSearchClick) },
                onDeleteClick = { onEvent(NoteListEvent.OnDeleteSelectedNotesClick) },
                onCloseSelectionClick = { onEvent(NoteListEvent.OnCloseSelectionClick) },
            )
        },
    ) { topPadding ->
        when (uiState) {
            is NoteListUiState.Loading -> LoadingContent(
                modifier = Modifier.hazeSource(hazeState),
            )

            is NoteListUiState.Empty -> EmptyContent(
                modifier = Modifier.hazeSource(hazeState),
                onEvent = onEvent,
            )

            is NoteListUiState.Success -> SuccessContent(
                modifier = Modifier.hazeSource(hazeState),
                uiState = uiState,
                screenState = screenState,
                toolbarPadding = topPadding,
                onEvent = onEvent,
                hazeState = hazeState,
            )
        }

        BottomNavigationBar(
            modifier = Modifier.align(Alignment.BottomEnd),
            hazeState = hazeState,
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 24.dp),
            onScrollToTopClick = { onEvent(NoteListEvent.OnScrollToTopClick) },
            needToHideNavigation = {
                uiState.hasNotes &&
                        screenState.listState.lastScrolledForward &&
                        screenState.listState.canScrollForward
            },
            needToShowScrollUpButton = { uiState.hasNotes && needToShowScrollUpButton },
            onAddNoteClick = { onEvent(NoteListEvent.OnAddNoteClick) },
        )

        SnackbarHost(
            modifier = Modifier.align(Alignment.BottomCenter),
            hostState = snackBarHostState,
            snackbar = { data ->
                SnackBar(
                    title = data.visuals.message,
                    icon = painterResource(uiR.drawable.ic_cloud_sync),
                    tonalColor = MaterialTheme.colorScheme.tertiaryContainer,
                )
            },
        )
    }
}

@Composable
private fun SuccessContent(
    uiState: NoteListUiState.Success,
    screenState: NoteListScreenState,
    toolbarPadding: Dp,
    hazeState: HazeState,
    onEvent: (event: NoteListEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    LaunchedEffect(uiState.scrollToPosition) {
        if (uiState.scrollToPosition != null) {
            screenState.scrollToPosition(
                position = uiState.scrollToPosition,
                toolbarHeight = density.run { ToolbarConstants.bigToolbarHeight.toPx().toInt() },
            )
            onEvent(NoteListEvent.OnScrolledToItem)
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = screenState.listState,
        contentPadding = PaddingValues(
            start = 8.dp,
            end = 8.dp,
            top = toolbarPadding + 8.dp,
            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 16.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(count = uiState.notes.count(), key = { uiState.notes[it].id }) { index ->
            val note = uiState.notes[index]
            NoteListItem(
                modifier = Modifier
                    .animateItem()
                    .animateContentSize(),
                content = note.content,
                tags = note.tags,
                date = note.date.getTitle(),
                fontFamily = note.fontFamily,
                moodId = note.moodId,
                locationState = note.locationState,
                isPinned = note.isPinned,
                isSelected = note.isSelected,
                hazeState = hazeState,
                onClick = { onEvent(NoteListEvent.OnNoteClick(note)) },
                onLongClick = { onEvent(NoteListEvent.OnNoteLongClick(note)) },
            )
        }
    }
}

@Composable
private fun EmptyContent(
    modifier: Modifier = Modifier,
    onEvent: (event: NoteListEvent) -> Unit,
) {
    val (initialOffsetPx, initialAlpha) = if (LocalInspectionMode.current) {
        0f to 1f
    } else {
        500.dp.dpToPx() to 0f
    }

    var startAnimation by rememberSaveable { mutableStateOf(false) }
    val transition = updateTransition(targetState = startAnimation)

    val contentAlpha by transition.animateFloat(
        transitionSpec = { tween(EMPTY_STATE_ANIM_DURATION) },
        targetValueByState = { if (it) 1f else initialAlpha },
    )
    val contentTranslationY by transition.animateFloat(
        transitionSpec = { tween(EMPTY_STATE_ANIM_DURATION) },
        targetValueByState = { if (it) 0f else initialOffsetPx },
    )

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.anim_empty_diary),
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        speed = 0.5f,
    )
    val dynamicProperties = rememberLottieDynamicProperties(
        LottieDynamicProperty(
            property = LottieProperty.COLOR,
            value = MaterialTheme.colorScheme.onSurface.toArgb(),
            keyPath = KeyPath("**"),
        ),
        LottieDynamicProperty(
            property = LottieProperty.STROKE_COLOR,
            value = MaterialTheme.colorScheme.onSurface.toArgb(),
            keyPath = KeyPath("**"),
        ),
    )

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer {
                alpha = contentAlpha
                translationY = contentTranslationY
            },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .clickableNoRipple { onEvent(NoteListEvent.OnAddNoteClick) }
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LottieAnimation(
                modifier = Modifier
                    .height(70.dp)
                    .scale(2.4f),
                composition = composition,
                progress = { progress },
                dynamicProperties = dynamicProperties,
            )
            Spacer(Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.notes_list_empty_list_title),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                modifier = Modifier.alpha(0.5f),
                text = stringResource(R.string.notes_list_empty_list_body),
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize())
}

@Composable
private fun NoteListScreenNote.Date.getTitle() = when (this) {
    is NoteListScreenNote.Date.Today -> stringResource(uiR.string.today_title)
    is NoteListScreenNote.Date.Yesterday -> stringResource(uiR.string.yesterday_title)
    is NoteListScreenNote.Date.Other -> text
}

@Preview
@Composable
private fun SuccessPreview() {
    SerenityTheme {
        MainScreenContent(
            uiState = NoteListUiState.Success(
                notes = generatePreviewNotes(withSelected = false),
                scrollToPosition = null,
                selectedNotesCount = 0,
                font = NoteFont.NotoSans,
            ),
            snackBarHostState = SnackbarHostState(),
            onEvent = {},
        )
    }
}

@Preview
@Composable
private fun EmptyPreview() {
    SerenityTheme {
        MainScreenContent(
            uiState = NoteListUiState.Empty,
            snackBarHostState = SnackbarHostState(),
            onEvent = {},
        )
    }
}

@Preview
@Composable
private fun SuccessWithSelectedPreview() {
    SerenityTheme {
        MainScreenContent(
            uiState = NoteListUiState.Success(
                notes = generatePreviewNotes(withSelected = true),
                scrollToPosition = null,
                selectedNotesCount = 3,
                font = NoteFont.NotoSans,
            ),
            snackBarHostState = SnackbarHostState(),
            onEvent = {},
        )
    }
}

private fun generatePreviewNotes(withSelected: Boolean) = buildList {
    repeat(5) { index ->
        add(
            NoteListScreenNote(
                id = index.toString(),
                date = NoteListScreenNote.Date.Other("19.06.2023"),
                tags = emptyList(),
                fontFamily = null,
                isPinned = false,
                moodId = null,
                locationState = LocationState.Empty,
                isSelected = withSelected && index % 2 == 0,
                content = listOf(
                    UiNoteContent.Title(
                        id = index.toString(),
                        state = NoteTitleState(
                            fontFamily = UiNoteFontFamily.NotoSans,
                            initialText = AnnotatedString(
                                text = "Kotlin is a modern programming language with a " +
                                        "lot more syntactic sugar compared to Java, and as such " +
                                        "there is equally more black magic",
                            ),
                        ),
                    ),
                ),
            ),
        )
    }
}
