package com.furianrt.storage.internal.database.notes.mappers

import com.furianrt.storage.api.entities.LocalNote
import com.furianrt.storage.internal.database.notes.entities.EntryNote
import com.furianrt.storage.internal.database.notes.entities.EntryNoteImage
import com.furianrt.storage.internal.database.notes.entities.EntryNoteTag
import com.furianrt.storage.internal.database.notes.entities.EntryNoteVideo
import com.furianrt.storage.internal.database.notes.entities.LinkedNote

private const val MEDIA_START_TAG = "{media_block}"
private const val MEDIA_END_TAG = "{/media_block}"

internal fun LinkedNote.toLocalNote() = LocalNote(
    id = note.id,
    timestamp = note.timestamp,
    tags = tags.map(EntryNoteTag::toNoteContentTag),
    content = getLocalNoteContent(),
)

internal fun LocalNote.toEntryNote() = EntryNote(
    id = id,
    text = content.toEntryNoteText(),
    timestamp = timestamp,
)

internal fun LinkedNote.getLocalNoteContent(): List<LocalNote.Content> = testFun(note.text)

internal fun List<LocalNote.Content>.toEntryNoteText(): String {
    val builder = StringBuilder()
    forEach { content ->
        when (content) {
            is LocalNote.Content.Title -> builder.append(content.text)
            is LocalNote.Content.MediaBlock -> {
                builder.append(MEDIA_START_TAG)
                val ids = content.media.joinToString(separator = ",", transform = { it.id })
                builder.append(ids)
                builder.append(MEDIA_END_TAG)
            }
        }
    }
    return builder.toString()
}

private fun LinkedNote.testFun(text: String): List<LocalNote.Content> {
    val indexOfTag = text.indexOf(MEDIA_START_TAG)
    if (indexOfTag == -1) {
        return listOf(LocalNote.Content.Title(text))
    }
    val indexOfClosingTag = text.indexOf(MEDIA_END_TAG)
    val title = text.substring(0, indexOfTag)
    val mediaIds = text
        .substring(indexOfTag + MEDIA_START_TAG.length, indexOfClosingTag)
        .split(",")
    val media = mediaIds.mapNotNull { id ->
        images.find { it.id == id } ?: videos.find { it.id == id }
    }
    val result = mutableListOf<LocalNote.Content>()
    val localMedia = media.map { item ->
        when (item) {
            is EntryNoteImage -> item.toNoteContentImage()
            is EntryNoteVideo -> item.toNoteContentVideo()
            else -> throw IllegalArgumentException()
        }
    }
    result.add(LocalNote.Content.Title(title))
    result.add(LocalNote.Content.MediaBlock(localMedia))
    return result + testFun(text.substring(indexOfClosingTag + MEDIA_END_TAG.length, text.length))
}
