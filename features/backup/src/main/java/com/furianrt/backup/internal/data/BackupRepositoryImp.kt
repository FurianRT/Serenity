package com.furianrt.backup.internal.data

import android.content.Context
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
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.drive.DriveFolder
import com.google.android.gms.drive.MetadataChangeSet
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.FileContent
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.io.IOException
import java.util.Arrays
import javax.inject.Inject


private const val USER_INFO_EMAIL_SCOPE = "https://www.googleapis.com/auth/userinfo.email"

@ViewModelScoped
internal class BackupRepositoryImp @Inject constructor(
    private val authorizationClient: AuthorizationClient,
    private val credentialManager: CredentialManager,
    private val backupDataStore: BackupDataStore,
) : BackupRepository {

    @Throws(IOException::class)
    fun uploadAppData(): String {
        /*Load pre-authorized user credentials from the environment.
        TODO(developer) - See https://developers.google.com/identity for
        guides on implementing OAuth2 for your application.*/
        var credentials: GoogleCredentials? = null
        try {
            credentials = GoogleCredentials.getApplicationDefault()
                .createScoped(Arrays.asList<T>(DriveScopes.DRIVE_APPDATA))
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val requestInitializer = GoogleCredential.Builder().build().apply {
            accessToken =""
            this.refreshToken
        }



        // Build a new authorized API client service.
        val service = Drive.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            requestInitializer
        )
            .setApplicationName("Drive samples")
            .build()
        try {
            // File's metadata.
            val fileMetadata: File = File()
            fileMetadata.setName("config.json")
            fileMetadata.setParents(listOf("appDataFolder"))
            val filePath = File("files/config.json")
            val mediaContent = FileContent("application/json", filePath)
            val file: File = service.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute()
            println("File ID: " + file.getId())
            return file.getId()
        } catch (e: GoogleJsonResponseException) {
            // TODO(developer) - handle error appropriately
            System.err.println("Unable to create file: " + e.details)
            throw e
        }
    }

    suspend fun createAppDataFolder(context: Context): DriveFolder? = suspendCoroutine { cont ->
        val authClient = Identity.getAuthorizationClient(context)

        val scopes = listOf(
            Scope(DriveScopes.DRIVE_APPDATA),
            Scope(USER_INFO_EMAIL_SCOPE),
        )
        val authRequest = AuthorizationRequest.Builder()
            .setRequestedScopes(scopes)
            .build()

        authClient.authorize(authRequest)
            .addOnSuccessListener { signInCredential ->

                signInCredential.accessToken

                val account = signInCredential.googleIdToken?.let {
                    GoogleSignIn.getLastSignedInAccount(context)
                }

                if (account == null) {
                    cont.resume(null)
                    return@addOnSuccessListener
                }


                val driveClient = Drive
                    .Builder()
                    .build()

                driveClient.files()
                    .create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute()

                val metadata = MetadataChangeSet.Builder()
                    .setTitle("MyFolder")
                    .setMimeType(DriveFolder.MIME_TYPE)
                    .build()

                driveClient.getAppFolder()
                    .continueWithTask { folderTask ->
                        val appFolder = folderTask.result
                        driveClient.createFolder(appFolder, metadata)
                    }
                    .addOnSuccessListener { folder ->
                        cont.resume(folder)
                    }
                    .addOnFailureListener {
                        cont.resume(null)
                    }
            }
            .addOnFailureListener {
                cont.resume(null)
            }
    }

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