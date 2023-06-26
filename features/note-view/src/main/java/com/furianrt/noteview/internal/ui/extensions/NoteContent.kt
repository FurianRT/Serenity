package com.furianrt.noteview.internal.ui.extensions

import com.furianrt.notecontent.entities.UiNoteContent
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

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
        .apply { removeIf { it is UiNoteContent.Title && it.text.isEmpty() } }
        .mapIndexed { index, content -> content.changePosition(index) }
        .toImmutableList()
