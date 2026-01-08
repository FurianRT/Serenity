package com.furianrt.mediaselector.internal.ui.extensions

import com.furianrt.domain.entities.DeviceAlbum
import com.furianrt.domain.entities.DeviceMedia
import com.furianrt.mediaselector.api.MediaResult
import com.furianrt.mediaselector.internal.ui.entities.MediaAlbumItem
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
        album = MediaItem.Album(
            id = albumId.toString(),
            name = albumName,
        ),
    )

    is DeviceMedia.Video -> MediaItem.Video(
        id = id,
        name = name,
        uri = uri,
        ratio = ratio,
        duration = duration,
        state = state,
        album = MediaItem.Album(
            id = albumId.toString(),
            name = albumName,
        ),
    )
}

internal fun List<DeviceMedia>.toMediaItems(
    state: (id: Long) -> SelectionState,
) = map { it.toMediaItem(state = state(it.id)) }

internal fun List<MediaItem>.toMediaSelectorResult() = MediaResult(
    media = map(MediaItem::toResultMedia),
)

internal fun DeviceMedia.toMediaAlbumItem(): MediaAlbumItem.Thumbnail = when (this) {
    is DeviceMedia.Image -> MediaAlbumItem.Thumbnail.Image(
        id = id.toString(),
        uri = uri,
    )

    is DeviceMedia.Video -> MediaAlbumItem.Thumbnail.Video(
        id = id.toString(),
        uri = uri,
        duration = duration,
    )
}

internal fun DeviceAlbum.toMediaAlbumItem() = MediaAlbumItem(
    id = id.toString(),
    name = name,
    thumbnail = thumbnail.toThumbnailItem(),
    mediaCount = mediaCount,
)

internal fun DeviceAlbum.Thumbnail.toThumbnailItem(): MediaAlbumItem.Thumbnail = when (this) {
    is DeviceAlbum.Thumbnail.Image -> MediaAlbumItem.Thumbnail.Image(
        id = id.toString(),
        uri = uri,
    )

    is DeviceAlbum.Thumbnail.Video -> MediaAlbumItem.Thumbnail.Video(
        id = id.toString(),
        uri = uri,
        duration = duration,
    )
}

internal fun DeviceMedia.toThumbnailItem(): MediaAlbumItem.Thumbnail = when (this) {
    is DeviceMedia.Image -> MediaAlbumItem.Thumbnail.Image(
        id = id.toString(),
        uri = uri,
    )

    is DeviceMedia.Video -> MediaAlbumItem.Thumbnail.Video(
        id = id.toString(),
        uri = uri,
        duration = duration,
    )
}

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
