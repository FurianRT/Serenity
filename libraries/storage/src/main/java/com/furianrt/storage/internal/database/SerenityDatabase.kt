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
import com.furianrt.storage.internal.database.notes.dao.TagDao
import com.furianrt.storage.internal.database.notes.dao.VideoDao
import com.furianrt.storage.internal.database.notes.entities.EntryNote
import com.furianrt.storage.internal.database.notes.entities.EntryNoteImage
import com.furianrt.storage.internal.database.notes.entities.EntryNoteTag
import com.furianrt.storage.internal.database.notes.entities.EntryNoteToTag
import com.furianrt.storage.internal.database.notes.entities.EntryNoteVideo

@Database(
    entities = [
        EntryNote::class,
        EntryNoteImage::class,
        EntryNoteVideo::class,
        EntryNoteTag::class,
        EntryNoteToTag::class,
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
    abstract fun noteToTagDao(): NoteToTagDao

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
