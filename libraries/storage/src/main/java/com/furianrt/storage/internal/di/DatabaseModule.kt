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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal class DatabaseModule {

    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): SerenityDatabase = SerenityDatabase.create(context) { fillDbWithInitialData(it) }

    @Provides
    fun noteDao(database: SerenityDatabase): NoteDao = database.noteDao()

    @Provides
    fun tagDao(database: SerenityDatabase): TagDao = database.tagDao()

    @Provides
    fun imageDao(database: SerenityDatabase): ImageDao = database.imageDao()

    @Provides
    fun noteToTagDao(database: SerenityDatabase): NoteToTagDao = database.noteToTagDao()

    private fun fillDbWithInitialData(db: SupportSQLiteDatabase) {
        with(ContentValues()) {
            put(EntryNote.FIELD_ID, "1")
            put(EntryNote.FIELD_TIMESTAMP, System.currentTimeMillis())
            db.insert(EntryNote.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
            clear()

            put(EntryNote.FIELD_ID, "2")
            put(EntryNote.FIELD_TIMESTAMP, System.currentTimeMillis())
            db.insert(EntryNote.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
            clear()

            put(EntryNote.FIELD_ID, "3")
            put(EntryNote.FIELD_TIMESTAMP, System.currentTimeMillis())
            db.insert(EntryNote.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
            clear()

            put(EntryNote.FIELD_ID, "4")
            put(EntryNote.FIELD_TIMESTAMP, System.currentTimeMillis())
            db.insert(EntryNote.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
            clear()

            put(EntryNote.FIELD_ID, "5")
            put(EntryNote.FIELD_TIMESTAMP, System.currentTimeMillis())
            db.insert(EntryNote.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
            clear()

            put(EntryNote.FIELD_ID, "6")
            put(EntryNote.FIELD_TIMESTAMP, System.currentTimeMillis())
            db.insert(EntryNote.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
            clear()

            put(EntryNote.FIELD_ID, "7")
            put(EntryNote.FIELD_TIMESTAMP, System.currentTimeMillis())
            db.insert(EntryNote.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
            clear()

            put(EntryNote.FIELD_ID, "8")
            put(EntryNote.FIELD_TIMESTAMP, System.currentTimeMillis())
            db.insert(EntryNote.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
            clear()

            put(EntryNote.FIELD_ID, "9")
            put(EntryNote.FIELD_TIMESTAMP, System.currentTimeMillis())
            db.insert(EntryNote.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
            clear()

            put(EntryNote.FIELD_ID, "10")
            put(EntryNote.FIELD_TIMESTAMP, System.currentTimeMillis())
            db.insert(EntryNote.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
            clear()

            put(EntryNote.FIELD_ID, "11")
            put(EntryNote.FIELD_TIMESTAMP, System.currentTimeMillis())
            db.insert(EntryNote.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
            clear()

            put(EntryNote.FIELD_ID, "12")
            put(EntryNote.FIELD_TIMESTAMP, System.currentTimeMillis())
            db.insert(EntryNote.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
            clear()

            put(EntryNote.FIELD_ID, "13")
            put(EntryNote.FIELD_TIMESTAMP, System.currentTimeMillis())
            db.insert(EntryNote.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
            clear()

            put(EntryNote.FIELD_ID, "14")
            put(EntryNote.FIELD_TIMESTAMP, System.currentTimeMillis())
            db.insert(EntryNote.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
            clear()

            put(EntryNote.FIELD_ID, "15")
            put(EntryNote.FIELD_TIMESTAMP, System.currentTimeMillis())
            db.insert(EntryNote.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
            clear()

            put(EntryNote.FIELD_ID, "16")
            put(EntryNote.FIELD_TIMESTAMP, System.currentTimeMillis())
            db.insert(EntryNote.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
            clear()

            put(EntryNote.FIELD_ID, "17")
            put(EntryNote.FIELD_TIMESTAMP, System.currentTimeMillis())
            db.insert(EntryNote.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
            clear()

            put(EntryNote.FIELD_ID, "18")
            put(EntryNote.FIELD_TIMESTAMP, System.currentTimeMillis())
            db.insert(EntryNote.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
            clear()

            put(EntryNote.FIELD_ID, "19")
            put(EntryNote.FIELD_TIMESTAMP, System.currentTimeMillis())
            db.insert(EntryNote.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
            clear()

            put(EntryNote.FIELD_ID, "20")
            put(EntryNote.FIELD_TIMESTAMP, System.currentTimeMillis())
            db.insert(EntryNote.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
            clear()
        }
    }
}
