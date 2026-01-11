package com.furianrt.notepage.internal.ui.extensions

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import com.furianrt.notelistui.composables.title.NoteTitleState
import com.furianrt.notelistui.entities.UiNoteContent
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.notelistui.entities.UiNoteTag
import com.furianrt.notelistui.entities.isEmptyTitle
import java.util.UUID

internal fun List<UiNoteContent>.refreshTitleTemplates(
    fontFamily: UiNoteFontFamily,
    addTopTemplate: Boolean,
): List<UiNoteContent> {
    val result = toMutableList()
    if (!addTopTemplate && result.firstOrNull().isEmptyTitle()) {
        result.removeAt(0)
    }
    forEachIndexed { index, content ->
        if (content is UiNoteContent.Title) {
            return@forEachIndexed
        }

        if (index == 0 && addTopTemplate) {
            result.add(
                index, UiNoteContent.Title(
                    state = NoteTitleState(fontFamily),
                    id = UUID.randomUUID().toString()
                )
            )
        }

        if (index == lastIndex) {
            val newItemIndex = result.count()
            result.add(
                newItemIndex, UiNoteContent.Title(
                    id = UUID.randomUUID().toString(),
                    state = NoteTitleState(fontFamily),
                )
            )
        }

        val nextItem = getOrElse(index + 1) {
            return@forEachIndexed
        }

        if (nextItem !is UiNoteContent.Title) {
            val newItemIndex = result.indexOf(content) + 1
            result.add(
                newItemIndex, UiNoteContent.Title(
                    id = UUID.randomUUID().toString(),
                    state = NoteTitleState(fontFamily),
                )
            )
        }
    }

    if (result.isEmpty()) {
        result.add(
            UiNoteContent.Title(
                id = UUID.randomUUID().toString(),
                state = NoteTitleState(fontFamily),
            )
        )
    }

    return result
}

internal fun List<UiNoteTag>.addTagTemplate(
    suggestsProvider: (suspend (query: String) -> List<String>),
): List<UiNoteTag> {
    val hasTemplate = any { it is UiNoteTag.Template }
    return if (hasTemplate) {
        this
    } else {
        toMutableList().apply { add(UiNoteTag.Template(suggestsProvider = suggestsProvider)) }
    }
}

internal fun List<UiNoteTag>.removeTagTemplate(
    onlyEmpty: Boolean = false,
): List<UiNoteTag> = toMutableList().apply {
    removeAll { tag ->
        tag is UiNoteTag.Template && (!onlyEmpty || tag.textState.text.isBlank())
    }
}

internal fun List<UiNoteTag>.removeTagTemplate(
    id: String,
): List<UiNoteTag> = toMutableList().apply {
    removeAll { tag ->
        tag is UiNoteTag.Template && (tag.id == id || tag.textState.text.trim() == id)
    }
}

internal fun List<UiNoteTag>.addSecondTagTemplate(
    suggestsProvider: (suspend (query: String) -> List<String>),
): List<UiNoteTag> {
    val hasTemplates = count { it is UiNoteTag.Template } == 2
    return if (hasTemplates) {
        this
    } else {
        toMutableList().apply { add(UiNoteTag.Template(suggestsProvider = suggestsProvider)) }
    }
}

internal fun List<UiNoteTag>.removeSecondTagTemplate(): List<UiNoteTag> {
    val hasTemplates = count { it is UiNoteTag.Template } == 2
    if (!hasTemplates) return this
    val item = findLast { it is UiNoteTag.Template } ?: return this
    return toMutableList().apply { remove(item) }
}

internal fun List<UiNoteContent>.removeMedia(
    id: String,
    focusedTitleId: String?,
): List<UiNoteContent> {
    val mediaBlockIndex = indexOfFirst { content ->
        content is UiNoteContent.MediaBlock && content.media.any { it.id == id }
    }
    if (mediaBlockIndex == -1) {
        return this
    }
    val mediaBlock = this[mediaBlockIndex] as UiNoteContent.MediaBlock
    val newMediaBlock = mediaBlock.copy(
        media = mediaBlock.media.toMutableList().apply { removeAll { it.id == id } },
    )
    return if (newMediaBlock.media.isEmpty()) {
        toMutableList()
            .apply { removeAt(mediaBlockIndex) }
            .joinTitles(focusedTitleId)
    } else {
        toMutableList()
            .apply { set(mediaBlockIndex, newMediaBlock) }
            .joinTitles(focusedTitleId)
    }
}

internal fun List<UiNoteContent>.removeMediaBlock(
    id: String,
    focusedTitleId: String?,
): List<UiNoteContent> {
    val mediaBlockIndex = indexOfFirst { it is UiNoteContent.MediaBlock && it.id == id }
    if (mediaBlockIndex == -1) {
        return this
    }
    return toMutableList()
        .apply { removeAt(mediaBlockIndex) }
        .joinTitles(focusedTitleId)
}

internal fun List<UiNoteContent>.removeVoice(
    id: String,
    focusedTitleId: String?,
): List<UiNoteContent> = toMutableList()
    .apply { removeAll { it is UiNoteContent.Voice && it.id == id } }
    .joinTitles(focusedTitleId)

private fun List<UiNoteContent>.joinTitles(
    focusedTitleId: String?,
): List<UiNoteContent> {
    var counter = 0
    val resultMap = mutableMapOf<Int, UiNoteContent>()
    forEach { content ->
        val entry = resultMap[counter]
        when {
            entry == null && content is UiNoteContent.Title -> resultMap[counter] = content

            entry is UiNoteContent.Title && content is UiNoteContent.Title -> {
                val contentText = content.state.annotatedString
                val entryText = entry.state.annotatedString
                if (contentText.isNotEmpty() && entryText.isNotEmpty()) {
                    if (content.id == focusedTitleId) {
                        content.state.annotatedString =
                            entryText + AnnotatedString("\n") + contentText
                        content.state.selection =
                            TextRange(entryText.length + content.state.selection.max + 1)
                        resultMap[counter] = content
                    } else {
                        entry.state.annotatedString += AnnotatedString("\n") + contentText
                    }
                }
            }

            else -> resultMap[++counter] = content
        }
    }
    return resultMap.values.toList()
}
