package com.furianrt.toolspanel.internal.ui.background.image

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.furianrt.notelistui.entities.UiNoteBackgroundImage
import com.furianrt.notelistui.entities.UiNoteTheme
import com.furianrt.toolspanel.R
import com.furianrt.toolspanel.internal.domain.NoteThemesHolder
import com.furianrt.toolspanel.internal.ui.background.container.BackgroundSelectedThemeProvider
import com.furianrt.uikit.extensions.applyIf
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.extensions.clickableWithScaleAnim
import com.furianrt.uikit.extensions.drawTopInnerShadow
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground

private const val NOTE_BACKGROUND_TAG = "note_panel_image_background"

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ImageBackgroundContent(
    noteId: String,
    selectedThemeProvider: BackgroundSelectedThemeProvider,
    onThemeSelected: (theme: UiNoteTheme.Image.Picture?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = hiltViewModel<ImageBackgroundViewModel, ImageBackgroundViewModel.Factory>(
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
                    is ImageBackgroundEffect.OnThemeSelected -> onThemeSelected(effect.theme)
                }
            }
    }

    Content(
        modifier = modifier,
        uiState = uiState,
        onEvent = viewModel::onEvent,
    )
}

@Composable
private fun Content(
    uiState: ImageBackgroundUiState,
    onEvent: (event: ImageBackgroundEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyGridState(
        initialFirstVisibleItemIndex = uiState.selectedThemeIndex ?: 0,
    )
    val showShadow by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex != 0 || listState.firstVisibleItemScrollOffset != 0
        }
    }
    val shadowColor = MaterialTheme.colorScheme.surfaceDim
    LazyVerticalGrid(
        modifier = modifier
            .fillMaxSize()
            .clickableNoRipple {}
            .drawBehind {
                if (showShadow) {
                    drawTopInnerShadow(color = shadowColor)
                }
            },
        state = listState,
        columns = GridCells.Fixed(4),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 16.dp),
    ) {
        item {
            ClearItem(
                onClick = { onEvent(ImageBackgroundEvent.OnClearBackgroundClick) },
            )
        }
        itemsIndexed(items = uiState.themes) { index, theme ->
            ThemeItem(
                theme = theme,
                isSelected = index == uiState.selectedThemeIndex,
                onClick = { onEvent(ImageBackgroundEvent.OnThemeSelected(it)) },
            )
        }
    }
}

@Composable
private fun ThemeItem(
    theme: UiNoteTheme.Image.Picture,
    isSelected: Boolean,
    onClick: (theme: UiNoteTheme.Image.Picture) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (theme.image.scaleType) {
        UiNoteBackgroundImage.ScaleType.REPEAT -> {
            val drawable = ImageBitmap.imageResource(theme.image.resId)
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .aspectRatio(0.6f)
                    .then(
                        if (isSelected) {
                            Modifier.border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                shape = RoundedCornerShape(16.dp),
                            )
                        } else {
                            Modifier.shadow(2.dp, RoundedCornerShape(16.dp))
                        }
                    )
                    .drawWithCache {
                        val brush = ShaderBrush(
                            ImageShader(
                                image = drawable,
                                tileModeX = TileMode.Repeated,
                                tileModeY = TileMode.Repeated,
                            )
                        )
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
                        onDrawBehind {
                            clipPath(path) {
                                drawRect(brush)
                            }
                        }
                    }
                    .clickableNoRipple { onClick(theme) },
            )
        }

        UiNoteBackgroundImage.ScaleType.FILL,
        UiNoteBackgroundImage.ScaleType.CENTER,
        UiNoteBackgroundImage.ScaleType.CROP_ALIGN_BOTTOM,
        UiNoteBackgroundImage.ScaleType.CROP_ALIGN_CENTER,
        UiNoteBackgroundImage.ScaleType.CROP_ALIGN_TOP,
            -> {
            AsyncImage(
                modifier = modifier
                    .fillMaxWidth()
                    .aspectRatio(0.6f)
                    .then(
                        if (isSelected) {
                            Modifier.border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                shape = RoundedCornerShape(16.dp),
                            )
                        } else {
                            Modifier.shadow(2.dp, RoundedCornerShape(16.dp))
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
                    .applyIf(theme.image.scaleType == UiNoteBackgroundImage.ScaleType.CENTER) {
                        Modifier.background(theme.color.colorScheme.surface)
                    }
                    .clickableNoRipple { onClick(theme) },
                model = ImageRequest.Builder(LocalContext.current)
                    .data(theme.image.resId)
                    .size(300)
                    .build(),
                contentScale = if (theme.image.scaleType == UiNoteBackgroundImage.ScaleType.CENTER) {
                    ContentScale.Inside
                } else {
                    ContentScale.Crop
                },
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun ClearItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.6f)
            .padding(horizontal = 6.dp, vertical = 16.dp)
            .alpha(0.3f)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface,
                shape = RoundedCornerShape(16.dp),
            )
            .clip(RoundedCornerShape(16.dp))
            .clickableWithScaleAnim(onClick = onClick, maxScale = 1.2f),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            modifier = Modifier.scale(1.1f),
            painter = painterResource(R.drawable.ic_background_clear),
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = null,
        )
    }
}

@Composable
@PreviewWithBackground
private fun Preview() {
    val holder = NoteThemesHolder()
    SerenityTheme {
        Content(
            uiState = ImageBackgroundUiState(
                themes = holder.getPictureThemes(),
                selectedThemeIndex = 0,
            ),
            onEvent = {},
        )
    }
}