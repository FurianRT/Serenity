package com.furianrt.widgets.di

import com.furianrt.domain.WidgetsUpdater
import com.furianrt.widgets.managers.WidgetsUpdaterImp
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface WidgetsModule {
    @Binds
    @Singleton
    fun widgetsUpdater(imp: WidgetsUpdaterImp): WidgetsUpdater
}