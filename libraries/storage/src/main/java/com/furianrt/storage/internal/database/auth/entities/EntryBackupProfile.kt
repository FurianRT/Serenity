package com.furianrt.storage.internal.database.auth.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = EntryBackupProfile.TABLE_NAME)
internal class EntryBackupProfile(
    @PrimaryKey
    @ColumnInfo(name = FIELD_EMAIL)
    val email: String,
) {
    companion object {
        const val TABLE_NAME = "BackupProfiles"
        const val FIELD_EMAIL = "email"
    }
}
