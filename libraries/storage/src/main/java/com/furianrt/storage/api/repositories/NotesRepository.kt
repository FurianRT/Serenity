package com.furianrt.storage.api.repositories

import com.furianrt.storage.api.entities.LocalNote
import com.furianrt.storage.api.entities.LocalSimpleNote
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    suspend fun upsertNote(note: LocalNote)

    suspend fun deleteNote(note: LocalNote)

    suspend fun getAllNotes(): Flow<List<LocalNote>>

    suspend fun getAllNotesSimple(): Flow<List<LocalSimpleNote>>

    suspend fun getNote(noteId: String): Flow<LocalNote?>

    suspend fun upsertNoteContent(noteId: String, content: List<LocalNote.Content>)

    suspend fun upsertNoteTitle(noteId: String, title: LocalNote.Content.Title)

    suspend fun upsertNoteTitle(noteId: String, titles: List<LocalNote.Content.Title>)
}
