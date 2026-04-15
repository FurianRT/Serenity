package com.furianrt.notepage.internal.ui.extensions

import androidx.annotation.DrawableRes
import androidx.compose.ui.unit.dp
import com.furianrt.domain.entities.LocalNote
import com.furianrt.domain.entities.NoteFontFamily
import com.furianrt.domain.entities.NoteTextAlignment
import com.furianrt.mediaselector.api.MediaResult
import com.furianrt.notelistui.entities.UiNoteBackground
import com.furianrt.notelistui.entities.UiNoteBackgroundImage
import com.furianrt.notelistui.entities.UiNoteContent
import com.furianrt.notelistui.entities.UiNoteContent.MediaBlock
import com.furianrt.notelistui.entities.UiNoteTheme
import com.furianrt.notelistui.extensions.toRegularUiNoteTag
import com.furianrt.notelistui.extensions.toUiNoteContent
import com.furianrt.notelistui.extensions.toUiNoteFontColor
import com.furianrt.notelistui.extensions.toUiNoteFontFamily
import com.furianrt.notepage.internal.ui.page.entities.NoteItem
import com.furianrt.notepage.internal.ui.stickers.StickerState
import com.furianrt.notepage.internal.ui.stickers.entities.StickerItem
import com.furianrt.toolspanel.api.VoiceRecord
import com.furianrt.uikit.entities.UiThemeColor
import com.furianrt.uikit.entities.UiThemeImage
import com.furianrt.uikit.entities.colorScheme
import java.time.ZonedDateTime
import java.util.UUID

private const val APP_THEME_POSTFIX = "app_theme"

internal fun LocalNote.toNoteItem(
    appFont: NoteFontFamily,
    stickerIconProvider: (typeId: String) -> Int?,
    theme: UiNoteTheme?,
) = NoteItem(
    id = id,
    tags = tags.map(LocalNote.Tag::toRegularUiNoteTag),
    stickers = stickers.mapNotNull { it.toStickerItem(stickerIconProvider(it.typeId)) },
    content = content.map { it.toUiNoteContent((fontFamily ?: appFont).toUiNoteFontFamily()) },
    fontColor = fontColor?.toUiNoteFontColor(),
    fontFamily = fontFamily?.toUiNoteFontFamily(),
    fontSize = fontSize,
    lineHeightMultiplier = lineHeightMultiplier ?: 1f,
    textAlignment = textAlignment ?: NoteTextAlignment.START,
    theme = theme,
    moodId = moodId,
    location = location,
)

internal fun MediaResult.toMediaBlock() = MediaBlock(
    id = UUID.randomUUID().toString(),
    media = media.map(MediaResult.Media::toMediaBlockMedia),
)

internal fun VoiceRecord.toUiVoice() = UiNoteContent.Voice(
    id = id,
    uri = uri,
    duration = duration.toLong(),
    volume = volume,
    progressState = UiNoteContent.Voice.ProgressState(),
)

internal fun StickerItem.toLocalNoteSticker() = LocalNote.Sticker(
    id = id,
    typeId = typeId,
    scale = state.scale,
    rotation = state.rotation,
    isFlipped = state.isFlipped,
    biasX = state.biasX,
    dpOffsetY = state.dpOffsetY.value,
    editTime = state.editTime,
)

internal fun UiThemeColor.toNoteTheme(): UiNoteTheme = if (image != null) {
    UiNoteTheme.Image.Picture(
        isAppTheme = true,
        color = UiNoteBackground(
            id = id + APP_THEME_POSTFIX,
            isLight = isLight,
            colorScheme = colorScheme,
        ),
        image = UiNoteBackgroundImage(
            id = id + APP_THEME_POSTFIX,
            source = UiNoteBackgroundImage.Source.Resource(image!!.resId),
            scaleType = image!!.scaleType.toNoteThemeScaleType(),
        ),
    )
} else {
    UiNoteTheme.Solid(
        isAppTheme = true,
        color = UiNoteBackground(
            id = id + APP_THEME_POSTFIX,
            isLight = isLight,
            colorScheme = colorScheme,
        ),
    )
}

private fun UiThemeImage.ScaleType.toNoteThemeScaleType() = when (this) {
    UiThemeImage.ScaleType.REPEAT -> UiNoteBackgroundImage.ScaleType.REPEAT
    UiThemeImage.ScaleType.FILL -> UiNoteBackgroundImage.ScaleType.FILL
    UiThemeImage.ScaleType.CENTER -> UiNoteBackgroundImage.ScaleType.CENTER
    UiThemeImage.ScaleType.CROP_ALIGN_BOTTOM -> UiNoteBackgroundImage.ScaleType.CROP_ALIGN_BOTTOM
    UiThemeImage.ScaleType.CROP_ALIGN_CENTER -> UiNoteBackgroundImage.ScaleType.CROP_ALIGN_CENTER
    UiThemeImage.ScaleType.CROP_ALIGN_TOP -> UiNoteBackgroundImage.ScaleType.CROP_ALIGN_TOP
}

private fun LocalNote.Sticker.toStickerItem(
    @DrawableRes icon: Int?,
): StickerItem? = if (icon == null) {
    null
} else {
    StickerItem(
        id = id,
        typeId = typeId,
        icon = icon,
        state = StickerState(
            initialScale = scale,
            initialRotation = rotation,
            initialIsFlipped = isFlipped,
            initialBiasX = biasX,
            initialDpOffsetY = dpOffsetY.dp,
            initialEditTime = editTime,
            initialIsEditing = false,
        ),
    )
}

private fun MediaResult.Media.toMediaBlockMedia(): MediaBlock.Media = when (this) {
    is MediaResult.Media.Image -> MediaBlock.Image(
        id = UUID.randomUUID().toString(),
        name = name,
        uri = uri,
        ratio = ratio,
        addedDate = ZonedDateTime.now(),
    )

    is MediaResult.Media.Video -> MediaBlock.Video(
        id = UUID.randomUUID().toString(),
        name = name,
        uri = uri,
        ratio = ratio,
        addedDate = ZonedDateTime.now(),
        duration = duration,
    )
}
