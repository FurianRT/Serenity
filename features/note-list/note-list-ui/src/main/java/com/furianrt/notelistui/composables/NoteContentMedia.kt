package com.furianrt.notelistui.composables

import android.net.Uri
import android.view.animation.OvershootInterpolator
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Precision
import coil3.video.VideoFrameDecoder
import com.furianrt.core.buildImmutableList
import com.furianrt.notelistui.R
import com.furianrt.notelistui.entities.UiNoteContent.MediaBlock
import com.furianrt.notelistui.entities.contentHeight
import com.furianrt.uikit.components.DurationBadge
import com.furianrt.uikit.components.MenuItem
import com.furianrt.uikit.extensions.applyIf
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.LocalAuth
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import com.furianrt.uikit.R as uiR

private const val CONTENT_TRANSITION_DURATION = 400
private const val LONG_CLICK_SCALE = 0.98f
private const val LONG_CLICK_SCALE_DURATION = 350
private const val CROSS_FADE_DURATION = 150
private const val BLURRED_MEDIA_KEY_POSTFIX = "blurred"

@Composable
fun NoteContentMedia(
    block: MediaBlock,
    modifier: Modifier = Modifier,
    clickable: Boolean = false,
    dropDownHazeState: HazeState? = null,
    onClick: (media: MediaBlock.Media) -> Unit = {},
    onSortingClick: () -> Unit = {},
    onRemoveClick: (media: MediaBlock.Media) -> Unit = {},
) {
    AnimatedContent(
        modifier = modifier,
        targetState = block,
        contentKey = { targetState -> targetState.media.count() },
        transitionSpec = {
            fadeIn(animationSpec = tween(CONTENT_TRANSITION_DURATION))
                .togetherWith(fadeOut(animationSpec = tween(CONTENT_TRANSITION_DURATION)))
        },
        label = "MediaContentTransitionAnim",
    ) { targetState ->
        when (targetState.media.count()) {
            1 -> OneMediaHolder(
                modifier = Modifier.height(targetState.contentHeight),
                media = targetState.media[0],
                clickable = clickable,
                dropDownHazeState = dropDownHazeState,
                onClick = onClick,
                onSortingClick = onSortingClick,
                onRemoveClick = onRemoveClick,
            )

            2 -> RowMediaHolder(
                modifier = Modifier
                    .height(targetState.contentHeight)
                    .clip(RoundedCornerShape(8.dp)),
                media = targetState.media,
                clickable = clickable,
                dropDownHazeState = dropDownHazeState,
                onClick = onClick,
                onSortingClick = onSortingClick,
                onRemoveClick = onRemoveClick,
            )

            3 -> RowMediaHolder(
                modifier = Modifier
                    .height(targetState.contentHeight)
                    .clip(RoundedCornerShape(8.dp)),
                media = targetState.media,
                clickable = clickable,
                dropDownHazeState = dropDownHazeState,
                onClick = onClick,
                onSortingClick = onSortingClick,
                onRemoveClick = onRemoveClick,
            )

            4 -> FourMediaHolder(
                modifier = Modifier.height(targetState.contentHeight),
                media = targetState.media,
                clickable = clickable,
                dropDownHazeState = dropDownHazeState,
                onClick = onClick,
                onSortingClick = onSortingClick,
                onRemoveClick = onRemoveClick,
            )

            else -> ManyMediaHolder(
                modifier = Modifier.height(targetState.contentHeight),
                media = targetState.media,
                clickable = clickable,
                dropDownHazeState = dropDownHazeState,
                onClick = onClick,
                onSortingClick = onSortingClick,
                onRemoveClick = onRemoveClick,
            )
        }
    }
}

@Composable
private fun OneMediaHolder(
    media: MediaBlock.Media,
    clickable: Boolean,
    dropDownHazeState: HazeState?,
    onClick: (media: MediaBlock.Media) -> Unit,
    onSortingClick: () -> Unit,
    onRemoveClick: (media: MediaBlock.Media) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (media.ratio >= 1.4f) {
        MediaItem(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)),
            cornerRadius = 0.dp,
            media = media,
            dropDownHazeState = dropDownHazeState,
            clickable = clickable,
            onClick = onClick,
            onSortingClick = onSortingClick,
            onRemoveClick = onRemoveClick,
        )
    } else {
        val interpolator = remember { OvershootInterpolator(5.0f) }
        val haptic = LocalHapticFeedback.current
        var showDropDownMenu by remember { mutableStateOf(false) }
        val longClickScale by animateFloatAsState(
            targetValue = if (showDropDownMenu) LONG_CLICK_SCALE else 1f,
            animationSpec = tween(
                durationMillis = LONG_CLICK_SCALE_DURATION,
                easing = if (showDropDownMenu) {
                    Easing { interpolator.getInterpolation(it) }
                } else {
                    FastOutSlowInEasing
                }
            ),
        )
        Box(
            modifier = modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = longClickScale
                    scaleY = longClickScale
                }
                .clip(RoundedCornerShape(8.dp))
                .applyIf(clickable) {
                    Modifier.combinedClickable(
                        onClick = { onClick(media) },
                        onLongClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            showDropDownMenu = true
                        },
                    )
                },
            contentAlignment = Alignment.Center,
        ) {
            MediaItem(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
                    .blur(32.dp),
                media = media,
                cornerRadius = 0.dp,
                cacheExtraKey = BLURRED_MEDIA_KEY_POSTFIX,
            )
            Box {
                MediaItem(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(
                            ratio = if (media.ratio < 0.65f) media.ratio * 1.4f else media.ratio,
                            matchHeightConstraintsFirst = true,
                        ),
                    media = media,
                    cornerRadius = 0.dp,
                    contentScale = ContentScale.Crop,
                )
                if (clickable && dropDownHazeState != null) {
                    PopUpMenu(
                        expanded = showDropDownMenu,
                        hazeState = dropDownHazeState,
                        onRemoveClick = { onRemoveClick(media) },
                        onSortingClick = onSortingClick,
                        onDismissRequest = { showDropDownMenu = false },
                    )
                }
            }
        }
    }
}

@Composable
private fun RowMediaHolder(
    media: ImmutableList<MediaBlock.Media>,
    clickable: Boolean,
    dropDownHazeState: HazeState?,
    onClick: (media: MediaBlock.Media) -> Unit,
    onSortingClick: () -> Unit,
    onRemoveClick: (media: MediaBlock.Media) -> Unit,
    modifier: Modifier = Modifier,
) {
    val maxImagesCount = 4
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        val subList = media.take(maxImagesCount)
        val ratioSum = subList.sumOf { it.ratio.toDouble() }.toFloat()
        subList.forEachIndexed { index, item ->
            val weight = if (media.count() >= maxImagesCount) 1f else item.ratio / ratioSum
            MediaItem(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(weight),
                media = item,
                dropDownHazeState = dropDownHazeState,
                offscreenImageCount = if (index == subList.lastIndex) {
                    (media.count() - maxImagesCount).coerceAtLeast(0)
                } else {
                    0
                },
                clickable = clickable,
                onClick = onClick,
                onSortingClick = onSortingClick,
                onRemoveClick = onRemoveClick,
            )
        }
    }
}

@Composable
private fun FourMediaHolder(
    media: ImmutableList<MediaBlock.Media>,
    clickable: Boolean,
    dropDownHazeState: HazeState?,
    onClick: (media: MediaBlock.Media) -> Unit,
    onSortingClick: () -> Unit,
    onRemoveClick: (media: MediaBlock.Media) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.clip(RoundedCornerShape(8.dp)),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        MediaItem(
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.55f),
            media = media[0],
            dropDownHazeState = dropDownHazeState,
            clickable = clickable,
            onClick = onClick,
            onSortingClick = onSortingClick,
            onRemoveClick = onRemoveClick,
        )
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.45f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            MediaItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.6f),
                media = media[1],
                dropDownHazeState = dropDownHazeState,
                clickable = clickable,
                onClick = onClick,
                onSortingClick = onSortingClick,
                onRemoveClick = onRemoveClick,
            )
            RowMediaHolder(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.4f),
                media = media.subList(2, 4),
                dropDownHazeState = dropDownHazeState,
                clickable = clickable,
                onClick = onClick,
                onSortingClick = onSortingClick,
                onRemoveClick = onRemoveClick,
            )
        }
    }
}

@Composable
private fun ManyMediaHolder(
    media: ImmutableList<MediaBlock.Media>,
    clickable: Boolean,
    dropDownHazeState: HazeState?,
    onClick: (media: MediaBlock.Media) -> Unit,
    onSortingClick: () -> Unit,
    onRemoveClick: (media: MediaBlock.Media) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(8.dp)),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        RowMediaHolder(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.63f),
            media = media.subList(0, 2),
            dropDownHazeState = dropDownHazeState,
            clickable = clickable,
            onClick = onClick,
            onSortingClick = onSortingClick,
            onRemoveClick = onRemoveClick,
        )
        RowMediaHolder(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.37f),
            media = media.subList(2, media.count()),
            dropDownHazeState = dropDownHazeState,
            clickable = clickable,
            onClick = onClick,
            onSortingClick = onSortingClick,
            onRemoveClick = onRemoveClick,
        )
    }
}

@Composable
private fun MediaItem(
    media: MediaBlock.Media,
    modifier: Modifier = Modifier,
    clickable: Boolean = false,
    dropDownHazeState: HazeState? = null,
    onClick: (media: MediaBlock.Media) -> Unit = {},
    onSortingClick: () -> Unit = {},
    onRemoveClick: (media: MediaBlock.Media) -> Unit = {},
    contentScale: ContentScale = ContentScale.Crop,
    cornerRadius: Dp = 4.dp,
    offscreenImageCount: Int = 0,
    cacheExtraKey: String? = null,
) {
    val haptic = LocalHapticFeedback.current
    var showDropDownMenu by remember { mutableStateOf(false) }
    val dimColor = MaterialTheme.colorScheme.scrim
    val interpolator = remember { OvershootInterpolator(5.0f) }
    val longClickScale by animateFloatAsState(
        targetValue = if (showDropDownMenu) LONG_CLICK_SCALE else 1f,
        animationSpec = tween(
            durationMillis = LONG_CLICK_SCALE_DURATION,
            easing = if (showDropDownMenu) {
                Easing { interpolator.getInterpolation(it) }
            } else {
                FastOutSlowInEasing
            }
        ),
    )
    val dimModifier = Modifier.drawWithContent {
        drawContent()
        if (offscreenImageCount > 0) {
            drawRect(color = dimColor)
        }
    }
    val scaleModifier = Modifier.graphicsLayer {
        scaleY = longClickScale
        scaleX = longClickScale
    }
    val clickModifier = Modifier.combinedClickable(
        onClick = { onClick(media) },
        onLongClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            showDropDownMenu = true
        },
    )
    Box(
        modifier = modifier.clip(RoundedCornerShape(cornerRadius)),
        contentAlignment = Alignment.Center,
        propagateMinConstraints = true,
    ) {
        when (media) {
            is MediaBlock.Image -> ImageItem(
                modifier = dimModifier
                    .then(scaleModifier)
                    .clip(RoundedCornerShape(cornerRadius))
                    .applyIf(clickable) { clickModifier },
                image = media,
                contentScale = contentScale,
                cacheExtraKey = cacheExtraKey,
            )

            is MediaBlock.Video -> VideoItem(
                modifier = dimModifier
                    .then(scaleModifier)
                    .clip(RoundedCornerShape(cornerRadius))
                    .applyIf(clickable) { clickModifier },
                video = media,
                contentScale = contentScale,
                cacheExtraKey = cacheExtraKey,
            )
        }
        if (offscreenImageCount > 0) {
            Text(
                modifier = Modifier.wrapContentSize(),
                text = "+$offscreenImageCount",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                style = MaterialTheme.typography.headlineMedium,
            )
        }
        if (clickable && dropDownHazeState != null) {
            PopUpMenu(
                expanded = showDropDownMenu,
                hazeState = dropDownHazeState,
                onRemoveClick = { onRemoveClick(media) },
                onSortingClick = onSortingClick,
                onDismissRequest = { showDropDownMenu = false },
            )
        }
    }
}

@Composable
private fun ImageItem(
    image: MediaBlock.Image,
    cacheExtraKey: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
) {
    val context = LocalContext.current
    val request = remember(image.id, cacheExtraKey) {
        ImageRequest.Builder(context)
            .diskCachePolicy(CachePolicy.DISABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCacheKey(image.id)
            .apply {
                if (cacheExtraKey != null) memoryCacheKeyExtra(image.id, cacheExtraKey)
            }
            .precision(Precision.INEXACT)
            .crossfade(CROSS_FADE_DURATION)
            .data(image.uri)
            .build()
    }

    AsyncImage(
        modifier = modifier.fillMaxSize(),
        model = request,
        placeholder = ColorPainter(MaterialTheme.colorScheme.tertiary),
        error = ColorPainter(MaterialTheme.colorScheme.tertiary),
        contentScale = contentScale,
        contentDescription = null,
    )
}

@Composable
private fun VideoItem(
    video: MediaBlock.Video,
    cacheExtraKey: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
) {
    var badgeSize by remember { mutableStateOf(IntSize.Zero) }
    var isVisible by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val request = remember(video.id, cacheExtraKey) {
        ImageRequest.Builder(context)
            .diskCachePolicy(CachePolicy.DISABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCacheKey(video.id)
            .apply {
                if (cacheExtraKey != null) memoryCacheKeyExtra(video.id, cacheExtraKey)
            }
            .precision(Precision.INEXACT)
            .crossfade(CROSS_FADE_DURATION)
            .data(video.uri)
            .decoderFactory { result, options, _ -> VideoFrameDecoder(result.source, options) }
            .build()
    }
    Box(modifier = modifier.onSizeChanged { isVisible = it.width >= badgeSize.width }) {
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = request,
            placeholder = ColorPainter(MaterialTheme.colorScheme.tertiary),
            error = ColorPainter(MaterialTheme.colorScheme.tertiary),
            contentScale = contentScale,
            contentDescription = null,
        )
        if (isVisible) {
            DurationBadge(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 4.dp, bottom = 4.dp)
                    .onSizeChanged { badgeSize = it },
                duration = video.duration,
            )
        }
    }
}

@Composable
private fun PopUpMenu(
    expanded: Boolean,
    hazeState: HazeState,
    onRemoveClick: () -> Unit,
    onSortingClick: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val auth = LocalAuth.current

    LifecycleStartEffect(Unit) {
        scope.launch {
            if (!auth.isAuthorized()) {
                onDismissRequest()
            }
        }
        onStopOrDispose {}
    }
    DropdownMenu(
        modifier = Modifier
            .hazeEffect(
                state = hazeState,
                style = HazeDefaults.style(
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    blurRadius = 12.dp,
                )
            )
            .background(MaterialTheme.colorScheme.tertiaryContainer),
        containerColor = Color.Transparent,
        offset = DpOffset(x = 8.dp, y = -(8).dp),
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 0.dp,
        expanded = expanded,
        onDismissRequest = onDismissRequest,
    ) {
        MenuItem(
            icon = painterResource(R.drawable.ic_change_order),
            text = stringResource(uiR.string.action_change_sorting),
            onClick = {
                onSortingClick()
                onDismissRequest()
            },
        )
        MenuItem(
            icon = painterResource(uiR.drawable.ic_delete),
            text = stringResource(uiR.string.action_delete),
            onClick = {
                onRemoveClick()
                onDismissRequest()
            },
        )
    }
}

@PreviewWithBackground
@Composable
private fun NoteContentOneMediaPreview() {
    SerenityTheme {
        NoteContentMedia(
            block = MediaBlock(
                id = "1",
                media = buildImmutableList {
                    add(
                        MediaBlock.Image(
                            id = "0",
                            name = "0",
                            uri = Uri.EMPTY,
                            addedDate = ZonedDateTime.now(),
                            ratio = 1.1f,
                        )
                    )
                },
            ),
        )
    }
}

@PreviewWithBackground
@Composable
private fun NoteContentTwoMediaPreview() {
    SerenityTheme {
        NoteContentMedia(
            block = MediaBlock(
                id = "1",
                media = buildImmutableList {
                    add(
                        MediaBlock.Image(
                            id = "0",
                            name = "0",
                            uri = Uri.EMPTY,
                            addedDate = ZonedDateTime.now(),
                            ratio = 1.2f,
                        )
                    )
                    add(
                        MediaBlock.Image(
                            id = "1",
                            name = "1",
                            uri = Uri.EMPTY,
                            addedDate = ZonedDateTime.now(),
                            ratio = 1f,
                        )
                    )
                },
            ),
        )
    }
}

@PreviewWithBackground
@Composable
private fun NoteContentThreeMediaPreview() {
    SerenityTheme {
        NoteContentMedia(
            block = MediaBlock(
                id = "1",
                media = buildImmutableList {
                    add(
                        MediaBlock.Image(
                            id = "0",
                            name = "0",
                            uri = Uri.EMPTY,
                            addedDate = ZonedDateTime.now(),
                            ratio = 1.2f,
                        )
                    )
                    add(
                        MediaBlock.Image(
                            id = "1",
                            name = "1",
                            uri = Uri.EMPTY,
                            addedDate = ZonedDateTime.now(),
                            ratio = 1f,
                        )
                    )
                    add(
                        MediaBlock.Image(
                            id = "2",
                            name = "2",
                            uri = Uri.EMPTY,
                            addedDate = ZonedDateTime.now(),
                            ratio = 0.4f,
                        )
                    )
                },
            ),
        )
    }
}

@PreviewWithBackground
@Composable
private fun NoteContentFourMediaPreview() {
    SerenityTheme {
        NoteContentMedia(
            block = MediaBlock(
                id = "1",
                media = buildImmutableList {
                    add(
                        MediaBlock.Image(
                            id = "0",
                            name = "0",
                            uri = Uri.EMPTY,
                            addedDate = ZonedDateTime.now(),
                            ratio = 0.4f,
                        )
                    )
                    add(
                        MediaBlock.Image(
                            id = "1",
                            name = "1",
                            uri = Uri.EMPTY,
                            addedDate = ZonedDateTime.now(),
                            ratio = 1.8f,
                        )
                    )
                    add(
                        MediaBlock.Image(
                            id = "2",
                            name = "2",
                            uri = Uri.EMPTY,
                            addedDate = ZonedDateTime.now(),
                            ratio = 1f,
                        )
                    )
                    add(
                        MediaBlock.Image(
                            id = "3",
                            name = "3",
                            uri = Uri.EMPTY,
                            addedDate = ZonedDateTime.now(),
                            ratio = 1.2f,
                        )
                    )
                },
            ),
        )
    }
}

@PreviewWithBackground
@Composable
private fun NoteContentFiveMediaPreview() {
    SerenityTheme {
        NoteContentMedia(
            block = MediaBlock(
                id = "1",
                media = buildImmutableList {
                    add(
                        MediaBlock.Image(
                            id = "0",
                            name = "0",
                            uri = Uri.EMPTY,
                            addedDate = ZonedDateTime.now(),
                            ratio = 1.2f,
                        )
                    )
                    add(
                        MediaBlock.Image(
                            id = "1",
                            name = "1",
                            uri = Uri.EMPTY,
                            addedDate = ZonedDateTime.now(),
                            ratio = 1.2f,
                        )
                    )
                    add(
                        MediaBlock.Image(
                            id = "2",
                            name = "2",
                            uri = Uri.EMPTY,
                            addedDate = ZonedDateTime.now(),
                            ratio = 1.2f,
                        )
                    )
                    add(
                        MediaBlock.Image(
                            id = "3",
                            name = "3",
                            uri = Uri.EMPTY,
                            addedDate = ZonedDateTime.now(),
                            ratio = 1.2f,
                        )
                    )
                    add(
                        MediaBlock.Image(
                            id = "4",
                            name = "4",
                            uri = Uri.EMPTY,
                            addedDate = ZonedDateTime.now(),
                            ratio = 1.2f,
                        )
                    )
                },
            ),
        )
    }
}

@PreviewWithBackground
@Composable
private fun NoteContentSixMediaPreview() {
    SerenityTheme {
        NoteContentMedia(
            block = MediaBlock(
                id = "1",
                media = buildImmutableList {
                    add(
                        MediaBlock.Image(
                            id = "0",
                            name = "0",
                            uri = Uri.EMPTY,
                            addedDate = ZonedDateTime.now(),
                            ratio = 1f,
                        )
                    )
                    add(
                        MediaBlock.Image(
                            id = "1",
                            name = "1",
                            uri = Uri.EMPTY,
                            addedDate = ZonedDateTime.now(),
                            ratio = 1.5f,
                        )
                    )
                    add(
                        MediaBlock.Image(
                            id = "2",
                            name = "2",
                            uri = Uri.EMPTY,
                            addedDate = ZonedDateTime.now(),
                            ratio = 1f,
                        )
                    )
                    add(
                        MediaBlock.Image(
                            id = "3",
                            name = "3",
                            uri = Uri.EMPTY,
                            addedDate = ZonedDateTime.now(),
                            ratio = 1.9f,
                        )
                    )
                    add(
                        MediaBlock.Image(
                            id = "4",
                            name = "4",
                            uri = Uri.EMPTY,
                            addedDate = ZonedDateTime.now(),
                            ratio = 1f,
                        )
                    )
                    add(
                        MediaBlock.Image(
                            id = "5",
                            name = "5",
                            uri = Uri.EMPTY,
                            addedDate = ZonedDateTime.now(),
                            ratio = 1.2f,
                        )
                    )
                },
            ),
        )
    }
}

@PreviewWithBackground
@Composable
private fun NoteContentSevenMediaPreview() {
    SerenityTheme {
        NoteContentMedia(
            block = MediaBlock(
                id = "1",
                media = buildImmutableList {
                    add(
                        MediaBlock.Image(
                            id = "0",
                            name = "0",
                            uri = Uri.EMPTY,
                            addedDate = ZonedDateTime.now(),
                            ratio = 1.5f,
                        )
                    )
                    add(
                        MediaBlock.Image(
                            id = "1",
                            name = "1",
                            uri = Uri.EMPTY,
                            addedDate = ZonedDateTime.now(),
                            ratio = 1f,
                        )
                    )
                    add(
                        MediaBlock.Image(
                            id = "2",
                            name = "2",
                            uri = Uri.EMPTY,
                            addedDate = ZonedDateTime.now(),
                            ratio = 1.2f,
                        )
                    )
                    add(
                        MediaBlock.Image(
                            id = "3",
                            name = "3",
                            uri = Uri.EMPTY,
                            addedDate = ZonedDateTime.now(),
                            ratio = 1.2f,
                        )
                    )
                    add(
                        MediaBlock.Image(
                            id = "4",
                            name = "4",
                            uri = Uri.EMPTY,
                            addedDate = ZonedDateTime.now(),
                            ratio = 1.2f,
                        )
                    )
                    add(
                        MediaBlock.Image(
                            id = "5",
                            name = "5",
                            uri = Uri.EMPTY,
                            addedDate = ZonedDateTime.now(),
                            ratio = 1.2f,
                        )
                    )
                    add(
                        MediaBlock.Image(
                            id = "6",
                            name = "6",
                            uri = Uri.EMPTY,
                            addedDate = ZonedDateTime.now(),
                            ratio = 1.2f,
                        )
                    )
                },
            ),
        )
    }
}
