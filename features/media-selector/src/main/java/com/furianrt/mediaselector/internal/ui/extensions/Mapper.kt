package com.furianrt.mediaselector.internal.ui.extensions

import com.furianrt.mediaselector.internal.ui.entities.MediaItem
import com.furianrt.storage.api.entities.DeviceMedia
import com.furianrt.uikit.extensions.toTimeString

internal fun DeviceMedia.toMediaItem(isSelected: Boolean = false): MediaItem = when (this) {
    is DeviceMedia.Image -> MediaItem.Image(
        id = id,
        uri = uri,
        title = title,
        isSelected = isSelected,
    )

    is DeviceMedia.Video -> MediaItem.Video(
        id = id,
        uri = uri,
        title = title,
        duration = duration.toTimeString(),
        isSelected = isSelected,
    )
}
