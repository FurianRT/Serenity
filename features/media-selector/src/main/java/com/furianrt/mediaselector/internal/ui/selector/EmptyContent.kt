package com.furianrt.mediaselector.internal.ui.selector

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.furianrt.mediaselector.R
import com.furianrt.mediaselector.internal.ui.entities.MediaAlbumItem
import com.furianrt.mediaselector.internal.ui.selector.MediaSelectorEvent.OnPartialAccessMessageClick
import com.furianrt.mediaselector.internal.ui.selector.composables.BottomPanel
import com.furianrt.mediaselector.internal.ui.selector.composables.PermissionsMessage
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

// TODO Сделать пустое состояние покрасивше
@Composable
internal fun EmptyContent(
    uiState: MediaSelectorUiState.Empty,
    onEvent: (event: MediaSelectorEvent) -> Unit,
    albumsDialogState: List<MediaAlbumItem>?,
    modifier: Modifier = Modifier,
) {
    val hazeState = remember { HazeState() }
    Column(
        modifier = modifier
            .fillMaxSize()
            .hazeSource(hazeState),
    ) {
        if (uiState.showPartialAccessMessage) {
            PermissionsMessage(
                modifier = Modifier.padding(horizontal = 4.dp),
                onClick = { onEvent(OnPartialAccessMessageClick) },
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.media_selector_empty_list_title),
                style = MaterialTheme.typography.titleLarge,
            )
        }
        BottomPanel(
            selectedAlbum = uiState.selectedAlbum,
            selectedCount = 0,
            visible = true,
            albumsDialogState = albumsDialogState,
            hazeState = hazeState,
            onSendClick = { onEvent(MediaSelectorEvent.OnSendClick) },
            onAlbumsClick = { onEvent(MediaSelectorEvent.OnAlbumsClick) },
            onAlbumSelected = { onEvent(MediaSelectorEvent.OnAlbumSelected(it)) },
            onAlbumsDismissed = { onEvent(MediaSelectorEvent.OnAlbumsDismissed) },
        )
    }
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        EmptyContent(
            uiState = MediaSelectorUiState.Empty(
                selectedAlbum = null,
                showPartialAccessMessage = true,
            ),
            albumsDialogState = null,
            onEvent = {},
        )
    }
}
