package com.furianrt.storage.internal.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.room.withTransaction
import androidx.sqlite.db.SupportSQLiteDatabase
import com.furianrt.domain.TransactionsHelper
import com.furianrt.storage.internal.database.SerenityDatabase.Companion.VERSION
import com.furianrt.storage.internal.database.auth.dao.BackupProfileDao
import com.furianrt.storage.internal.database.auth.entities.EntryBackupProfile
import com.furianrt.storage.internal.database.notes.dao.CustomBackgroundDao
import com.furianrt.storage.internal.database.notes.dao.CustomStickerDao
import com.furianrt.storage.internal.database.notes.dao.ImageDao
import com.furianrt.storage.internal.database.notes.dao.LocationDao
import com.furianrt.storage.internal.database.notes.dao.NoteDao
import com.furianrt.storage.internal.database.notes.dao.NoteToTagDao
import com.furianrt.storage.internal.database.notes.dao.StickerDao
import com.furianrt.storage.internal.database.notes.dao.TagDao
import com.furianrt.storage.internal.database.notes.dao.VideoDao
import com.furianrt.storage.internal.database.notes.dao.VoiceDao
import com.furianrt.storage.internal.database.notes.entities.EntryCustomSticker
import com.furianrt.storage.internal.database.notes.entities.EntryNote
import com.furianrt.storage.internal.database.notes.entities.EntryNoteCustomBackground
import com.furianrt.storage.internal.database.notes.entities.EntryNoteImage
import com.furianrt.storage.internal.database.notes.entities.EntryNoteLocation
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
        EntryBackupProfile::class,
        EntryNoteLocation::class,
        EntryNoteCustomBackground::class,
        EntryCustomSticker::class,
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
    abstract fun backupProfileDao(): BackupProfileDao
    abstract fun locationDao(): LocationDao
    abstract fun customBackgroundDao(): CustomBackgroundDao
    abstract fun customStickerDao(): CustomStickerDao

    companion object {
        private const val NAME = "Serenity.db"
        private const val VERSION = 8

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
            .addMigrations(
                MIGRATION_1_2,
                MIGRATION_2_3,
                MIGRATION_3_4,
                MIGRATION_4_5,
                MIGRATION_5_6,
                MIGRATION_6_7,
                MIGRATION_7_8,
            )
            .build()
    }

    override suspend fun startTransaction(block: suspend () -> Unit) = withTransaction(block)
}

private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE Notes ADD COLUMN background_id TEXT")
    }
}
private val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE Notes ADD COLUMN mood_id TEXT")
    }
}
private val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS NoteLocations (
                id TEXT NOT NULL PRIMARY KEY,
                note_id TEXT NOT NULL,
                title TEXT NOT NULL,
                latitude REAL NOT NULL,
                longitude REAL NOT NULL,
                FOREIGN KEY(note_id) 
                    REFERENCES Notes(id) 
                    ON DELETE CASCADE
            )
            """
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS index_NoteLocations_note_id ON NoteLocations(note_id)"
        )
    }
}
private val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE Notes ADD COLUMN background_image_id TEXT")
    }
}
private val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS NoteCustomBackgrounds (
                id TEXT NOT NULL PRIMARY KEY,
                name TEXT NOT NULL,
                uri TEXT NOT NULL,
                primary_color INTEGER NOT NULL,
                accent_color INTEGER NOT NULL,
                is_light INTEGER NOT NULL,
                added_date TEXT NOT NULL,
                is_saved INTEGER NOT NULL,
                is_hidden INTEGER NOT NULL
            )
            """
        )
    }
}
private val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE Notes ADD COLUMN text_alignment INTEGER")
        db.execSQL("ALTER TABLE Notes ADD COLUMN line_height_multiplier REAL")
    }
}
private val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS CustomStickers (
                id TEXT NOT NULL PRIMARY KEY,
                name TEXT NOT NULL,
                uri TEXT NOT NULL,
                ratio REAL NOT NULL,
                added_date TEXT NOT NULL,
                is_saved INTEGER NOT NULL,
                is_hidden INTEGER NOT NULL
            )
            """
        )
    }
}
