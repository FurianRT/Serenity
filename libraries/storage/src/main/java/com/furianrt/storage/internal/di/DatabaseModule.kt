package com.furianrt.storage.internal.di

import android.content.Context
import com.furianrt.domain.TransactionsHelper
import com.furianrt.storage.internal.database.SerenityDatabase
import com.furianrt.storage.internal.database.notes.dao.ImageDao
import com.furianrt.storage.internal.database.notes.dao.NoteDao
import com.furianrt.storage.internal.database.notes.dao.NoteToTagDao
import com.furianrt.storage.internal.database.notes.dao.TagDao
import com.furianrt.storage.internal.database.notes.dao.VideoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class DatabaseModule {

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
    fun noteToTagDao(database: SerenityDatabase): NoteToTagDao = database.noteToTagDao()

    @Provides
    @Singleton
    fun transactionsHelper(database: SerenityDatabase): TransactionsHelper = database
}
