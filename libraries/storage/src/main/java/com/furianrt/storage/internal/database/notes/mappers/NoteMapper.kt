package com.furianrt.storage.internal.database.notes.mappers

import com.furianrt.domain.entities.LocalNote
import com.furianrt.domain.entities.SimpleNote
import com.furianrt.storage.internal.database.notes.entities.EntryNote
import com.furianrt.storage.internal.database.notes.entities.EntryNoteImage
import com.furianrt.storage.internal.database.notes.entities.EntryNoteSticker
import com.furianrt.storage.internal.database.notes.entities.EntryNoteTag
import com.furianrt.storage.internal.database.notes.entities.EntryNoteVideo
import com.furianrt.storage.internal.database.notes.entities.LinkedNote

private const val TITLE_START_TAG = "{text}"
private const val TITLE_END_TAG = "{/text}"

private const val MEDIA_START_TAG = "{media}"
private const val MEDIA_END_TAG = "{/media}"

private const val VOICE_START_TAG = "{voice}"
private const val VOICE_END_TAG = "{/voice}"

private enum class FirstTagType {
    TITLE, MEDIA, VOICE, NONE
}

internal fun SimpleNote.toEntryNote() = EntryNote(
    id = id,
    text = "",
    textSpans = emptyList(),
    font = font,
    fontColor = fontColor,
    fontSize = fontSize,
    date = date,
)

internal fun LinkedNote.toLocalNote() = LocalNote(
    id = note.id,
    date = note.date,
    tags = tags.map(EntryNoteTag::toNoteContentTag),
    stickers = stickers.map(EntryNoteSticker::toNoteContentSticker),
    fontFamily = note.font,
    fontColor = note.fontColor,
    fontSize = note.fontSize,
    content = getLocalNoteContent(),
)

internal fun LinkedNote.getLocalNoteContent(): List<LocalNote.Content> {
    return getLocalNoteContent(note.text)
}

internal fun List<LocalNote.Content>.toEntryNoteText(): String {
    val builder = StringBuilder()
    filterNot { it is LocalNote.Content.Title && it.text.isEmpty() }.forEach { content ->
        when (content) {
            is LocalNote.Content.Title -> {
                builder.append(TITLE_START_TAG)
                builder.append("[${content.id}]")
                builder.append(content.text)
                builder.append(TITLE_END_TAG)
            }

            is LocalNote.Content.MediaBlock -> {
                builder.append(MEDIA_START_TAG)
                builder.append("[${content.id}]")
                val ids = content.media.joinToString(separator = ",", transform = { it.id })
                builder.append(ids)
                builder.append(MEDIA_END_TAG)
            }

            is LocalNote.Content.Voice -> {
                builder.append(VOICE_START_TAG)
                builder.append(content.id)
                builder.append(VOICE_END_TAG)
            }
        }
    }
    return builder.toString()
}

private fun LinkedNote.getLocalNoteContent(text: String): List<LocalNote.Content> {
    val (startIndex, content) = when (getFirstTagType(text)) {
        FirstTagType.TITLE -> {
            (text.indexOf(TITLE_END_TAG) + TITLE_END_TAG.length) to extractTitle(text)
        }

        FirstTagType.MEDIA -> {
            (text.indexOf(MEDIA_END_TAG) + MEDIA_END_TAG.length) to extractMedia(text)
        }

        FirstTagType.VOICE -> {
            (text.indexOf(VOICE_END_TAG) + VOICE_END_TAG.length) to extractVoice(text)
        }

        FirstTagType.NONE -> return emptyList()
    }
    return if (content == null) {
        getLocalNoteContent(text.substring(startIndex, text.length))
    } else {
        listOf(content) + getLocalNoteContent(text.substring(startIndex, text.length))
    }
}

private fun LinkedNote.extractTitle(text: String): LocalNote.Content.Title {
    val indexOfTag = text.indexOf(TITLE_START_TAG)
    val indexOfClosingTag = text.indexOf(TITLE_END_TAG)
    val id = text.substring(text.indexOf("[") + 1, text.indexOf("]"))
    val title = text.substring(
        startIndex = indexOfTag + TITLE_START_TAG.length + id.length + 2,
        endIndex = indexOfClosingTag,
    )
    return LocalNote.Content.Title(
        id = id,
        text = title,
        spans = note.textSpans.filter { it.titleId == id },
    )
}

private fun LinkedNote.extractMedia(text: String): LocalNote.Content.MediaBlock? {
    val indexOfTag = text.indexOf(MEDIA_START_TAG)
    val indexOfClosingTag = text.indexOf(MEDIA_END_TAG)
    val id = text.substring(text.indexOf("[") + 1, text.indexOf("]"))
    val mediaIds = text
        .substring(indexOfTag + MEDIA_START_TAG.length + id.length + 2, indexOfClosingTag)
        .split(",")
    val media = mediaIds.mapNotNull { mediaId ->
        images.find { it.id == mediaId } ?: videos.find { it.id == mediaId }
    }
    val localMedia = media.map { item ->
        when (item) {
            is EntryNoteImage -> item.toNoteContentImage()
            is EntryNoteVideo -> item.toNoteContentVideo()
            else -> throw IllegalArgumentException()
        }
    }
    return if (media.isEmpty()) {
        null
    } else {
        LocalNote.Content.MediaBlock(id = id, media = localMedia)
    }
}

private fun LinkedNote.extractVoice(text: String): LocalNote.Content.Voice? {
    val indexOfTag = text.indexOf(VOICE_START_TAG)
    val indexOfClosingTag = text.indexOf(VOICE_END_TAG)
    val id = text.substring(indexOfTag + VOICE_START_TAG.length, indexOfClosingTag)
    return voices.find { it.id == id }?.toNoteContentVoice()
}

private fun getFirstTagType(text: String): FirstTagType {
    class TypeIndex(val type: FirstTagType, val index: Int)

    val typesList = listOf(
        TypeIndex(FirstTagType.TITLE, text.indexOf(TITLE_START_TAG)),
        TypeIndex(FirstTagType.MEDIA, text.indexOf(MEDIA_START_TAG)),
        TypeIndex(FirstTagType.VOICE, text.indexOf(VOICE_START_TAG)),
    )

    val firstType = typesList
        .filter { it.index != -1 }
        .sortedBy(TypeIndex::index)
        .firstOrNull()?.type

    return firstType ?: FirstTagType.NONE
}
