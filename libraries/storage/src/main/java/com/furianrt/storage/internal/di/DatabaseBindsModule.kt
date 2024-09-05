package com.furianrt.storage.internal.di

import com.furianrt.storage.api.repositories.MediaRepository
import com.furianrt.storage.api.repositories.NotesRepository
import com.furianrt.storage.api.repositories.TagsRepository
import com.furianrt.storage.internal.repositories.MediaRepositoryImp
import com.furianrt.storage.internal.repositories.NotesRepositoryImp
import com.furianrt.storage.internal.repositories.TagsRepositoryImp
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface DatabaseBindsModule {

    @Binds
    @Singleton
    fun notesRepository(imp: NotesRepositoryImp): NotesRepository

    @Binds
    @Singleton
    fun tagsRepository(imp: TagsRepositoryImp): TagsRepository

    @Binds
    @Singleton
    fun mediaRepository(imp: MediaRepositoryImp): MediaRepository
}