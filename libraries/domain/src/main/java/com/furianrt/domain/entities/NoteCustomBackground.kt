package com.furianrt.domain.entities

import android.net.Uri
import com.furianrt.common.UriSerializer
import com.furianrt.common.ZonedDateTimeSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import java.time.ZonedDateTime

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class NoteCustomBackground(
    @SerialName("id")
    val id: String,

    @SerialName("name")
    val name: String,

    @Serializable(with = UriSerializer::class)
    @SerialName("uri")
    val uri: Uri,
    @SerialName("primary_color")
    val primaryColor: Int,

    @SerialName("accent_color")
    val accentColor: Int,

    @SerialName("is_light")
    val isLight: Boolean,

    @SerialName("is_hidden")
    val isHidden: Boolean,

    @Serializable(with = ZonedDateTimeSerializer::class)
    @SerialName("added_date")
    val addedDate: ZonedDateTime,
)
