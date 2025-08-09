package com.furianrt.toolspanel.internal.di

import com.furianrt.toolspanel.api.NoteBackgroundProvider
import com.furianrt.toolspanel.api.StickerIconProvider
import com.furianrt.toolspanel.internal.domain.NoteBackgroundHolder
import com.furianrt.toolspanel.internal.domain.StickersHolder
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface ToolsPanelModule {

    @Binds
    @Singleton
    fun stickerIconProvider(imp: StickersHolder): StickerIconProvider

    @Binds
    @Singleton
    fun noteBackgroundProvider(imp: NoteBackgroundHolder): NoteBackgroundProvider
}