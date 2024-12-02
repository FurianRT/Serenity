package com.furianrt.domain.repositories

import com.furianrt.domain.entities.LocalNote
import com.furianrt.domain.entities.SimpleNote
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface NotesRepository {
    suspend fun insetNote(note: SimpleNote)
    suspend fun updateNoteText(noteId: String, content: List<LocalNote.Content>)
    suspend fun updateNoteDate(noteId: String, date: ZonedDateTime)
    suspend fun deleteNote(noteId: String)
    fun getAllNotes(): Flow<List<LocalNote>>
    fun getAllNotes(query: String): Flow<List<LocalNote>>
    fun getNote(noteId: String): Flow<LocalNote?>

    fun cacheNoteContent(noteId: String, content: List<LocalNote.Content>)
    fun deleteNoteContentFromCache(noteId: String)
    fun getNoteContentFromCache(noteId: String): List<LocalNote.Content>
}
