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
    val fontFamily: NoteFontFamily?,
    val fontColor: NoteFontColor?,
    val fontSize: Int,
    val isPinned: Boolean,
    val tags: List<Tag>,
    val stickers: List<Sticker>,
    val content: List<Content>,
) {
    @Serializable
    data class Tag(val title: String)

    @Serializable
    data class Sticker(
        val id: String,
        val typeId: String,
        val scale: Float,
        val rotation: Float,
        val isFlipped: Boolean,
        val biasX: Float,
        val dpOffsetY: Float,
        val editTime: Long,
    )

    @Serializable
    sealed class Content(open val id: String) {

        @Serializable
        @SerialName("Title")
        data class Title(
            @SerialName("title_id")
            override val id: String,
            val text: String,
            val spans: List<NoteTextSpan>,
        ) : Content(id)

        @Serializable
        @SerialName("MediaBlock")
        data class MediaBlock(
            @SerialName("media_block_id")
            override val id: String,
            val media: List<Media>,
        ) : Content(id)

        @Serializable
        @SerialName("Voice")
        data class Voice(
            @SerialName("voice_id")
            override val id: String,

            @Serializable(with = UriSerializer::class)
            @SerialName("voice_uri")
            val uri: Uri,

            val duration: Int,
            val volume: List<Float>,
        ) : Content(id)

        @Serializable
        sealed class Media(
            open val id: String,
            open val name: String,
            open val ratio: Float,

            @Serializable(with = UriSerializer::class)
            open val uri: Uri,

            @Serializable(with = ZonedDateTimeSerializer::class)
            open val addedDate: ZonedDateTime,
        )

        @Serializable
        @SerialName("Image")
        data class Image(
            @SerialName("image_id")
            override val id: String,

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
        ) : Media(id, name, ratio, uri, addedDate)

        @Serializable
        @SerialName("Video")
        data class Video(
            @SerialName("video_id")
            override val id: String,

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
        ) : Media(id, name, ratio, uri, addedDate)
    }
}
