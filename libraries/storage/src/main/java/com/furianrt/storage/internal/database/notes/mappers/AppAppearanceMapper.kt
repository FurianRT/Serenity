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

internal fun AppCustomBackground.toEntry(isSaved: Boolean) = EntryAppCustomBackground(
    id = id,
    name = name,
    uri = uri,
    addedDate = addedDate,
    isSaved = isSaved,
    primaryColor = primaryColor,
    accentColor = accentColor,
    isLight = isLight,
)

internal fun EntryAppCustomBackground.toDomain() = AppCustomBackground(
    id = id,
    name = name,
    uri = uri,
    addedDate = addedDate,
    primaryColor = primaryColor,
    accentColor = accentColor,
    isLight = isLight,
)
