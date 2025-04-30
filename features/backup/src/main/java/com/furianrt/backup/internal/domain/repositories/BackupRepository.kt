package com.furianrt.backup.internal.domain.repositories

import android.content.Intent
import com.furianrt.backup.internal.domain.entities.BackupPeriod
import com.furianrt.backup.internal.domain.entities.RemoteFile
import com.furianrt.domain.entities.LocalNote
import com.google.android.gms.auth.api.identity.AuthorizationResult
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.time.ZonedDateTime

internal interface BackupRepository {
    suspend fun authorize(): Result<AuthorizationResult>
    fun getAuthorizationResult(intent: Intent?): AuthorizationResult?
    suspend fun signOut(): Result<Unit>

    fun getAccessToken(): Flow<String?>
    suspend fun updateAccessToken(token: String)

    fun isAutoBackupEnabled(): Flow<Boolean>
    suspend fun setAutoBackupEnabled(enabled: Boolean)

    fun getAutoBackupPeriod(): Flow<BackupPeriod>
    suspend fun setAutoBackupPeriod(period: BackupPeriod)

    fun getLastSyncDate(): Flow<ZonedDateTime?>
    suspend fun setLastSyncDate(date: ZonedDateTime)

    fun isBackupConfirmed(): Flow<Boolean>
    suspend fun setBackupConfirmed(confirmed: Boolean)

    suspend fun getUserEmail(): Result<String?>

    suspend fun getContentList(): Result<List<RemoteFile>>
    suspend fun deleteFiles(files: List<RemoteFile>): Result<Unit>
    suspend fun uploadNotesData(notes: List<LocalNote>): Result<Unit>
    suspend fun getRemoteNotes(fileId: String): Result<List<LocalNote>>
    suspend fun uploadMedia(media: LocalNote.Content.Media): Result<Unit>
    suspend fun uploadVoice(voice: LocalNote.Content.Voice): Result<Unit>
    suspend fun loadRemoteLocalToFile(
        remoteFileId: String,
        file: File,
    ): Result<Unit>
}