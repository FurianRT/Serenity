package com.furianrt.storage.internal.database.auth.entities

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

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
