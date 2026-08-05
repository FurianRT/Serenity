package com.furianrt.widgets.di

import com.furianrt.domain.repositories.AppearanceRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
internal interface WidgetEntryPoint {
    fun appearanceRepository(): AppearanceRepository
}