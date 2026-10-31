package com.furianrt.apptheme.internal.ui.extensions

import com.furianrt.domain.entities.AppCustomBackground
import com.furianrt.mediaselector.api.MediaResult
import java.time.ZonedDateTime
import java.util.UUID

internal fun MediaResult.Media.Image.toAppCustomBackground(
    primaryColor: Int,
    accentColor: Int,
    isLight: Boolean,
) = AppCustomBackground(
    id = UUID.randomUUID().toString(),
    name = name,
    uri = uri,
    primaryColor = primaryColor,
    accentColor = accentColor,
    isLight = isLight,
    addedDate = ZonedDateTime.now(),
)
