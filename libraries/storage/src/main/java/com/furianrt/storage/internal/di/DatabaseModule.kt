package com.furianrt.storage.internal.di

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.furianrt.storage.internal.database.TransactionsHelper
import com.furianrt.storage.internal.database.SerenityDatabase
import com.furianrt.storage.internal.database.notes.dao.ContentBlockDao
import com.furianrt.storage.internal.database.notes.dao.ImageDao
import com.furianrt.storage.internal.database.notes.dao.NoteDao
import com.furianrt.storage.internal.database.notes.dao.NoteTitleDao
import com.furianrt.storage.internal.database.notes.dao.NoteToTagDao
import com.furianrt.storage.internal.database.notes.dao.TagDao
import com.furianrt.storage.internal.database.notes.entities.EntryContentBlock
import com.furianrt.storage.internal.database.notes.entities.EntryNote
import com.furianrt.storage.internal.database.notes.entities.EntryNoteImage
import com.furianrt.storage.internal.database.notes.entities.EntryNoteTag
import com.furianrt.storage.internal.database.notes.entities.EntryNoteTitle
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
    fun noteToTagDao(database: SerenityDatabase): NoteToTagDao = database.noteToTagDao()

    @Provides
    @Singleton
    fun contentBlockDao(database: SerenityDatabase): ContentBlockDao = database.contentBlockDao()

    @Provides
    @Singleton
    fun noteTitleDao(database: SerenityDatabase): NoteTitleDao = database.noteTitleDao()

    @Provides
    @Singleton
    fun transactionsHelper(database: SerenityDatabase): TransactionsHelper = database

    private fun fillDbWithInitialData(db: SupportSQLiteDatabase) {
        val noteTitle = "Kotlin is a modern programming language with a " +
                "lot more syntactic sugar compared to Java"

        val tagsTitles = listOf("Kotlin", "Programming", "Android", "Development", "Java")

        val imageUrls = listOf(
            Pair(
                "https://kartinkof.club/uploads/posts/2022-12/1670401826_kartinkof-club-p-kartinki-neobichnie-so-smislom-1.jpg",
                1.333f,
            ),
            Pair(
                "https://i.pinimg.com/originals/25/90/a1/2590a1a6759841581e6e1ed7fc91376d.jpg",
                0.68f,
            ),
            Pair(
                "https://appleinsider.ru/wp-content/uploads/2019/07/drew-hays-z0WDn0Mas9o-unsplash-1.jpg",
                1.5f,
            ),
            Pair("https://ss.sport-express.ru/userfiles/materials/187/1876626/volga.jpg", 1.782f),
            Pair(
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRjuyn1MYqy3hrOOT8LtspjE3Ss4HsRbricew&usqp=CAU",
                0.574f,
            ),
            Pair(
                "https://moslenta.ru/thumb/1200x0/filters:quality(75):no_upscale()/imgs/2022/08/15/08/5541985/9432ed2f471e51edb0d70e1dcc0b4089591bec2d.jpg",
                1.526f,
            ),
            Pair(
                "https://amiel.club/uploads/posts/2022-03/1647751619_1-amiel-club-p-krasivie-kartinki-na-vaiber-1.jpg",
                0.75f,
            ),
        )

        with(ContentValues()) {
            repeat(tagsTitles.count()) { tagIndex ->
                put(EntryNoteTag.FIELD_ID, tagIndex.toString())
                put(EntryNoteTag.FIELD_TITLE, tagsTitles[tagIndex])
                db.insert(EntryNoteTag.TABLE_NAME, SQLiteDatabase.CONFLICT_ABORT, this)
                clear()
            }

            repeat(60) { times ->
                val noteId = UUID.randomUUID().toString()
                put(EntryNote.FIELD_ID, noteId)
                val dateTime = ZonedDateTime.now().plusDays(times.toLong())
                val resultMills = dateTime.toInstant().toEpochMilli()
                put(EntryNote.FIELD_TIMESTAMP, resultMills)
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

                    repeat(Random.nextInt(1, 9)) { index ->
                        val imageId = UUID.randomUUID().toString()
                        val imageIndex = Random.nextInt(imageUrls.count())
                        put(EntryNoteImage.FIELD_ID, imageId)
                        put(EntryNoteImage.FIELD_BLOCK_ID, blockId)
                        put(EntryNoteImage.FIELD_URI, imageUrls[imageIndex].first)
                        put(EntryNoteImage.FIELD_RATIO, imageUrls[imageIndex].second)
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
