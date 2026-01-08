package com.furianrt.storage.internal.device.mappers

import com.furianrt.domain.entities.DeviceAlbum
import com.furianrt.domain.entities.DeviceMedia

internal fun DeviceMedia.toAlbumThumbnail(): DeviceAlbum.Thumbnail = when (this) {
    is DeviceMedia.Image -> DeviceAlbum.Thumbnail.Image(
        id = id,
        uri = uri,
    )

    is DeviceMedia.Video -> DeviceAlbum.Thumbnail.Video(
        id = id,
        uri = uri,
        duration = duration,
    )
}