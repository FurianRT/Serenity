package com.furianrt.notepage.internal.ui.extensions

import com.furianrt.notecontent.entities.UiNoteContent
import com.furianrt.notecontent.entities.UiNoteTag
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import java.util.UUID

internal fun List<UiNoteContent>.addTitleTemplates(): ImmutableList<UiNoteContent> {
    val result = toMutableList()
    forEachIndexed { index, content ->
        if (content is UiNoteContent.Title) {
            return@forEachIndexed
        }

        if (index == 0) {
            result.add(index, UiNoteContent.Title(id = UUID.randomUUID().toString()))
        }

        if (index == lastIndex) {
            val newItemIndex = result.count()
            result.add(newItemIndex, UiNoteContent.Title(id = UUID.randomUUID().toString()))
        }

        val nextItem = getOrElse(index + 1) {
            return@forEachIndexed
        }

        if (nextItem !is UiNoteContent.Title) {
            val newItemIndex = result.indexOf(content) + 1
            result.add(newItemIndex, UiNoteContent.Title(id = UUID.randomUUID().toString()))
        }
    }

    if (result.isEmpty()) {
        result.add(UiNoteContent.Title(id = UUID.randomUUID().toString()))
    }

    return result.toImmutableList()
}

internal fun ImmutableList<UiNoteContent>.removeTitleTemplates(): ImmutableList<UiNoteContent> =
    toPersistentList().removeAll { it is UiNoteContent.Title && it.state.text.isEmpty() }

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
    tag is UiNoteTag.Template && tag.id == id
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
    name: String,
): ImmutableList<UiNoteContent> {
    val mediaBlockIndex = indexOfFirst { content ->
        content is UiNoteContent.MediaBlock && content.media.any { it.name == name }
    }
    if (mediaBlockIndex == -1) {
        return this
    }
    val mediaBlock = this[mediaBlockIndex] as UiNoteContent.MediaBlock
    val newMediaBlock = mediaBlock.copy(
        media = mediaBlock.media.toPersistentList().removeAll { it.name == name },
    )
    return if (newMediaBlock.media.isEmpty()) {
        toPersistentList().removeAt(mediaBlockIndex).joinTitles()
    } else {
        toPersistentList().set(mediaBlockIndex, newMediaBlock).joinTitles()
    }
}

internal fun ImmutableList<UiNoteContent>.joinTitles(): ImmutableList<UiNoteContent> {
    var counter = 0
    val resultMap = mutableMapOf<Int, UiNoteContent>()
    forEach { content ->
        val entry = resultMap[counter]
        when {
            entry == null && content is UiNoteContent.Title -> resultMap[counter] = content

            entry is UiNoteContent.Title && content is UiNoteContent.Title -> entry.state.edit {
                if (content.state.text.isNotEmpty()) {
                    appendLine()
                    append(content.state.text)
                }
            }

            else -> resultMap[++counter] = content
        }
    }
    return resultMap.values.toImmutableList()
}
