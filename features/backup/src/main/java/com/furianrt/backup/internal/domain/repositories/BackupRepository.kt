package com.furianrt.backup.internal.domain.repositories

import android.content.Intent
import com.google.android.gms.auth.api.identity.AuthorizationResult
import kotlinx.coroutines.flow.Flow

internal interface BackupRepository {
    suspend fun authorize(): Result<AuthorizationResult>
    fun getAuthorizationResult(intent: Intent?): AuthorizationResult?
    suspend fun signOut(): Result<Unit>

    fun getGoogleAccessToken(): Flow<String?>
    suspend fun updateGoogleAccessToken(token: String)

    fun isAutoBackupEnabled(): Flow<Boolean>
    suspend fun setAutoBackupEnabled(enabled: Boolean)
}