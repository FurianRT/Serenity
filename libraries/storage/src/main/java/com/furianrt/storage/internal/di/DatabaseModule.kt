package com.furianrt.storage.internal.di

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.withTransaction
import androidx.sqlite.db.SupportSQLiteDatabase
import com.furianrt.storage.api.TransactionsHelper
import com.furianrt.storage.internal.SerenityDatabase
import com.furianrt.storage.internal.notes.dao.ContentBlockDao
import com.furianrt.storage.internal.notes.dao.ImageDao
import com.furianrt.storage.internal.notes.dao.NoteDao
import com.furianrt.storage.internal.notes.dao.NoteTitleDao
import com.furianrt.storage.internal.notes.dao.NoteToTagDao
import com.furianrt.storage.internal.notes.dao.TagDao
import com.furianrt.storage.internal.notes.entities.EntryContentBlock
import com.furianrt.storage.internal.notes.entities.EntryNote
import com.furianrt.storage.internal.notes.entities.EntryNoteImage
import com.furianrt.storage.internal.notes.entities.EntryNoteTag
import com.furianrt.storage.internal.notes.entities.EntryNoteTitle
import com.furianrt.storage.internal.notes.entities.EntryNoteToTag
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.UUID
import javax.inject.Singleton
import kotlin.random.Random

@Module
@InstallIn(SingletonComponent::class)
internal class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SerenityDatabase =
        SerenityDatabase.create(context, ::fillDbWithInitialData)

    @Provides
    @Singleton
    fun noteDao(database: SerenityDatabase): NoteDao = database.noteDao()

    @Provides
    @Singleton
    fun tagDao(database: SerenityDatabase): TagDao = database.tagDao()

    @Provides
    @Singleton
    fun imageDao(database: SerenityDatabase): ImageDao = database.imageDao()

    @Provides
    @Singleton
    fun noteToTagDao(database: SerenityDatabase): NoteToTagDao = database.noteToTagDao()

    @Provides
    @Singleton
    fun contentBlockDao(database: SerenityDatabase): ContentBlockDao = database.contentBlockDao()

    @Provides
    @Singleton
    fun noteTitleDao(database: SerenityDatabase): NoteTitleDao = database.noteTitleDao()

    @Provides
    @Singleton
    fun transactionsHelper(database: SerenityDatabase): TransactionsHelper =
        object : TransactionsHelper {
            override suspend fun <R> withTransaction(block: suspend () -> R): R =
                database.withTransaction(block)
        }

    private fun fillDbWithInitialData(db: SupportSQLiteDatabase) {
        val noteTitle = "Kotlin is a modern programming language with a " +
            "lot more syntactic sugar compared to Java"

        val tagsTitles = listOf("Kotlin", "Programming", "Android", "Development", "Java")

        with(ContentValues()) {
            repeat(tagsTitles.count()) { tagIndex ->
                put(EntryNoteTag.FIELD_ID, tagIndex.toString())
                put(EntryNoteTag.FIELD_TITLE, tagsTitles[tagIndex])
                db.insert(EntryNoteTag.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
                clear()
            }

            repeat(60) {
                val noteId = UUID.randomUUID().toString()
                put(EntryNote.FIELD_ID, noteId)
                put(EntryNote.FIELD_TIMESTAMP, System.currentTimeMillis())
                db.insert(EntryNote.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
                clear()

                val positionFlag = Random.nextBoolean()

                val hasTitle = Random.nextBoolean()

                if (hasTitle) {
                    val titleId = UUID.randomUUID().toString()
                    put(EntryNoteTitle.FIELD_ID, titleId)
                    put(EntryNoteTitle.FIELD_NOTE_ID, noteId)
                    put(EntryNoteTitle.FIELD_POSITION, if (positionFlag) 1 else 0)
                    put(EntryNoteTitle.FIELD_TEXT, noteTitle)
                    db.insert(EntryNoteTitle.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
                    clear()
                }

                if (!hasTitle || Random.nextBoolean()) {
                    val blockId = UUID.randomUUID().toString()
                    put(EntryContentBlock.FIELD_ID, blockId)
                    put(EntryContentBlock.FIELD_NOTE_ID, noteId)
                    put(EntryContentBlock.FIELD_POSITION, if (positionFlag) 0 else 1)
                    db.insert(EntryContentBlock.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
                    clear()

                    repeat(Random.nextInt(1, 5)) { index ->
                        val imageId = UUID.randomUUID().toString()
                        val color = Color(
                            alpha = 255,
                            red = Random.nextInt(256),
                            green = Random.nextInt(256),
                            blue = Random.nextInt(256),
                        ).toArgb().toString()
                        put(EntryNoteImage.FIELD_ID, imageId)
                        put(EntryNoteImage.FIELD_BLOCK_ID, blockId)
                        put(EntryNoteImage.FIELD_URI, color)
                        put(EntryNoteImage.FIELD_POSITION, index)
                        db.insert(EntryNoteImage.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
                        clear()
                    }
                }

                repeat(Random.nextInt(tagsTitles.count())) { tagIndex ->
                    put(EntryNoteToTag.FIELD_NOTE_ID, noteId)
                    put(EntryNoteToTag.FIELD_TAG_ID, tagIndex.toString())
                    db.insert(EntryNoteToTag.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
                    clear()
                }
            }
        }
    }
}
