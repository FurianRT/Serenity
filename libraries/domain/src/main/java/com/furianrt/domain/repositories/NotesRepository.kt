package com.furianrt.domain.repositories

import com.furianrt.domain.entities.LocalNote
import com.furianrt.domain.entities.NoteFontColor
import com.furianrt.domain.entities.NoteFontFamily
import com.furianrt.domain.entities.SimpleNote
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZonedDateTime

interface NotesRepository {
    suspend fun insertNote(note: SimpleNote)
    suspend fun upsertNote(note: SimpleNote)
    suspend fun updateNoteText(noteId: String, content: List<LocalNote.Content>)
    suspend fun updateNoteDate(noteId: String, date: ZonedDateTime)
    suspend fun updateNoteIsPinned(noteId: String, isPinned: Boolean)
    suspend fun updateNoteIsPinned(noteIds: Set<String>, isPinned: Boolean)
    suspend fun updateNoteBackgroundId(
        noteId: String,
        backgroundId: String?,
        backgroundImageId: String?,
    )

    suspend fun updateNoteMoodId(noteId: String, moodId: String?)
    suspend fun updateNoteDefaultMoodId(moodId: String)
    suspend fun updateNoteFont(
        noteId: String,
        color: NoteFontColor?,
        family: NoteFontFamily?,
        size: Int,
    )

    suspend fun setTemplate(noteId: String, isTemplate: Boolean)

    suspend fun deleteNote(noteId: String)
    suspend fun deleteNotes(noteIds: Set<String>)
    fun getAllNotes(): Flow<List<LocalNote>>
    fun getAllNotes(query: String): Flow<List<LocalNote>>
    fun getNote(noteId: String): Flow<LocalNote?>
    fun getOrCreateTemplateNote(noteId: String): Flow<SimpleNote>
    fun getAllTemplates(): Flow<List<SimpleNote>>

    fun getUniqueNotesDates(): Flow<Set<LocalDate>>

    fun cacheNoteContent(noteId: String, content: List<LocalNote.Content>)
    fun deleteNoteContentFromCache(noteId: String)
    fun getNoteContentFromCache(noteId: String): List<LocalNote.Content>

    fun enqueueOneTimeCleanup()
    fun enqueuePeriodicCleanup()
}
