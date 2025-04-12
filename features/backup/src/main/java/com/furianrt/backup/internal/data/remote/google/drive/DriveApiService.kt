package com.furianrt.backup.internal.data.remote.google.drive

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

private const val UPLOAD_FILE_METHOD = "upload/drive/v3/files?uploadType=multipart"
private const val FILES_METHOD = "drive/v3/files"

internal interface DriveApiService {

    @GET(FILES_METHOD)
    suspend fun getFilesList(
        @Query("pageToken") pageToken: String?,
        @Query("spaces") spaces: String = "appDataFolder",
        @Query("fields") fields: String = "files(id,name,mimeType,createdTime)",
    ): DriveFilesListResponse

    @DELETE("$FILES_METHOD/{fileId}")
    suspend fun deleteFile(
        @Path("fileId") fileId: String,
    ): Response<Unit>

    @Multipart
    @POST(UPLOAD_FILE_METHOD)
    suspend fun uploadFile(
        @Part("metadata") metadata: RequestBody,
        @Part filePart: MultipartBody.Part,
    )

    @GET("$FILES_METHOD/{fileId}?alt=media")
    @Streaming
    suspend fun downloadFile(
        @Path("fileId") fileId: String,
    ): ResponseBody
}