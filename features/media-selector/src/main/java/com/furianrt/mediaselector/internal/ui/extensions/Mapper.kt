package com.furianrt.mediaselector.internal.ui.extensions

import com.furianrt.common.MediaResult
import com.furianrt.core.mapImmutable
import com.furianrt.domain.entities.DeviceMedia
import com.furianrt.mediaselector.internal.ui.entities.MediaItem
import com.furianrt.mediaselector.internal.ui.entities.SelectionState

internal fun DeviceMedia.toMediaItem(
    state: SelectionState = SelectionState.Default,
): MediaItem = when (this) {
    is DeviceMedia.Image -> MediaItem.Image(
        id = id,
        name = name,
        uri = uri,
        ratio = ratio,
        state = state,
    )

    is DeviceMedia.Video -> MediaItem.Video(
        id = id,
        name = name,
        uri = uri,
        ratio = ratio,
        duration = duration,
        state = state,
    )
}

internal fun List<DeviceMedia>.toMediaItems(
    state: (id: Long) -> SelectionState,
) = mapImmutable { it.toMediaItem(state = state(it.id)) }

internal fun List<MediaItem>.toMediaSelectorResult() = MediaResult(
    media = map(MediaItem::toResultMedia),
)

private fun MediaItem.toResultMedia(): MediaResult.Media = when (this) {
    is MediaItem.Image -> MediaResult.Media.Image(
        name = name,
        uri = uri,
        ratio = ratio,
    )

    is MediaItem.Video -> MediaResult.Media.Video(
        name = name,
        uri = uri,
        ratio = ratio,
        duration = duration,
    )
}
