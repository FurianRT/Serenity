package com.furianrt.storage.internal.database.auth.mappers

import com.furianrt.domain.entities.BackupProfile
import com.furianrt.storage.internal.database.auth.entities.EntryBackupProfile

internal fun EntryBackupProfile.toBackupProfile() = BackupProfile(
    email = email,
)

internal fun BackupProfile.toEntryBackupProfile() = EntryBackupProfile(
    email = email,
)