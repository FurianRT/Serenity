package com.furianrt.noteview.internal.ui.extensions

import com.furianrt.notecontent.entities.UiNoteContent
import com.furianrt.notecontent.entities.UiNoteTag
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList

internal fun ImmutableList<UiNoteContent>.addTitleTemplates(): ImmutableList<UiNoteContent> {
    val result = this.toMutableList()
    forEachIndexed { index, content ->
        if (content is UiNoteContent.Title) {
            return@forEachIndexed
        }

        if (index == 0) {
            result.add(index, UiNoteContent.Title(position = index))
        }

        if (index == lastIndex) {
            val newItemIndex = result.count()
            result.add(newItemIndex, UiNoteContent.Title(position = newItemIndex))
        }

        val nextItem = getOrElse(index + 1) {
            return@forEachIndexed
        }

        if (nextItem !is UiNoteContent.Title) {
            val newItemIndex = result.indexOf(content) + 1
            result.add(newItemIndex, UiNoteContent.Title(position = newItemIndex))
        }
    }

    if (result.isEmpty()) {
        result.add(UiNoteContent.Title(position = 0))
    }

    return result.mapIndexed { index, content -> content.changePosition(index) }.toImmutableList()
}

internal fun ImmutableList<UiNoteContent>.removeTitleTemplates(): ImmutableList<UiNoteContent> =
    toMutableList()
        .apply { removeIf { it is UiNoteContent.Title && it.state.text.isEmpty() } }
        .mapIndexed { index, content -> content.changePosition(index) }
        .toImmutableList()

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
