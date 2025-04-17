package com.furianrt.backup.internal.data.remote.google

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.exceptions.ClearCredentialException
import com.furianrt.backup.internal.data.local.BackupDataStore
import com.furianrt.backup.internal.data.remote.google.drive.DriveApiService
import com.furianrt.backup.internal.data.remote.google.info.UserInfoApiService
import com.furianrt.backup.internal.data.remote.google.info.primaryEmail
import com.furianrt.backup.internal.domain.entities.BackupPeriod
import com.furianrt.backup.internal.domain.entities.RemoteFile
import com.furianrt.backup.internal.domain.exceptions.AuthException
import com.furianrt.backup.internal.domain.repositories.BackupRepository
import com.furianrt.backup.internal.extensions.toRemoteFile
import com.furianrt.core.DispatchersProvider
import com.furianrt.domain.entities.LocalNote
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.AuthorizationResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.BufferedSink
import okio.source
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton

private const val USER_INFO_EMAIL_SCOPE = "https://www.googleapis.com/auth/userinfo.email"
private const val USER_PROFILE_SCOPE = "profile"

@Singleton
internal class DriveBackupRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val backupDataStore: BackupDataStore,
    private val userInfoApiService: UserInfoApiService,
    private val driveApiService: DriveApiService,
    private val dispatchers: DispatchersProvider,
) : BackupRepository {

    private val authorizationClient by lazy { Identity.getAuthorizationClient(context) }
    private val credentialManager by lazy { CredentialManager.create(context) }

    override suspend fun authorize(): Result<AuthorizationResult> =
        suspendCancellableCoroutine { continuation ->
            val scopes = listOf(
                Scope(DriveScopes.DRIVE_APPDATA),
                Scope(USER_INFO_EMAIL_SCOPE),
                Scope(USER_PROFILE_SCOPE),
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

    override fun getAccessToken(): Flow<String?> = backupDataStore.getGoogleAccessToken()

    override suspend fun updateAccessToken(token: String) {
        backupDataStore.updateGoogleAccessToken(token)
    }

    override fun isAutoBackupEnabled(): Flow<Boolean> = backupDataStore.isAutoBackupEnabled()

    override suspend fun setAutoBackupEnabled(enabled: Boolean) {
        backupDataStore.setAutoBackupEnabled(enabled)
    }

    override fun getAutoBackupPeriod(): Flow<BackupPeriod> = backupDataStore.getAutoBackupPeriod()

    override suspend fun setAutoBackupPeriod(period: BackupPeriod) {
        backupDataStore.setAutoBackupPeriod(period)
    }

    override fun getLastSyncDate(): Flow<ZonedDateTime?> = backupDataStore.getLastSyncDate()

    override suspend fun setLastSyncDate(date: ZonedDateTime) {
        backupDataStore.setLastSyncDate(date)
    }

    override suspend fun getUserEmail(): Result<String?> {
        return runCatching { userInfoApiService.getUserProfile().primaryEmail }
    }

    override suspend fun getContentList(): Result<List<RemoteFile>> {
        return runCatching { loadContentList() }
    }

    override suspend fun deleteFiles(files: List<RemoteFile>): Result<Unit> = runCatching {
        files.forEach { file ->
            val response = driveApiService.deleteFile(file.id)
            if (!response.isSuccessful && response.code() != 404) {
                throw IOException()
            }
        }
    }

    override suspend fun uploadNotesData(notes: List<LocalNote>): Result<Unit> {
        val jsonString = Json.encodeToString(notes)

        val metadata = """
            {
                "name": "NotesData",
                "mimeType": "application/json",
                "parents": ["appDataFolder"]
            }
        """.trimIndent()

        val metadataRequestBody = metadata
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        val fileRequestBody = jsonString.toRequestBody("application/json".toMediaType())
        val filePart = MultipartBody.Part.createFormData(
            name = "file",
            filename = "NotesData",
            body = fileRequestBody,
        )
        return runCatching { driveApiService.uploadFile(metadataRequestBody, filePart) }
    }

    override suspend fun getRemoteNotes(
        fileId: String,
    ): Result<List<LocalNote>> = withContext(dispatchers.io) {
        runCatching {
            val responseBody = driveApiService.downloadFile(fileId)
            val jsonText = InputStreamReader(responseBody.byteStream()).use { it.readText() }
            Json.decodeFromString<List<LocalNote>>(jsonText)
        }
    }

    override suspend fun uploadMedia(media: LocalNote.Content.Media): Result<Unit> = uploadFile(
        name = media.name,
        fileUri = media.uri,
        mimeType = when (media) {
            is LocalNote.Content.Image -> "image/*"
            is LocalNote.Content.Video -> "video/*"
        },
    )

    override suspend fun uploadVoice(media: LocalNote.Content.Voice): Result<Unit> = uploadFile(
        name = media.id,
        fileUri = media.uri,
        mimeType = "audio/*",
    )

    override suspend fun loadRemoteLocalToFile(
        remoteFileId: String,
        file: File,
    ): Result<Unit> = withContext(dispatchers.io) {
        runCatching {
            val response = driveApiService.downloadFile(remoteFileId)
            val bufferSize = 64 * 1024
            response.byteStream().use { input ->
                FileOutputStream(file).use { output ->
                    val buffer = ByteArray(bufferSize)
                    var bytesRead: Int
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                    }
                }
            }
        }
    }

    private suspend fun uploadFile(
        name: String,
        fileUri: Uri,
        mimeType: String,
    ): Result<Unit> = withContext(dispatchers.io) {
        val contentResolver = context.contentResolver

        val streamBody = object : RequestBody() {
            override fun contentType(): MediaType = mimeType.toMediaType()
            override fun writeTo(sink: BufferedSink) {
                contentResolver.openInputStream(fileUri)?.source()?.use { source ->
                    sink.writeAll(source)
                } ?: error("Unable to open Uri inside writeTo: $fileUri")
            }
        }

        val filePart = MultipartBody.Part.createFormData("file", name, streamBody)
        val metadataJson = """
        {
            "name": "$name",
            "parents": ["appDataFolder"],
            "mimeType": "$mimeType"
        }
    """.trimIndent()

        val metadataRequestBody = metadataJson
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        return@withContext runCatching {
            driveApiService.uploadFile(metadataRequestBody, filePart)
        }
    }

    private tailrec suspend fun loadContentList(
        accumulated: List<RemoteFile> = emptyList(),
        pageToken: String? = null,
    ): List<RemoteFile> {
        val response = driveApiService.getFilesList(pageToken)
        val newFiles = response.files?.mapNotNull { it.toRemoteFile() }.orEmpty()
        val updatedList = accumulated + newFiles

        return if (response.nextPageToken != null) {
            loadContentList(updatedList, response.nextPageToken)
        } else {
            updatedList
        }
    }
}