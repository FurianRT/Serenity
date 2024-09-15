package com.furianrt.notecontent.composables

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.furianrt.core.buildImmutableList
import com.furianrt.notecontent.entities.UiNoteContent.MediaBlock
import com.furianrt.notecontent.entities.contentHeightDp
import com.furianrt.uikit.extensions.applyIf
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeDefaults.tint
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeChild
import kotlinx.collections.immutable.ImmutableList
import com.furianrt.uikit.R as uiR

private const val CONTENT_TRANSITION_DURATION = 400

@Composable
fun NoteContentMedia(
    block: MediaBlock,
    modifier: Modifier = Modifier,
    clickable: Boolean = false,
    dropDownHazeState: HazeState? = null,
    onClick: (media: MediaBlock.Media) -> Unit = {},
    onShareClick: (media: MediaBlock.Media) -> Unit = {},
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
                modifier = Modifier.sizeIn(maxHeight = targetState.contentHeightDp.dp),
                media = targetState.media[0],
                clickable = clickable,
                dropDownHazeState = dropDownHazeState,
                onClick = onClick,
                onShareClick = onShareClick,
                onRemoveClick = onRemoveClick,
            )

            2 -> RowMediaHolder(
                modifier = Modifier
                    .height(targetState.contentHeightDp.dp)
                    .clip(RoundedCornerShape(8.dp)),
                media = targetState.media,
                clickable = clickable,
                dropDownHazeState = dropDownHazeState,
                onClick = onClick,
                onShareClick = onShareClick,
                onRemoveClick = onRemoveClick,
            )

            3 -> RowMediaHolder(
                modifier = Modifier
                    .height(targetState.contentHeightDp.dp)
                    .clip(RoundedCornerShape(8.dp)),
                media = targetState.media,
                clickable = clickable,
                dropDownHazeState = dropDownHazeState,
                onClick = onClick,
                onShareClick = onShareClick,
                onRemoveClick = onRemoveClick,
            )

            4 -> FourMediaHolder(
                modifier = Modifier.height(targetState.contentHeightDp.dp),
                media = targetState.media,
                clickable = clickable,
                dropDownHazeState = dropDownHazeState,
                onClick = onClick,
                onShareClick = onShareClick,
                onRemoveClick = onRemoveClick,
            )

            else -> ManyMediaHolder(
                modifier = Modifier.height(targetState.contentHeightDp.dp),
                media = targetState.media,
                clickable = clickable,
                dropDownHazeState = dropDownHazeState,
                onClick = onClick,
                onShareClick = onShareClick,
                onRemoveClick = onRemoveClick,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OneMediaHolder(
    media: MediaBlock.Media,
    clickable: Boolean,
    dropDownHazeState: HazeState?,
    onClick: (media: MediaBlock.Media) -> Unit,
    onShareClick: (media: MediaBlock.Media) -> Unit,
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
            onShareClick = onShareClick,
            onRemoveClick = onRemoveClick,
        )
    } else {
        val haptic = LocalHapticFeedback.current
        var showDropDownMenu by remember { mutableStateOf(false) }
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
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
                    .fillMaxWidth()
                    .blur(32.dp),
                media = media,
                cornerRadius = 0.dp,
            )
            MediaItem(
                modifier = Modifier.aspectRatio(
                    ratio = if (media.ratio < 0.65f) media.ratio * 1.4f else media.ratio,
                    matchHeightConstraintsFirst = true,
                ),
                media = media,
                cornerRadius = 0.dp,
                contentScale = ContentScale.Crop,
            )
        }
        if (clickable && dropDownHazeState != null) {
            PopUpMenu(
                expanded = showDropDownMenu,
                hazeState = dropDownHazeState,
                onRemoveClick = { onRemoveClick(media) },
                onShareClick = { onShareClick(media) },
                onDismissRequest = { showDropDownMenu = false },
            )
        }
    }
}

@Composable
private fun RowMediaHolder(
    media: ImmutableList<MediaBlock.Media>,
    clickable: Boolean,
    dropDownHazeState: HazeState?,
    onClick: (media: MediaBlock.Media) -> Unit,
    onShareClick: (media: MediaBlock.Media) -> Unit,
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
                modifier = Modifier.weight(weight),
                media = item,
                dropDownHazeState = dropDownHazeState,
                offscreenImageCount = if (index == subList.lastIndex) {
                    (media.count() - maxImagesCount).coerceAtLeast(0)
                } else {
                    0
                },
                clickable = clickable,
                onClick = onClick,
                onShareClick = onShareClick,
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
    onShareClick: (media: MediaBlock.Media) -> Unit,
    onRemoveClick: (media: MediaBlock.Media) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.clip(RoundedCornerShape(8.dp)),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        MediaItem(
            modifier = Modifier.weight(0.55f),
            media = media[0],
            dropDownHazeState = dropDownHazeState,
            clickable = clickable,
            onClick = onClick,
            onShareClick = onShareClick,
            onRemoveClick = onRemoveClick,
        )
        Column(
            modifier = Modifier.weight(0.45f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            MediaItem(
                modifier = Modifier.weight(0.6f),
                media = media[1],
                dropDownHazeState = dropDownHazeState,
                clickable = clickable,
                onClick = onClick,
                onShareClick = onShareClick,
                onRemoveClick = onRemoveClick,
            )
            RowMediaHolder(
                modifier = Modifier.weight(0.4f),
                media = media.subList(2, 4),
                dropDownHazeState = dropDownHazeState,
                clickable = clickable,
                onClick = onClick,
                onShareClick = onShareClick,
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
    onShareClick: (media: MediaBlock.Media) -> Unit,
    onRemoveClick: (media: MediaBlock.Media) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(8.dp)),
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
            onShareClick = onShareClick,
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
            onShareClick = onShareClick,
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
    onShareClick: (media: MediaBlock.Media) -> Unit = {},
    onRemoveClick: (media: MediaBlock.Media) -> Unit = {},
    contentScale: ContentScale = ContentScale.Crop,
    cornerRadius: Dp = 4.dp,
    offscreenImageCount: Int = 0,
) {
    if (media is MediaBlock.Image) {
        ImageItem(
            modifier = modifier,
            image = media,
            clickable = clickable,
            dropDownHazeState = dropDownHazeState,
            onClick = onClick,
            onShareClick = onShareClick,
            onRemoveClick = onRemoveClick,
            contentScale = contentScale,
            cornerRadius = cornerRadius,
            offscreenImageCount = offscreenImageCount,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ImageItem(
    image: MediaBlock.Image,
    clickable: Boolean,
    dropDownHazeState: HazeState?,
    onClick: (image: MediaBlock.Image) -> Unit,
    onRemoveClick: (image: MediaBlock.Image) -> Unit,
    onShareClick: (image: MediaBlock.Image) -> Unit,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    cornerRadius: Dp = 4.dp,
    offscreenImageCount: Int = 0,
) {
    val context = LocalContext.current
    val request = remember(image.id) {
        ImageRequest.Builder(context)
            .diskCachePolicy(CachePolicy.ENABLED)
            .diskCacheKey(image.id)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCacheKey(image.id)
            .data(image.uri)
            .build()
    }
    val haptic = LocalHapticFeedback.current
    var showDropDownMenu by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        if (offscreenImageCount == 0) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(cornerRadius))
                    .applyIf(clickable) {
                        Modifier.combinedClickable(
                            enabled = clickable,
                            onClick = { onClick(image) },
                            onLongClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                showDropDownMenu = true
                            },
                        )
                    },
                model = request,
                placeholder = ColorPainter(MaterialTheme.colorScheme.tertiary),
                contentDescription = null,
                contentScale = contentScale,
            )
        } else {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(cornerRadius))
                    .applyIf(clickable) {
                        Modifier.combinedClickable(
                            onClick = { onClick(image) },
                            onLongClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                showDropDownMenu = true
                            },
                        )
                    },
                contentAlignment = Alignment.Center,
                propagateMinConstraints = true,
            ) {
                AsyncImage(
                    modifier = Modifier.drawWithContent {
                        drawContent()
                        drawRect(color = Color.Black, alpha = 0.4f)
                    },
                    model = request,
                    placeholder = ColorPainter(MaterialTheme.colorScheme.tertiary),
                    contentDescription = null,
                    contentScale = contentScale,
                )
                Text(
                    modifier = Modifier.wrapContentSize(),
                    text = "+$offscreenImageCount",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.headlineMedium,
                )
            }
        }
        if (clickable && dropDownHazeState != null) {
            PopUpMenu(
                expanded = showDropDownMenu,
                hazeState = dropDownHazeState,
                onRemoveClick = { onRemoveClick(image) },
                onShareClick = { onShareClick(image) },
                onDismissRequest = { showDropDownMenu = false },
            )
        }
    }
}

@Composable
private fun PopUpMenu(
    expanded: Boolean,
    hazeState: HazeState,
    onRemoveClick: () -> Unit,
    onShareClick: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    DropdownMenu(
        modifier = Modifier
            .hazeChild(
                state = hazeState,
                style = HazeDefaults.style(
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    tint = HazeTint.Color(
                        tint(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
                    ),
                    blurRadius = 12.dp,
                ),
            ),
        containerColor = Color.Transparent,
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 0.dp,
        expanded = expanded,
        onDismissRequest = onDismissRequest,
    ) {
        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(uiR.string.action_delete),
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(uiR.drawable.ic_delete),
                    tint = Color.Unspecified,
                    contentDescription = null,
                )
            },
            onClick = {
                onRemoveClick()
                onDismissRequest()
            }
        )
        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(uiR.string.action_share),
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(uiR.drawable.ic_share),
                    tint = Color.Unspecified,
                    contentDescription = null,
                )
            },
            onClick = {
                onShareClick()
                onDismissRequest()
            }
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
                    add(MediaBlock.Image(id = "0", uri = Uri.EMPTY, addedTime = 0, ratio = 1.1f))
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
                    add(MediaBlock.Image(id = "0", uri = Uri.EMPTY, addedTime = 0, ratio = 1.2f))
                    add(MediaBlock.Image(id = "1", uri = Uri.EMPTY, addedTime = 0, ratio = 1f))
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
                    add(MediaBlock.Image(id = "0", uri = Uri.EMPTY, addedTime = 0, ratio = 1.2f))
                    add(MediaBlock.Image(id = "1", uri = Uri.EMPTY, addedTime = 0, ratio = 1f))
                    add(MediaBlock.Image(id = "2", uri = Uri.EMPTY, addedTime = 0, ratio = 0.4f))
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
                    add(MediaBlock.Image(id = "0", uri = Uri.EMPTY, addedTime = 0, ratio = 0.4f))
                    add(MediaBlock.Image(id = "1", uri = Uri.EMPTY, addedTime = 0, ratio = 1.8f))
                    add(MediaBlock.Image(id = "2", uri = Uri.EMPTY, addedTime = 0, ratio = 1f))
                    add(MediaBlock.Image(id = "3", uri = Uri.EMPTY, addedTime = 0, ratio = 1.2f))
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
                    add(MediaBlock.Image(id = "0", uri = Uri.EMPTY, addedTime = 0, ratio = 1.2f))
                    add(MediaBlock.Image(id = "1", uri = Uri.EMPTY, addedTime = 0, ratio = 1.2f))
                    add(MediaBlock.Image(id = "2", uri = Uri.EMPTY, addedTime = 0, ratio = 1.2f))
                    add(MediaBlock.Image(id = "3", uri = Uri.EMPTY, addedTime = 0, ratio = 1.2f))
                    add(MediaBlock.Image(id = "4", uri = Uri.EMPTY, addedTime = 0, ratio = 1.2f))
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
                    add(MediaBlock.Image(id = "0", uri = Uri.EMPTY, addedTime = 0, ratio = 1f))
                    add(MediaBlock.Image(id = "1", uri = Uri.EMPTY, addedTime = 0, ratio = 1.5f))
                    add(MediaBlock.Image(id = "2", uri = Uri.EMPTY, addedTime = 0, ratio = 1f))
                    add(MediaBlock.Image(id = "3", uri = Uri.EMPTY, addedTime = 0, ratio = 1.9f))
                    add(MediaBlock.Image(id = "4", uri = Uri.EMPTY, addedTime = 0, ratio = 1f))
                    add(MediaBlock.Image(id = "5", uri = Uri.EMPTY, addedTime = 0, ratio = 1.2f))
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
                    add(MediaBlock.Image(id = "0", uri = Uri.EMPTY, addedTime = 0, ratio = 1.5f))
                    add(MediaBlock.Image(id = "1", uri = Uri.EMPTY, addedTime = 0, ratio = 1f))
                    add(MediaBlock.Image(id = "2", uri = Uri.EMPTY, addedTime = 0, ratio = 1.2f))
                    add(MediaBlock.Image(id = "3", uri = Uri.EMPTY, addedTime = 0, ratio = 1.2f))
                    add(MediaBlock.Image(id = "4", uri = Uri.EMPTY, addedTime = 0, ratio = 1.2f))
                    add(MediaBlock.Image(id = "5", uri = Uri.EMPTY, addedTime = 0, ratio = 1.2f))
                    add(MediaBlock.Image(id = "6", uri = Uri.EMPTY, addedTime = 0, ratio = 1.2f))
                },
            ),
        )
    }
}
