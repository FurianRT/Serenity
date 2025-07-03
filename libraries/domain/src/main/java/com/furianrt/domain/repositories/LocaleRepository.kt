package com.furianrt.domain.repositories

import com.furianrt.domain.entities.AppLocale
import kotlinx.coroutines.flow.Flow

interface LocaleRepository {
    fun getSelectedLocale(): Flow<AppLocale>
    fun setSelectedLocale(locale: AppLocale)
    fun getLocaleList(): Flow<List<AppLocale>>
}