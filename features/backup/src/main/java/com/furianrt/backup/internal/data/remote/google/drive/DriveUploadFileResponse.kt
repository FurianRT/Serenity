package com.furianrt.backup.internal.data.remote.google.drive

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal class DriveUploadFileResponse(
    @SerialName("id")
    val id: String? = null,

    @SerialName("name")
    val name: String? = null,
)