package com.furianrt.toolspanel.internal.ui.background.custom

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.furianrt.mediaselector.api.MediaSelectorState
import com.furianrt.notelistui.entities.UiNoteBackgroundImage
import com.furianrt.notelistui.entities.UiNoteTheme
import com.furianrt.toolspanel.R
import com.furianrt.toolspanel.internal.domain.NoteThemesHolder
import com.furianrt.toolspanel.internal.ui.background.container.BackgroundSelectedThemeProvider
import com.furianrt.toolspanel.internal.ui.background.custom.composables.PopUpMenu
import com.furianrt.uikit.anim.rememberOvershootEasing
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.extensions.drawTopInnerShadow
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import com.furianrt.uikit.R as uiR

private const val NOTE_BACKGROUND_TAG = "note_panel_custom_background"
private const val HINT_ITEM_KEY = "hint"
private const val ADD_BUTTON_ITEM_KEY = "add_button"

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun CustomBackgroundPanel(
    noteId: String,
    selectedThemeProvider: BackgroundSelectedThemeProvider,
    onThemeSelected: (theme: UiNoteTheme.Image.Picture?) -> Unit,
    openMediaSelector: (params: MediaSelectorState.Params) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = hiltViewModel<CustomBackgroundViewModel, CustomBackgroundViewModel.Factory>(
        key = NOTE_BACKGROUND_TAG + noteId,
        creationCallback = { it.create(selectedThemeProvider = selectedThemeProvider) },
    )
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(Unit) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collect { effect ->
                when (effect) {
                    is CustomBackgroundEffect.OnThemeSelected -> onThemeSelected(effect.theme)
                    is CustomBackgroundEffect.OpenMediaSelector -> openMediaSelector(effect.params)
                }
            }
    }

    AnimatedContent(
        modifier = modifier,
        targetState = uiState,
        contentKey = { it.javaClass.simpleName },
        transitionSpec = { fadeIn().togetherWith(fadeOut()) },
    ) { targetState ->
        when (targetState) {
            is CustomBackgroundUiState.Loading -> LoadingContent()
            is CustomBackgroundUiState.Empty -> EmptyContent(
                onEvent = viewModel::onEvent,
            )

            is CustomBackgroundUiState.Content -> ListContent(
                state = targetState,
                onEvent = viewModel::onEvent,
            )
        }
    }
}

@Composable
private fun EmptyContent(
    onEvent: (event: CustomBackgroundEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clickableNoRipple {}
            .padding(12.dp)
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer.copy(
                    alpha = MaterialTheme.colorScheme.secondaryContainer.alpha * 0.7f,
                ),
                shape = RoundedCornerShape(8.dp),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(R.drawable.image_custom_background),
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = null,
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.background_panel_custom_background_title),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier.alpha(0.5f),
                text = stringResource(R.string.background_panel_custom_background_hint),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedButton(
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
                contentPadding = PaddingValues(
                    start = 24.dp,
                    end = 32.dp,
                    top = 8.dp,
                    bottom = 8.dp,
                ),
                onClick = { onEvent(CustomBackgroundEvent.OnSelectImageClick) },
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(uiR.drawable.ic_add),
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = null,
                    )
                    Text(
                        text = stringResource(R.string.background_panel_select_image_action),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Composable
private fun ListContent(
    state: CustomBackgroundUiState.Content,
    onEvent: (event: CustomBackgroundEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyGridState(
        initialFirstVisibleItemIndex = state.selectedThemeIndex ?: 0,
    )
    val showShadow by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex != 0 || listState.firstVisibleItemScrollOffset != 0
        }
    }
    val shadowColor = MaterialTheme.colorScheme.surfaceDim
    val hazeState = rememberHazeState()
    val spanCount = 4

    var prevItemCount by remember { mutableIntStateOf(state.themes.size) }
    LaunchedEffect(listState, state.themes.size) {
        if (prevItemCount < state.themes.size) {
            listState.animateScrollToItem(0)
        }
        prevItemCount = state.themes.size
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .hazeSource(hazeState)
            .clickableNoRipple {},
    ) {
        LazyVerticalGrid(
            modifier = Modifier.drawBehind {
                if (showShadow) {
                    drawTopInnerShadow(color = shadowColor)
                }
            },
            state = listState,
            columns = GridCells.Fixed(spanCount),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 16.dp),
        ) {
            itemsIndexed(
                items = state.themes,
                key = { _, item -> item.image.id },
            ) { index, theme ->
                ThemeItem(
                    modifier = Modifier.animateItem(),
                    theme = theme,
                    hazeState = hazeState,
                    isSelected = index == state.selectedThemeIndex,
                    onClick = { onEvent(CustomBackgroundEvent.OnThemeSelected(it)) },
                    onDeleteClick = { onEvent(CustomBackgroundEvent.OnDeleteThemeClick(it)) },
                )
            }
            item(key = ADD_BUTTON_ITEM_KEY) {
                AddBackgroundItem(
                    modifier = Modifier.animateItem(),
                    onClick = { onEvent(CustomBackgroundEvent.OnSelectImageClick) },
                )
            }
            item(
                span = { GridItemSpan(spanCount) },
                key = HINT_ITEM_KEY,
            ) {
                HintItem(
                    modifier = Modifier
                        .padding(start = 4.dp, end = 4.dp, top = 12.dp)
                        .animateItem(),
                )
            }
        }
    }
}

@Composable
private fun ThemeItem(
    theme: UiNoteTheme.Image.Picture,
    isSelected: Boolean,
    hazeState: HazeState,
    onClick: (theme: UiNoteTheme.Image.Picture) -> Unit,
    onDeleteClick: (theme: UiNoteTheme.Image.Picture) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var showDropDown by remember { mutableStateOf(false) }
    val overshootEasing = rememberOvershootEasing(tension = 4.0f)
    val scale by animateFloatAsState(
        targetValue = if (showDropDown) 0.95f else 1f,
        animationSpec = tween(
            durationMillis = 300,
            easing = if (showDropDown) overshootEasing else FastOutSlowInEasing,
        ),
    )
    val placeholderColor = MaterialTheme.colorScheme.background
    var placeholder: Painter by remember { mutableStateOf(ColorPainter(placeholderColor)) }
    val request = remember(theme.image.source) {
        ImageRequest.Builder(context)
            .data(
                when (val source = theme.image.source) {
                    is UiNoteBackgroundImage.Source.Resource -> source.resId
                    is UiNoteBackgroundImage.Source.Storage -> source.uri
                }
            )
            .size(300)
            .build()
    }

    Box(modifier = modifier) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.6f)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .then(
                    when {
                        isSelected -> Modifier.border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            shape = RoundedCornerShape(16.dp),
                        )

                        placeholder !is ColorPainter -> {
                            Modifier.shadow(2.dp, RoundedCornerShape(16.dp))
                        }

                        else -> Modifier
                    }
                )
                .drawWithCache {
                    val path = Path().apply {
                        val padding = if (isSelected) 4.dp.toPx() else 0f
                        addRoundRect(
                            RoundRect(
                                rect = Rect(
                                    left = padding,
                                    right = size.width - padding,
                                    top = padding,
                                    bottom = size.height - padding,
                                ),
                                cornerRadius = CornerRadius(15.dp.toPx())
                            )
                        )
                    }
                    onDrawWithContent {
                        clipPath(path) {
                            this@onDrawWithContent.drawContent()
                        }
                    }
                }
                .combinedClickable(
                    interactionSource = null,
                    indication = null,
                    onClick = { onClick(theme) },
                    onLongClick = { showDropDown = true },
                ),
            model = request,
            placeholder = placeholder,
            onSuccess = { placeholder = it.painter },
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
        PopUpMenu(
            expanded = showDropDown,
            hazeState = hazeState,
            onDeleteClick = { onDeleteClick(theme) },
            onDismissRequest = { showDropDown = false },
        )
    }
}

@Composable
private fun AddBackgroundItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.6f)
            .padding(horizontal = 6.dp, vertical = 8.dp)
            .alpha(0.3f)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface,
                shape = RoundedCornerShape(16.dp),
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(uiR.drawable.ic_add_media_big),
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = null,
        )
    }
}

@Composable
fun HintItem(
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier
            .fillMaxWidth()
            .alpha(0.5f),
        text = stringResource(R.string.background_panel_remove_background_hint),
        style = MaterialTheme.typography.labelMedium,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize())
}

@PreviewWithBackground
@Composable
private fun EmptyPreview() {
    SerenityTheme {
        EmptyContent(
            onEvent = {},
        )
    }
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        ListContent(
            state = CustomBackgroundUiState.Content(
                themes = NoteThemesHolder.pictureThemes(),
                selectedThemeIndex = 0,
            ),
            onEvent = {},
        )
    }
}
