package com.furianrt.toolspanel.internal.ui.stickers.custom

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
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
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.furianrt.domain.repositories.StickersRepository
import com.furianrt.mediaselector.api.MediaSelectorState
import com.furianrt.toolspanel.R
import com.furianrt.toolspanel.api.entities.Sticker
import com.furianrt.toolspanel.internal.domain.StickersHolder
import com.furianrt.toolspanel.internal.ui.stickers.custom.composables.PopUpMenu
import com.furianrt.uikit.anim.rememberOvershootEasing
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.extensions.dashedRoundedRectBorder
import com.furianrt.uikit.extensions.drawTopInnerShadow
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.collectLatest
import com.furianrt.uikit.R as uiR

private const val NOTE_STICKERS_TAG = "note_panel_custom_stickers"
private const val ADD_BUTTON_ITEM_KEY = "add_button"
private const val HINT_ITEM_KEY = "hint"

@Composable
internal fun CustomStickersPanel(
    noteId: String,
    onStickerSelected: (sticker: Sticker) -> Unit,
    openMediaSelector: (params: MediaSelectorState.Params) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: CustomStickersViewModel = hiltViewModel(
        key = NOTE_STICKERS_TAG + noteId,
    )
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val onStickerSelectedState by rememberUpdatedState(onStickerSelected)
    val openMediaSelectorState by rememberUpdatedState(openMediaSelector)

    LaunchedEffect(Unit) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->
                when (effect) {
                    is CustomStickersEffect.SelectSticker -> {
                        onStickerSelectedState(effect.sticker)
                    }

                    is CustomStickersEffect.OpenMediaSelector -> {
                        openMediaSelectorState(effect.params)
                    }
                }
            }
    }

    AnimatedContent(
        modifier = modifier
            .fillMaxSize()
            .clickableNoRipple {},
        targetState = uiState,
        contentKey = { it.javaClass.simpleName },
        transitionSpec = { fadeIn().togetherWith(fadeOut()) },
    ) { targetState ->
        when (targetState) {
            is CustomStickersUiState.Loading -> LoadingContent()
            is CustomStickersUiState.Empty -> EmptyContent(onEvent = viewModel::onEvent)
            is CustomStickersUiState.Content -> ListContent(
                uiState = targetState,
                onEvent = viewModel::onEvent,
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
private fun EmptyContent(
    onEvent: (event: CustomStickersEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer.copy(
                    alpha = MaterialTheme.colorScheme.secondaryContainer.alpha * 0.7f,
                ),
                shape = RoundedCornerShape(8.dp),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .dashedRoundedRectBorder(
                        color = MaterialTheme.colorScheme.onSurface,
                        width = 1.6.dp,
                        cornerRadius = 8.dp,
                        interval = 8.dp,
                    )
                    .padding(vertical = 16.dp, horizontal = 12.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.image_custom_background),
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = null,
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.stickers_panel_custom_stickers_title),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier.alpha(0.5f),
                text = stringResource(R.string.stickers_panel_custom_stickers_hint),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(24.dp))
            SelectImageButton(
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                ),
                onClick = { onEvent(CustomStickersEvent.OnSelectImageClick) },
            )
        }
    }
}

@Composable
private fun SelectImageButton(
    border: BorderStroke,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        border = border,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        contentPadding = PaddingValues(
            start = 24.dp,
            end = 32.dp,
            top = 8.dp,
            bottom = 8.dp,
        ),
        onClick = onClick,
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

@Composable
private fun ListContent(
    uiState: CustomStickersUiState.Content,
    onEvent: (event: CustomStickersEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyStaggeredGridState()
    val hazeState = rememberHazeState()
    val showShadow by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex != 0 || listState.firstVisibleItemScrollOffset != 0
        }
    }
    val shadowColor = MaterialTheme.colorScheme.surfaceDim

    var prevItemCount by remember { mutableIntStateOf(uiState.stickers.size) }
    LaunchedEffect(listState, uiState.stickers.size) {
        if (prevItemCount < uiState.stickers.size) {
            listState.animateScrollToItem(0)
        }
        prevItemCount = uiState.stickers.size
    }

    LazyVerticalStaggeredGrid(
        modifier = modifier
            .fillMaxSize()
            .hazeSource(hazeState)
            .drawBehind {
                if (showShadow) {
                    drawTopInnerShadow(color = shadowColor)
                }
            },
        state = listState,
        columns = StaggeredGridCells.Adaptive(72.dp),
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp),
    ) {
        items(
            count = uiState.stickers.size,
            key = { uiState.stickers[it].id },
        ) { index ->
            StickerItem(
                modifier = Modifier.animateItem(),
                sticker = uiState.stickers[index],
                hazeState = hazeState,
                onClick = { onEvent(CustomStickersEvent.OnStickerSelected(it)) },
                onDeleteClick = { onEvent(CustomStickersEvent.OnDeleteStickerClick(it)) },
            )
        }
        item(
            span = StaggeredGridItemSpan.FullLine,
            key = ADD_BUTTON_ITEM_KEY,
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .animateItem(),
                contentAlignment = Alignment.Center,
            ) {
                SelectImageButton(
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    ),
                    onClick = { onEvent(CustomStickersEvent.OnSelectImageClick) },
                )
            }
        }
        item(
            span = StaggeredGridItemSpan.FullLine,
            key = HINT_ITEM_KEY,
        ) {
            HintItem(
                modifier = Modifier
                    .padding(start = 4.dp, end = 4.dp, top = 8.dp)
                    .animateItem(),
            )
        }
    }
}

@Composable
private fun StickerItem(
    sticker: Sticker,
    hazeState: HazeState,
    onClick: (sticker: Sticker) -> Unit,
    onDeleteClick: (sticker: Sticker) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
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
    val colorPlaceholder = remember { ColorPainter(placeholderColor) }
    var placeholder: Painter by remember { mutableStateOf(colorPlaceholder) }
    val request = remember(sticker.iconData) {
        ImageRequest.Builder(context)
            .data(sticker.iconData)
            .diskCachePolicy(CachePolicy.DISABLED)
            .size(300)
            .build()
    }

    Box(
        modifier = modifier.wrapContentSize(),
        contentAlignment = Alignment.Center,
    ) {
        AsyncImage(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .then(
                    if (sticker.ratio != null) {
                        Modifier.aspectRatio(
                            sticker.ratio.coerceIn(
                                minimumValue = 0.5f,
                                maximumValue = 1.5f,
                            )
                        )
                    } else {
                        Modifier
                    },
                )
                .combinedClickable(
                    onClick = { onClick(sticker) },
                    onLongClick = {
                        focusManager.clearFocus()
                        showDropDown = true
                    },
                ),
            error = colorPlaceholder,
            model = request,
            placeholder = placeholder,
            onSuccess = { placeholder = it.painter },
            contentDescription = null,
        )
        PopUpMenu(
            expanded = showDropDown,
            hazeState = hazeState,
            onDeleteClick = { onDeleteClick(sticker) },
            onDismissRequest = { showDropDown = false },
        )
    }
}

@Composable
private fun HintItem(
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier
            .fillMaxWidth()
            .alpha(0.5f),
        text = stringResource(R.string.stickers_panel_remove_sticker_hint),
        style = MaterialTheme.typography.labelMedium,
        textAlign = TextAlign.Center
    )
}

@Composable
@PreviewWithBackground
private fun EmptyPreview() {
    SerenityTheme {
        EmptyContent(
            onEvent = {},
        )
    }
}

@Composable
@PreviewWithBackground
private fun ListPreview() {
    SerenityTheme {
        ListContent(
            uiState = CustomStickersUiState.Content(
                stickers = StickersHolder(StickersRepository.mock())
                    .getStickersPacks().first().stickers,
            ),
            onEvent = {},
        )
    }
}
