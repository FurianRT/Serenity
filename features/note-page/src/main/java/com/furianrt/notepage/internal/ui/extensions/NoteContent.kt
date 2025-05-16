package com.furianrt.notepage.internal.ui.extensions

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import com.furianrt.notelistui.composables.title.NoteTitleState
import com.furianrt.notelistui.entities.UiNoteContent
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.notelistui.entities.UiNoteTag
import com.furianrt.notelistui.entities.isEmptyTitle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import java.util.UUID

internal fun List<UiNoteContent>.refreshTitleTemplates(
    fontFamily: UiNoteFontFamily,
    addTopTemplate: Boolean,
): ImmutableList<UiNoteContent> {
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

    return result.toImmutableList()
}

internal fun ImmutableList<UiNoteTag>.addTagTemplate(): ImmutableList<UiNoteTag> {
    val hasTemplate = any { it is UiNoteTag.Template }
    return if (hasTemplate) {
        this
    } else {
        toPersistentList().add(UiNoteTag.Template())
    }
}

internal fun ImmutableList<UiNoteTag>.removeTagTemplate(
    onlyEmpty: Boolean = false,
): ImmutableList<UiNoteTag> = toPersistentList().removeAll { tag ->
    tag is UiNoteTag.Template && (!onlyEmpty || tag.textState.text.isBlank())
}

internal fun ImmutableList<UiNoteTag>.removeTagTemplate(
    id: String,
): ImmutableList<UiNoteTag> = toPersistentList().removeAll { tag ->
    tag is UiNoteTag.Template && (tag.id == id || tag.textState.text.trim() == id)
}

internal fun ImmutableList<UiNoteTag>.addSecondTagTemplate(): ImmutableList<UiNoteTag> {
    val hasTemplates = count { it is UiNoteTag.Template } == 2
    return if (hasTemplates) {
        this
    } else {
        toPersistentList().add(UiNoteTag.Template())
    }
}

internal fun ImmutableList<UiNoteTag>.removeSecondTagTemplate(): ImmutableList<UiNoteTag> {
    val hasTemplates = count { it is UiNoteTag.Template } == 2
    if (!hasTemplates) return this
    val item = findLast { it is UiNoteTag.Template } ?: return this
    return toPersistentList().remove(item)
}

internal fun ImmutableList<UiNoteContent>.removeMedia(
    id: String,
    focusedTitleId: String?,
): ImmutableList<UiNoteContent> {
    val mediaBlockIndex = indexOfFirst { content ->
        content is UiNoteContent.MediaBlock && content.media.any { it.id == id }
    }
    if (mediaBlockIndex == -1) {
        return this
    }
    val mediaBlock = this[mediaBlockIndex] as UiNoteContent.MediaBlock
    val newMediaBlock = mediaBlock.copy(
        media = mediaBlock.media.toPersistentList().removeAll { it.id == id },
    )
    return if (newMediaBlock.media.isEmpty()) {
        toPersistentList().removeAt(mediaBlockIndex).joinTitles(focusedTitleId)
    } else {
        toPersistentList().set(mediaBlockIndex, newMediaBlock).joinTitles(focusedTitleId)
    }
}

internal fun ImmutableList<UiNoteContent>.removeVoice(
    id: String,
    focusedTitleId: String?,
): ImmutableList<UiNoteContent> = toPersistentList()
    .removeAll { it is UiNoteContent.Voice && it.id == id }
    .joinTitles(focusedTitleId)

private fun ImmutableList<UiNoteContent>.joinTitles(
    focusedTitleId: String?,
): ImmutableList<UiNoteContent> {
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
    return resultMap.values.toImmutableList()
}
