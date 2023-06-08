package com.furianrt.storage.internal

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.furianrt.storage.internal.SerenityDatabase.Companion.VERSION
import com.furianrt.storage.internal.notes.dao.NoteDao
import com.furianrt.storage.internal.notes.entities.DbNote

@Database(
    entities = [DbNote::class],
    version = VERSION,
    exportSchema = false,
)
internal abstract class SerenityDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    companion object {
        private const val NAME = "Serenity.db"
        const val VERSION = 1

        fun createDatabase(
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
