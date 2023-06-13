package com.furianrt.storage.internal.di

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.furianrt.storage.internal.SerenityDatabase
import com.furianrt.storage.internal.notes.dao.ImageDao
import com.furianrt.storage.internal.notes.dao.NoteDao
import com.furianrt.storage.internal.notes.dao.NoteToTagDao
import com.furianrt.storage.internal.notes.dao.TagDao
import com.furianrt.storage.internal.notes.entities.EntryNote
import com.furianrt.storage.internal.notes.entities.EntryNoteTag
import com.furianrt.storage.internal.notes.entities.EntryNoteTitle
import com.furianrt.storage.internal.notes.entities.EntryNoteToTag
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlin.random.Random

@Module
@InstallIn(SingletonComponent::class)
internal class DatabaseModule {

    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): SerenityDatabase = SerenityDatabase.create(context, ::fillDbWithInitialData)

    @Provides
    fun noteDao(database: SerenityDatabase): NoteDao = database.noteDao()

    @Provides
    fun tagDao(database: SerenityDatabase): TagDao = database.tagDao()

    @Provides
    fun imageDao(database: SerenityDatabase): ImageDao = database.imageDao()

    @Provides
    fun noteToTagDao(database: SerenityDatabase): NoteToTagDao = database.noteToTagDao()

    private fun fillDbWithInitialData(db: SupportSQLiteDatabase) {
        val noteTitle = "Kotlin is a modern programming language with a " +
            "lot more syntactic sugar compared to Java"

        val tagsTitles = listOf("Kotlin", "Programming", "Android", "Development", "Java")

        for (noteIndex in 0..19) {
            with(ContentValues()) {
                put(EntryNote.FIELD_ID, noteIndex.toString())
                put(EntryNote.FIELD_TIMESTAMP, System.currentTimeMillis())
                db.insert(EntryNote.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
                clear()

                for (noteTitleIndex in 0 until 1) {
                    put(EntryNoteTitle.FIELD_ID, noteIndex.toString() + noteTitleIndex.toString())
                    put(EntryNoteTitle.FIELD_NOTE_ID, noteIndex.toString())
                    put(EntryNoteTitle.FIELD_TEXT, noteTitle)
                    put(EntryNoteTitle.FIELD_BLOCK_POSITION, noteTitleIndex)
                    db.insert(EntryNoteTitle.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
                    clear()
                }
            }
        }

        for (tagIndex in 0 until tagsTitles.count()) {
            with(ContentValues()) {
                put(EntryNoteTag.FIELD_ID, tagIndex.toString())
                put(EntryNoteTag.FIELD_TITLE, tagsTitles[tagIndex])
                db.insert(EntryNoteTag.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
            }
        }

        for (noteIndex in 0..19) {
            for (tagIndex in 0..Random.nextInt(tagsTitles.count())) {
                with(ContentValues()) {
                    put(EntryNoteToTag.FIELD_NOTE_ID, noteIndex.toString())
                    put(EntryNoteToTag.FIELD_TAG_ID, tagIndex.toString())
                    db.insert(EntryNoteToTag.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
                }
            }
        }
    }
}
