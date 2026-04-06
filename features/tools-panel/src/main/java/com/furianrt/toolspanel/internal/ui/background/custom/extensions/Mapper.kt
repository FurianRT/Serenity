package com.furianrt.toolspanel.internal.ui.background.custom.extensions

import androidx.compose.ui.graphics.Color
import com.furianrt.domain.entities.NoteCustomBackground
import com.furianrt.mediaselector.api.MediaResult
import com.furianrt.notelistui.entities.UiNoteBackground
import com.furianrt.notelistui.entities.UiNoteBackgroundImage
import com.furianrt.notelistui.entities.UiNoteTheme
import com.furianrt.uikit.theme.getDefaultDarkColorScheme
import com.furianrt.uikit.theme.getDefaultLightColorScheme
import java.time.ZonedDateTime
import java.util.UUID

internal fun NoteCustomBackground.toUiNoteTheme() = UiNoteTheme.Image.Picture(
    color = UiNoteBackground(
        id = id,
        isLight = isLight,
        colorScheme = if (isLight) {
            getDefaultLightColorScheme(
                surface = Color(primaryColor),
                primaryContainer = Color(accentColor),
            )
        } else {
            getDefaultDarkColorScheme(
                surface = Color(primaryColor),
                primaryContainer = Color(accentColor),
            )
        },
    ),
    image = UiNoteBackgroundImage(
        id = id,
        source = UiNoteBackgroundImage.Source.Storage(uri),
        scaleType = UiNoteBackgroundImage.ScaleType.CROP_ALIGN_CENTER,
    )
)

internal fun MediaResult.Media.Image.toNoteCustomBackground(
    primaryColor: Int,
    accentColor: Int,
    isLight: Boolean,
) = NoteCustomBackground(
    id = UUID.randomUUID().toString(),
    name = name,
    uri = uri,
    primaryColor = primaryColor,
    accentColor = accentColor,
    isLight = isLight,
    addedDate = ZonedDateTime.now(),
)
