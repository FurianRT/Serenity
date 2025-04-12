package com.furianrt.backup.internal.data.remote.google.drive

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal class DriveFilesListResponse(
    @SerialName("nextPageToken")
    val nextPageToken: String? = null,

    @SerialName("files")
    val files: List<File>? = null,
) {
    @Serializable
    class File(
        @SerialName("id")
        val id: String,

        @SerialName("name")
        val name: String,

        @SerialName("mimeType")
        val mimeType: String,

        @SerialName("createdTime")
        val createdTime: String,
    )
}