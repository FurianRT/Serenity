package com.furianrt.storage.internal.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.withTransaction
import androidx.sqlite.db.SupportSQLiteDatabase
import com.furianrt.domain.TransactionsHelper
import com.furianrt.storage.internal.database.SerenityDatabase.Companion.VERSION
import com.furianrt.storage.internal.database.notes.dao.ImageDao
import com.furianrt.storage.internal.database.notes.dao.NoteDao
import com.furianrt.storage.internal.database.notes.dao.NoteToTagDao
import com.furianrt.storage.internal.database.notes.dao.StickerDao
import com.furianrt.storage.internal.database.notes.dao.TagDao
import com.furianrt.storage.internal.database.notes.dao.VideoDao
import com.furianrt.storage.internal.database.notes.dao.VoiceDao
import com.furianrt.storage.internal.database.notes.entities.EntryNote
import com.furianrt.storage.internal.database.notes.entities.EntryNoteImage
import com.furianrt.storage.internal.database.notes.entities.EntryNoteSticker
import com.furianrt.storage.internal.database.notes.entities.EntryNoteTag
import com.furianrt.storage.internal.database.notes.entities.EntryNoteToTag
import com.furianrt.storage.internal.database.notes.entities.EntryNoteVideo
import com.furianrt.storage.internal.database.notes.entities.EntryNoteVoice

@Database(
    entities = [
        EntryNote::class,
        EntryNoteImage::class,
        EntryNoteVideo::class,
        EntryNoteTag::class,
        EntryNoteToTag::class,
        EntryNoteVoice::class,
        EntryNoteSticker::class,
    ],
    version = VERSION,
    exportSchema = false,
)
@TypeConverters(TypeConverter::class)
internal abstract class SerenityDatabase : RoomDatabase(), TransactionsHelper {

    abstract fun noteDao(): NoteDao
    abstract fun tagDao(): TagDao
    abstract fun imageDao(): ImageDao
    abstract fun videoDao(): VideoDao
    abstract fun voiceDao(): VoiceDao
    abstract fun noteToTagDao(): NoteToTagDao
    abstract fun stickerDao(): StickerDao

    companion object {
        private const val NAME = "Serenity.db"
        const val VERSION = 1

        fun create(
            context: Context,
            callback: (db: SupportSQLiteDatabase) -> Unit = {},
        ) = Room.databaseBuilder(context, SerenityDatabase::class.java, NAME)
            .addCallback(
                object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        callback(db)
                    }
                },
            )
            .build()
    }

    override suspend fun startTransaction(block: suspend () -> Unit) = withTransaction(block)
}
