package com.furianrt.storage.internal.di

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.furianrt.storage.internal.SerenityDatabase
import com.furianrt.storage.internal.notes.dao.NoteDao
import com.furianrt.storage.internal.notes.entities.DbNote
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
    ): SerenityDatabase = SerenityDatabase.createDatabase(context) { createInitialData(it) }

    @Provides
    fun noteDao(database: SerenityDatabase): NoteDao = database.noteDao()

    private fun createInitialData(db: SupportSQLiteDatabase) {
        with(ContentValues()) {
            val title = "Kotlin is a modern programming language with a " +
                "lot more syntactic sugar compared to Java, and as such " +
                "there is equally more black magic"

            put(DbNote.FIELD_ID, "1")
            put(DbNote.FIELD_TIME, System.currentTimeMillis())
            put(DbNote.FIELD_TITLE, title)
            db.insert(DbNote.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
            clear()

            put(DbNote.FIELD_ID, "2")
            put(DbNote.FIELD_TIME, System.currentTimeMillis())
            put(DbNote.FIELD_TITLE, title)
            db.insert(DbNote.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
            clear()

            put(DbNote.FIELD_ID, "3")
            put(DbNote.FIELD_TIME, System.currentTimeMillis())
            put(DbNote.FIELD_TITLE, title)
            db.insert(DbNote.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
            clear()

            put(DbNote.FIELD_ID, "4")
            put(DbNote.FIELD_TIME, System.currentTimeMillis())
            put(DbNote.FIELD_TITLE, title)
            db.insert(DbNote.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
            clear()

            put(DbNote.FIELD_ID, "5")
            put(DbNote.FIELD_TIME, System.currentTimeMillis())
            put(DbNote.FIELD_TITLE, title)
            db.insert(DbNote.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
            clear()

            put(DbNote.FIELD_ID, "6")
            put(DbNote.FIELD_TIME, System.currentTimeMillis())
            put(DbNote.FIELD_TITLE, title)
            db.insert(DbNote.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
            clear()

            put(DbNote.FIELD_ID, "7")
            put(DbNote.FIELD_TIME, System.currentTimeMillis())
            put(DbNote.FIELD_TITLE, title)
            db.insert(DbNote.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
            clear()

            put(DbNote.FIELD_ID, "8")
            put(DbNote.FIELD_TIME, System.currentTimeMillis())
            put(DbNote.FIELD_TITLE, title)
            db.insert(DbNote.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
            clear()

            put(DbNote.FIELD_ID, "9")
            put(DbNote.FIELD_TIME, System.currentTimeMillis())
            put(DbNote.FIELD_TITLE, title)
            db.insert(DbNote.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
            clear()

            put(DbNote.FIELD_ID, "10")
            put(DbNote.FIELD_TIME, System.currentTimeMillis())
            put(DbNote.FIELD_TITLE, title)
            db.insert(DbNote.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
            clear()

            put(DbNote.FIELD_ID, "11")
            put(DbNote.FIELD_TIME, System.currentTimeMillis())
            put(DbNote.FIELD_TITLE, title)
            db.insert(DbNote.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
            clear()

            put(DbNote.FIELD_ID, "12")
            put(DbNote.FIELD_TIME, System.currentTimeMillis())
            put(DbNote.FIELD_TITLE, title)
            db.insert(DbNote.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
            clear()

            put(DbNote.FIELD_ID, "13")
            put(DbNote.FIELD_TIME, System.currentTimeMillis())
            put(DbNote.FIELD_TITLE, title)
            db.insert(DbNote.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
            clear()

            put(DbNote.FIELD_ID, "14")
            put(DbNote.FIELD_TIME, System.currentTimeMillis())
            put(DbNote.FIELD_TITLE, title)
            db.insert(DbNote.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
            clear()

            put(DbNote.FIELD_ID, "15")
            put(DbNote.FIELD_TIME, System.currentTimeMillis())
            put(DbNote.FIELD_TITLE, title)
            db.insert(DbNote.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
            clear()

            put(DbNote.FIELD_ID, "16")
            put(DbNote.FIELD_TIME, System.currentTimeMillis())
            put(DbNote.FIELD_TITLE, title)
            db.insert(DbNote.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
            clear()

            put(DbNote.FIELD_ID, "17")
            put(DbNote.FIELD_TIME, System.currentTimeMillis())
            put(DbNote.FIELD_TITLE, title)
            db.insert(DbNote.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
            clear()

            put(DbNote.FIELD_ID, "18")
            put(DbNote.FIELD_TIME, System.currentTimeMillis())
            put(DbNote.FIELD_TITLE, title)
            db.insert(DbNote.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
            clear()

            put(DbNote.FIELD_ID, "19")
            put(DbNote.FIELD_TIME, System.currentTimeMillis())
            put(DbNote.FIELD_TITLE, title)
            db.insert(DbNote.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
            clear()

            put(DbNote.FIELD_ID, "20")
            put(DbNote.FIELD_TIME, System.currentTimeMillis())
            put(DbNote.FIELD_TITLE, title)
            db.insert(DbNote.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
            clear()
        }
    }
}
