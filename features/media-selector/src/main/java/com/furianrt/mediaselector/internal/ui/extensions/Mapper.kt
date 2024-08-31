package com.furianrt.mediaselector.internal.ui.extensions

import com.furianrt.core.mapImmutable
import com.furianrt.mediaselector.internal.ui.entities.MediaItem
import com.furianrt.mediaselector.internal.ui.entities.SelectionState
import com.furianrt.storage.api.entities.DeviceMedia
import com.furianrt.uikit.extensions.toTimeString

internal fun DeviceMedia.toMediaItem(
    state: SelectionState = SelectionState.Default,
): MediaItem = when (this) {
    is DeviceMedia.Image -> MediaItem.Image(
        id = id,
        uri = uri,
        ratio = ratio,
        state = state,
    )

    is DeviceMedia.Video -> MediaItem.Video(
        id = id,
        uri = uri,
        ratio = ratio,
        duration = duration.toTimeString(),
        state = state,
    )
}

internal fun List<DeviceMedia>.toMediaItems(
    state: (id: Long) -> SelectionState,
) = mapImmutable { it.toMediaItem(state = state(it.id)) }
