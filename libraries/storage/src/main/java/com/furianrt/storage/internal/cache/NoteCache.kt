package com.furianrt.storage.internal.cache

import com.furianrt.domain.entities.LocalNote
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class NoteCache @Inject constructor() {

    private val cache = mutableMapOf<String, List<LocalNote.Content>>()

    @Synchronized
    fun getNoteContent(noteId: String): List<LocalNote.Content> = cache[noteId].orEmpty()

    @Synchronized
    fun cacheNoteContent(noteId: String, content: List<LocalNote.Content>) {
        cache[noteId] = content
    }

    @Synchronized
    fun deleteCache(noteId: String) {
        cache.remove(noteId)
    }
}