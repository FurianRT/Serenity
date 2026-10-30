package com.furianrt.storage.internal.database.notes.mappers

import com.furianrt.domain.entities.AppCustomBackground
import com.furianrt.storage.internal.database.notes.entities.EntryAppCustomBackground

internal fun EntryAppCustomBackground.toAppCustomBackground() = AppCustomBackground(
    id = id,
    name = name,
    uri = uri,
    primaryColor = primaryColor,
    accentColor = accentColor,
    isLight = isLight,
    addedDate = addedDate,
)