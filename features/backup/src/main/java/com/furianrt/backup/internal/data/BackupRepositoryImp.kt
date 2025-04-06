package com.furianrt.backup.internal.data

import android.content.Intent
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.exceptions.ClearCredentialException
import com.furianrt.backup.internal.data.local.BackupDataStore
import com.furianrt.backup.internal.domain.exceptions.AuthException
import com.furianrt.backup.internal.domain.repositories.BackupRepository
import com.google.android.gms.auth.api.identity.AuthorizationClient
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.AuthorizationResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject

private const val USER_INFO_EMAIL_SCOPE = "https://www.googleapis.com/auth/userinfo.email"

@ViewModelScoped
internal class BackupRepositoryImp @Inject constructor(
    private val authorizationClient: AuthorizationClient,
    private val credentialManager: CredentialManager,
    private val backupDataStore: BackupDataStore,
) : BackupRepository {

    override suspend fun authorize(): Result<AuthorizationResult> =
        suspendCancellableCoroutine { continuation ->
            val scopes = listOf(
                Scope(DriveScopes.DRIVE_APPDATA),
                Scope(USER_INFO_EMAIL_SCOPE),
            )
            val authRequest = AuthorizationRequest.Builder()
                .setRequestedScopes(scopes)
                .build()

            authorizationClient.authorize(authRequest)
                .addOnSuccessListener { authResponse ->
                    continuation.resume(Result.success(authResponse)) { _, _, _ -> }
                }
                .addOnFailureListener { error ->
                    continuation.resume(Result.failure(error)) { _, _, _ -> }
                }
        }

    override fun getAuthorizationResult(intent: Intent?): AuthorizationResult? = try {
        authorizationClient.getAuthorizationResultFromIntent(intent)
    } catch (e: ApiException) {
        e.printStackTrace()
        null
    }

    override suspend fun signOut(): Result<Unit> = try {
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
        Result.success(Unit)
    } catch (e: ClearCredentialException) {
        Result.failure(AuthException.ClearCredentialException())
    }

    override fun getGoogleAccessToken(): Flow<String?> = backupDataStore.getGoogleAccessToken()

    override suspend fun updateGoogleAccessToken(token: String) {
        backupDataStore.updateGoogleAccessToken(token)
    }

    override fun isAutoBackupEnabled(): Flow<Boolean> = backupDataStore.isAutoBackupEnabled()

    override suspend fun setAutoBackupEnabled(enabled: Boolean) {
        backupDataStore.setAutoBackupEnabled(enabled)
    }
}