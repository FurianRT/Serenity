package com.furianrt.storage.internal.di

import com.furianrt.storage.api.repositories.NotesRepository
import com.furianrt.storage.internal.notes.repositories.NotesRepositoryImp
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface RepositoriesModule {

    @Binds
    fun notesRepository(imp: NotesRepositoryImp): NotesRepository
}
