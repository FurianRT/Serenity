package com.furianrt.storage.internal.database.notes.mappers

import com.furianrt.domain.entities.LocalNote
import com.furianrt.domain.entities.SimpleNote
import com.furianrt.storage.internal.database.notes.entities.EntryNote
import com.furianrt.storage.internal.database.notes.entities.EntryNoteImage
import com.furianrt.storage.internal.database.notes.entities.EntryNoteTag
import com.furianrt.storage.internal.database.notes.entities.EntryNoteVideo
import com.furianrt.storage.internal.database.notes.entities.LinkedNote

private const val TITLE_START_TAG = "{text}"
private const val TITLE_END_TAG = "{/text}"
private const val MEDIA_START_TAG = "{media}"
private const val MEDIA_END_TAG = "{/media}"

private enum class FirstTagType {
    TITLE, MEDIA, NONE
}

internal fun SimpleNote.toEntryNote() = EntryNote(
    id = id,
    text = "",
    font = font,
    fontColor = fontColor,
    fontSize = fontSize,
    date = date,
)

internal fun LinkedNote.toLocalNote() = LocalNote(
    id = note.id,
    date = note.date,
    tags = tags.map(EntryNoteTag::toNoteContentTag),
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
                val names = content.media.joinToString(separator = ",", transform = { it.name })
                builder.append(names)
                builder.append(MEDIA_END_TAG)
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

        FirstTagType.NONE -> return emptyList()
    }
    return if (content == null) {
        getLocalNoteContent(text.substring(startIndex, text.length))
    } else {
        listOf(content) + getLocalNoteContent(text.substring(startIndex, text.length))
    }
}

private fun extractTitle(text: String): LocalNote.Content.Title {
    val indexOfTag = text.indexOf(TITLE_START_TAG)
    val indexOfClosingTag = text.indexOf(TITLE_END_TAG)
    val id = text.substring(text.indexOf("[") + 1, text.indexOf("]"))
    val title = text.substring(
        startIndex = indexOfTag + TITLE_START_TAG.length + id.length + 2,
        endIndex = indexOfClosingTag,
    )
    return LocalNote.Content.Title(id = id, text = title)
}

private fun LinkedNote.extractMedia(text: String): LocalNote.Content.MediaBlock? {
    val indexOfTag = text.indexOf(MEDIA_START_TAG)
    val indexOfClosingTag = text.indexOf(MEDIA_END_TAG)
    val id = text.substring(text.indexOf("[") + 1, text.indexOf("]"))
    val mediaNames = text
        .substring(indexOfTag + MEDIA_START_TAG.length + id.length + 2, indexOfClosingTag)
        .split(",")
    val media = mediaNames.mapNotNull { mediaName ->
        images.find { it.name == mediaName } ?: videos.find { it.name == mediaName }
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

private fun getFirstTagType(text: String): FirstTagType {
    val indexOfTitleTag = text.indexOf(TITLE_START_TAG)
    val indexOfMediaTag = text.indexOf(MEDIA_START_TAG)
    return when {
        indexOfTitleTag != -1 && indexOfMediaTag != -1 -> if (indexOfTitleTag < indexOfMediaTag) {
            FirstTagType.TITLE
        } else {
            FirstTagType.MEDIA
        }

        indexOfTitleTag != -1 && indexOfMediaTag == -1 -> FirstTagType.TITLE
        indexOfTitleTag == -1 && indexOfMediaTag != -1 -> FirstTagType.MEDIA
        else -> FirstTagType.NONE
    }
}
