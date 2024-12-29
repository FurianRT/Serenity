package com.furianrt.domain.entities

import android.net.Uri
import com.furianrt.common.UriSerializer
import com.furianrt.common.ZonedDateTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Serializable
data class LocalNote(
    val id: String,
    @Serializable(with = ZonedDateTimeSerializer::class)
    val date: ZonedDateTime,
    val fontFamily: NoteFontFamily,
    val fontColor: NoteFontColor,
    val fontSize: Int,
    val tags: List<Tag>,
    val content: List<Content>,
) {
    @Serializable
    data class Tag(val title: String)

    @Serializable
    sealed class Content(open val id: String) {

        @Serializable
        data class Title(
            @SerialName("title_id")
            override val id: String,
            val text: String,
        ) : Content(id)

        @Serializable
        data class MediaBlock(
            @SerialName("media_block_id")
            override val id: String,
            val media: List<Media>,
        ) : Content(id)

        @Serializable
        sealed class Media(
            open val name: String,
            open val ratio: Float,

            @Serializable(with = UriSerializer::class)
            open val uri: Uri,

            @Serializable(with = ZonedDateTimeSerializer::class)
            open val addedDate: ZonedDateTime,
        )

        @Serializable
        data class Image(
            @SerialName("image_name")
            override val name: String,

            @Serializable(with = UriSerializer::class)
            @SerialName("image_uri")
            override val uri: Uri,

            @SerialName("image_ratio")
            override val ratio: Float,

            @Serializable(with = ZonedDateTimeSerializer::class)
            @SerialName("image_date")
            override val addedDate: ZonedDateTime,
        ) : Media(name, ratio, uri, addedDate)

        @Serializable
        data class Video(
            @SerialName("video_name")
            override val name: String,

            @Serializable(with = UriSerializer::class)
            @SerialName("video_uri")
            override val uri: Uri,

            @SerialName("video_ratio")
            override val ratio: Float,

            @Serializable(with = ZonedDateTimeSerializer::class)
            @SerialName("video_date")
            override val addedDate: ZonedDateTime,

            val duration: Int,
        ) : Media(name, ratio, uri, addedDate)
    }
}
