package com.furianrt.toolspanel.internal.ui.background.pattern

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.furianrt.notelistui.entities.UiNoteBackground
import com.furianrt.notelistui.entities.UiNoteBackgroundImage
import com.furianrt.notelistui.entities.UiNoteTheme
import com.furianrt.toolspanel.R
import com.furianrt.toolspanel.internal.domain.NoteThemesHolder
import com.furianrt.toolspanel.internal.ui.background.container.BackgroundSelectedThemeProvider
import com.furianrt.uikit.entities.UiThemeColor
import com.furianrt.uikit.extensions.applyIf
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.extensions.clickableWithScaleAnim
import com.furianrt.uikit.extensions.dpToPx
import com.furianrt.uikit.theme.SerenityTheme

private const val NOTE_BACKGROUND_TAG = "note_panel_pattern_background"

@Composable
internal fun PatternBackgroundContent(
    noteId: String,
    selectedThemeProvider: BackgroundSelectedThemeProvider,
    onThemeSelected: (theme: UiNoteTheme.Image.Pattern?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = hiltViewModel<PatternBackgroundViewModel, PatternBackgroundViewModel.Factory>(
        key = NOTE_BACKGROUND_TAG + noteId,
        creationCallback = { it.create(selectedThemeProvider) },
    )
    val uiState = viewModel.state.collectAsStateWithLifecycle().value
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(Unit) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collect { effect ->
                when (effect) {
                    is PatternBackgroundEffect.SendThemeSelected -> onThemeSelected(effect.theme)
                }
            }
    }

    when (uiState) {
        is PatternBackgroundUiState.Success -> SuccessContent(
            modifier = modifier,
            uiState = uiState,
            onEvent = viewModel::onEvent,
        )

        is PatternBackgroundUiState.Loading -> Box(
            modifier = modifier.clickableNoRipple {},
        )
    }
}

@Composable
private fun SuccessContent(
    uiState: PatternBackgroundUiState.Success,
    onEvent: (event: PatternBackgroundEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val imagesListState = rememberLazyListState(
        initialFirstVisibleItemIndex = uiState.selectedImageIndex ?: 0,
        initialFirstVisibleItemScrollOffset = -45.dp.dpToPx().toInt(),
    )
    val colorListState = rememberLazyListState(
        initialFirstVisibleItemIndex = uiState.selectedColorIndex ?: 0,
        initialFirstVisibleItemScrollOffset = -28.dp.dpToPx().toInt(),
    )
    Column(
        modifier = modifier
            .padding(vertical = 16.dp)
            .clickableNoRipple {},
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .weight(weight = 1f, fill = false)
                .heightIn(max = 140.dp)
                .systemGestureExclusion(),
            state = imagesListState,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            contentPadding = PaddingValues(horizontal = 8.dp),
        ) {
            item {
                ClearItem(
                    onClick = { onEvent(PatternBackgroundEvent.OnClearClick) },
                )
            }
            itemsIndexed(
                items = uiState.images,
            ) { index, image ->
                ImageItem(
                    image = image,
                    color = uiState.selectedColorIndex?.let { uiState.colors[it] },
                    appThemeColor = uiState.appTheme,
                    isSelected = index == uiState.selectedImageIndex,
                    hasSelectedImage = uiState.selectedImageIndex != null,
                    onClick = { onEvent(PatternBackgroundEvent.OnImageSelected(it)) },
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .alpha(0.7f),
            text = stringResource(R.string.background_panel_theme_title),
            style = MaterialTheme.typography.labelSmall,
        )
        Spacer(Modifier.height(8.dp))
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .systemGestureExclusion(),
            state = colorListState,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(horizontal = 8.dp),
        ) {
            itemsIndexed(
                items = uiState.colors,
            ) { index, color ->
                ColorItem(
                    color = color,
                    isSelected = index == uiState.selectedColorIndex,
                    onClick = { onEvent(PatternBackgroundEvent.OnColorSelected(it)) },
                )
            }
        }
    }
}

@Composable
private fun ImageItem(
    image: UiNoteBackgroundImage,
    color: UiNoteBackground?,
    appThemeColor: UiThemeColor,
    isSelected: Boolean,
    hasSelectedImage: Boolean,
    onClick: (image: UiNoteBackgroundImage) -> Unit,
    modifier: Modifier = Modifier,
) {
    val drawable = ImageBitmap.imageResource(image.resId)
    val animatedSurface = if (!hasSelectedImage && color != null) {
        animateColorAsState(
            targetValue = color.colorScheme.surface,
            animationSpec = tween(250),
        ).value
    } else {
        val surface = color?.colorScheme?.surface
        val noteTheme = MaterialTheme.colorScheme.surface
        surface ?: if (appThemeColor.surface == noteTheme) {
            noteTheme
        } else {
            appThemeColor.surface
        }
    }

    val animatedOnSurface = if (!hasSelectedImage && color != null) {
        animateColorAsState(
            targetValue = color.colorScheme.onSurface,
            animationSpec = tween(250),
        ).value
    } else {
        val onSurface = color?.colorScheme?.onSurface
        val noteTheme = MaterialTheme.colorScheme.onSurface
        onSurface ?: if (appThemeColor.onSurface == noteTheme) {
            noteTheme
        } else {
            appThemeColor.onSurface
        }
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(90.dp)
            .applyIf(isSelected) {
                Modifier.border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(16.dp),
                )
            }
            .applyIf(!isSelected) {
                Modifier.shadow(2.dp, RoundedCornerShape(16.dp))
            }
            .drawWithCache {
                val brush = ShaderBrush(
                    ImageShader(
                        image = drawable,
                        tileModeX = TileMode.Repeated,
                        tileModeY = TileMode.Repeated,
                    )
                )
                val colorFilter = ColorFilter.tint(
                    color = animatedOnSurface,
                    blendMode = BlendMode.SrcIn
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
                        drawRect(animatedSurface)
                        drawRect(brush = brush, alpha = 0.4f, colorFilter = colorFilter)
                    }
                }
            }
            .clickableNoRipple { onClick(image) },
    )
}

@Composable
private fun ClearItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxHeight(0.85f)
            .width(70.dp)
            .alpha(0.3f)
            .scale(0.9f)
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
            modifier = Modifier.scale(1.2f),
            painter = painterResource(R.drawable.ic_background_clear),
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = null,
        )
    }
}

@Composable
private fun ColorItem(
    color: UiNoteBackground,
    isSelected: Boolean,
    onClick: (color: UiNoteBackground) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(56.dp)
            .applyIf(isSelected) {
                Modifier
                    .border(
                        width = 1.dp,
                        color = color.colorScheme.surfaceContainer,
                        shape = RoundedCornerShape(16.dp),
                    )
                    .padding(3.dp)
            }
            .clickableNoRipple { onClick(color) },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = color.colorScheme.surface,
                    shape = RoundedCornerShape(if (isSelected) 14.dp else 16.dp),
                )
        )
    }
}

@Preview(heightDp = 300, showBackground = true, backgroundColor = 0xFF474972)
@Composable
private fun Preview() {
    val themesHolder = NoteThemesHolder()
    SerenityTheme {
        SuccessContent(
            uiState = PatternBackgroundUiState.Success(
                images = themesHolder.getPatternImages(),
                colors = themesHolder.getSolidThemes().map { it.color },
                selectedColorIndex = 1,
                selectedImageIndex = 0,
                appTheme = UiThemeColor.fromId(null),
            ),
            onEvent = {},
        )
    }
}
