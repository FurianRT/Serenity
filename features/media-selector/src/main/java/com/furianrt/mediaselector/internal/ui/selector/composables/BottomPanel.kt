package com.furianrt.mediaselector.internal.ui.selector.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.furianrt.mediaselector.R
import com.furianrt.mediaselector.internal.ui.entities.MediaAlbumItem
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import com.furianrt.uikit.R as uiR

private const val ACTION_PANEL_ANIM_DURATION = 350
private const val ACTION_FAB_ANIM_DURATION = 250

@Composable
internal fun BottomPanel(
    selectedAlbum: MediaAlbumItem?,
    selectedCount: Int,
    visible: Boolean,
    hazeState: HazeState,
    albumsDialogState: List<MediaAlbumItem>?,
    modifier: Modifier = Modifier,
    onSendClick: () -> Unit = {},
    onAlbumsClick: () -> Unit = {},
    onAlbumSelected: (album: MediaAlbumItem) -> Unit = {},
    onAlbumsDismissed: () -> Unit = {},
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = slideIn(
                animationSpec = tween(ACTION_PANEL_ANIM_DURATION),
                initialOffset = { IntOffset(0, it.height) },
            ),
            exit = slideOut(
                animationSpec = tween(ACTION_PANEL_ANIM_DURATION),
                targetOffset = { IntOffset(0, it.height) },
            )
        ) {
            AlbumsButton(
                modifier = Modifier
                    .hazeEffect(
                        state = hazeState,
                        style = HazeDefaults.style(
                            backgroundColor = MaterialTheme.colorScheme.surface,
                            tint = HazeTint(MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)),
                            blurRadius = 12.dp,
                        )
                    )
                    .background(MaterialTheme.colorScheme.outlineVariant)
                    .navigationBarsPadding(),
                hazeState = hazeState,
                selectedAlbum = selectedAlbum,
                onAlbumsClick = onAlbumsClick,
                albumsDialogState = albumsDialogState,
                onAlbumSelected = onAlbumSelected,
                onAlbumsDismissed = onAlbumsDismissed,
            )
        }
        AnimatedVisibility(
            modifier = Modifier
                .wrapContentSize()
                .navigationBarsPadding()
                .align(Alignment.BottomEnd),
            visible = selectedCount > 0,
            enter = fadeIn(animationSpec = tween(ACTION_FAB_ANIM_DURATION)) + scaleIn(
                initialScale = 0.5f,
                animationSpec = tween(ACTION_FAB_ANIM_DURATION),
            ),
            exit = fadeOut(animationSpec = tween(ACTION_FAB_ANIM_DURATION)) + scaleOut(
                animationSpec = tween(ACTION_FAB_ANIM_DURATION),
                targetScale = 0.5f,
            )
        ) {
            ButtonSend(
                modifier = Modifier.padding(end = 12.dp, bottom = 12.dp),
                selectedCount = selectedCount,
                hazeState = hazeState,
                onClick = onSendClick,
            )
        }
    }
}

@Composable
private fun AlbumsButton(
    selectedAlbum: MediaAlbumItem?,
    albumsDialogState: List<MediaAlbumItem>?,
    hazeState: HazeState,
    onAlbumsClick: () -> Unit,
    onAlbumSelected: (album: MediaAlbumItem) -> Unit,
    onAlbumsDismissed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var prevAlbumsDialogState: List<MediaAlbumItem> by remember { mutableStateOf(emptyList()) }

    LaunchedEffect(albumsDialogState) {
        if (albumsDialogState != null) {
            prevAlbumsDialogState = albumsDialogState
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedContent(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = onAlbumsClick)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center,
            targetState = if (selectedAlbum == null || selectedAlbum.id == MediaAlbumItem.ALL_MEDIA_ALBUM_ID) {
                stringResource(R.string.media_selector_albums)
            } else {
                selectedAlbum.name
            },
        ) { targetState ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = targetState,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Icon(
                    modifier = Modifier.padding(top = 2.dp),
                    painter = painterResource(uiR.drawable.ic_small_arrow_up),
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = null,
                )
            }
        }
        AlbumsList(
            albums = prevAlbumsDialogState,
            dropDownHazeState = hazeState,
            expanded = albumsDialogState != null,
            onAlbumClick = onAlbumSelected,
            onDismissRequest = onAlbumsDismissed,
        )
    }
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        BottomPanel(
            selectedAlbum = MediaAlbumItem(
                id = "",
                name = "Albums",
                thumbnail = null,
                mediaCount = 10,
            ),
            albumsDialogState = null,
            selectedCount = 3,
            visible = true,
            hazeState = HazeState(),
        )
    }
}
