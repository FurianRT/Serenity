package com.furianrt.storage.internal.di

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.furianrt.storage.api.TransactionsHelper
import com.furianrt.storage.internal.database.SerenityDatabase
import com.furianrt.storage.internal.database.notes.dao.ImageDao
import com.furianrt.storage.internal.database.notes.dao.NoteDao
import com.furianrt.storage.internal.database.notes.dao.NoteToTagDao
import com.furianrt.storage.internal.database.notes.dao.TagDao
import com.furianrt.storage.internal.database.notes.dao.VideoDao
import com.furianrt.storage.internal.database.notes.entities.EntryNote
import com.furianrt.storage.internal.database.notes.entities.EntryNoteTag
import com.furianrt.storage.internal.database.notes.entities.EntryNoteToTag
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.time.ZonedDateTime
import java.util.UUID
import javax.inject.Singleton
import kotlin.random.Random

@Module
@InstallIn(SingletonComponent::class)
internal class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): SerenityDatabase = SerenityDatabase.create(context, ::fillDbWithInitialData)

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
    fun videoDao(database: SerenityDatabase): VideoDao = database.videoDao()

    @Provides
    @Singleton
    fun noteToTagDao(database: SerenityDatabase): NoteToTagDao = database.noteToTagDao()

    @Provides
    @Singleton
    fun transactionsHelper(database: SerenityDatabase): TransactionsHelper = database

    private fun fillDbWithInitialData(db: SupportSQLiteDatabase) {
        val tagsTitles = listOf("Kotlin", "Programming", "Android", "Development", "Java")

       /* val imageUrls = listOf(
            Pair(
                "content://media/external_primary/file/1000005635",
                1.333f,
            ),
            Pair(
                "content://media/external_primary/file/1000005541",
                0.68f,
            ),
            Pair(
                "content://media/external_primary/file/1000005540",
                1.5f,
            ),
            Pair("content://media/external_primary/file/1000005539", 1.782f),
            Pair(
                "content://media/external_primary/file/1000005538",
                0.574f,
            ),
            Pair(
                "content://media/external_primary/file/1000005537",
                1.526f,
            ),
            Pair(
                "content://media/external_primary/file/1000005536",
                0.75f,
            ),
        )*/

        with(ContentValues()) {
            repeat(tagsTitles.count()) { tagIndex ->
                put(EntryNoteTag.FIELD_ID, tagIndex.toString())
                put(EntryNoteTag.FIELD_TITLE, tagsTitles[tagIndex])
                db.insert(EntryNoteTag.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
                clear()
            }

            repeat(20) { times ->
                val noteId = UUID.randomUUID().toString()
                val dateTime = ZonedDateTime.now().plusDays(times.toLong())
                val resultMills = dateTime.toInstant().toEpochMilli()
               /* val text = "{text}[id1]Kotlin is a modern programming language{/text}" +
                        "{media}[id2]${noteId + "0"},${noteId + "1"}{/media}" +
                        "{text}[id3]with a lot more syntactic{/text}" +
                        "{media}[id4]${noteId + "2"},${noteId + "3"}{/media}" +
                        "{text}[id5]sugar compared to Java{/text}"*/
                val text = "{text}[id1]Kotlin is a modern programming language{/text}"
                put(EntryNote.FIELD_ID, noteId)
                put(EntryNote.FIELD_TIMESTAMP, resultMills)
                put(EntryNote.FIELD_TEXT, text)
                db.insert(EntryNote.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
                clear()

               /* repeat(4) { index ->
                    val imageId = index.toString()
                    val imageIndex = Random.nextInt(imageUrls.count())
                    put(EntryNoteImage.FIELD_ID, noteId + imageId)
                    put(EntryNoteImage.FIELD_NOTE_ID, noteId)
                    put(EntryNoteImage.FIELD_URI, imageUrls[imageIndex].first)
                    put(EntryNoteImage.FIELD_RATIO, imageUrls[imageIndex].second)
                    put(EntryNoteImage.FIELD_DATE, System.currentTimeMillis())
                    db.insert(EntryNoteImage.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
                    clear()
                }*/

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
