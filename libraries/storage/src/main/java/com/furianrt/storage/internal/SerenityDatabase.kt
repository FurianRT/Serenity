package com.furianrt.storage.internal

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.furianrt.storage.internal.SerenityDatabase.Companion.VERSION
import com.furianrt.storage.internal.notes.dao.ImageDao
import com.furianrt.storage.internal.notes.dao.NoteDao
import com.furianrt.storage.internal.notes.dao.NoteToTagDao
import com.furianrt.storage.internal.notes.dao.TagDao
import com.furianrt.storage.internal.notes.entities.EntryContentBlock
import com.furianrt.storage.internal.notes.entities.EntryNote
import com.furianrt.storage.internal.notes.entities.EntryNoteImage
import com.furianrt.storage.internal.notes.entities.EntryNoteTag
import com.furianrt.storage.internal.notes.entities.EntryNoteTitle
import com.furianrt.storage.internal.notes.entities.EntryNoteToTag

@Database(
    entities = [
        EntryNote::class,
        EntryNoteTitle::class,
        EntryNoteImage::class,
        EntryNoteTag::class,
        EntryNoteToTag::class,
        EntryContentBlock::class,
    ],
    version = VERSION,
    exportSchema = false,
)
internal abstract class SerenityDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao
    abstract fun tagDao(): TagDao
    abstract fun imageDao(): ImageDao
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
}
