package com.furianrt.storage.internal.di

import com.furianrt.storage.api.repositories.ImagesRepository
import com.furianrt.storage.api.repositories.NotesRepository
import com.furianrt.storage.api.repositories.TagsRepository
import com.furianrt.storage.internal.database.notes.repositories.ImagesRepositoryImp
import com.furianrt.storage.internal.database.notes.repositories.NotesRepositoryImp
import com.furianrt.storage.internal.database.notes.repositories.TagsRepositoryImp
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface DatabaseBindsModule {

    @Binds
    fun notesRepository(imp: NotesRepositoryImp): NotesRepository

    @Binds
    fun tagsRepository(imp: TagsRepositoryImp): TagsRepository

    @Binds
    fun imagesRepository(imp: ImagesRepositoryImp): ImagesRepository
}