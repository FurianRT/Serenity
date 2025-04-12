package com.furianrt.storage.internal.di

import android.content.Context
import com.furianrt.domain.TransactionsHelper
import com.furianrt.domain.repositories.AppearanceRepository
import com.furianrt.domain.repositories.DeviceInfoRepository
import com.furianrt.domain.repositories.MediaRepository
import com.furianrt.domain.repositories.NotesRepository
import com.furianrt.domain.repositories.ProfileRepository
import com.furianrt.domain.repositories.StickersRepository
import com.furianrt.domain.repositories.TagsRepository
import com.furianrt.storage.internal.database.SerenityDatabase
import com.furianrt.storage.internal.database.auth.dao.BackupProfileDao
import com.furianrt.storage.internal.database.notes.dao.ImageDao
import com.furianrt.storage.internal.database.notes.dao.NoteDao
import com.furianrt.storage.internal.database.notes.dao.NoteToTagDao
import com.furianrt.storage.internal.database.notes.dao.StickerDao
import com.furianrt.storage.internal.database.notes.dao.TagDao
import com.furianrt.storage.internal.database.notes.dao.VideoDao
import com.furianrt.storage.internal.database.notes.dao.VoiceDao
import com.furianrt.storage.internal.repositories.AppearanceRepositoryImp
import com.furianrt.storage.internal.repositories.DeviceInfoRepositoryImp
import com.furianrt.storage.internal.repositories.MediaRepositoryImp
import com.furianrt.storage.internal.repositories.NotesRepositoryImp
import com.furianrt.storage.internal.repositories.ProfileRepositoryImp
import com.furianrt.storage.internal.repositories.StickersRepositoryImp
import com.furianrt.storage.internal.repositories.TagsRepositoryImp
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface DatabaseModule {

    @Binds
    @Singleton
    fun notesRepository(imp: NotesRepositoryImp): NotesRepository

    @Binds
    @Singleton
    fun tagsRepository(imp: TagsRepositoryImp): TagsRepository

    @Binds
    @Singleton
    fun mediaRepository(imp: MediaRepositoryImp): MediaRepository

    @Binds
    @Singleton
    fun appearanceRepository(imp: AppearanceRepositoryImp): AppearanceRepository

    @Binds
    @Singleton
    fun stickersRepository(imp: StickersRepositoryImp): StickersRepository

    @Binds
    @Singleton
    fun profileRepository(imp: ProfileRepositoryImp): ProfileRepository

    @Binds
    @Singleton
    fun deviceInfoRepository(imp: DeviceInfoRepositoryImp): DeviceInfoRepository

    companion object {
        @Provides
        @Singleton
        fun provideDatabase(
            @ApplicationContext context: Context,
        ): SerenityDatabase = SerenityDatabase.create(context)

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
        fun voiceDao(database: SerenityDatabase): VoiceDao = database.voiceDao()

        @Provides
        @Singleton
        fun noteToTagDao(database: SerenityDatabase): NoteToTagDao = database.noteToTagDao()

        @Provides
        @Singleton
        fun stickerDao(database: SerenityDatabase): StickerDao = database.stickerDao()

        @Provides
        @Singleton
        fun backupProfileDao(
            database: SerenityDatabase,
        ): BackupProfileDao = database.backupProfileDao()

        @Provides
        @Singleton
        fun transactionsHelper(database: SerenityDatabase): TransactionsHelper = database
    }
}
