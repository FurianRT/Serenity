package com.furianrt.domain.repositories

import com.furianrt.domain.entities.LocalNote
import kotlinx.coroutines.flow.Flow

interface StickersRepository {
    suspend fun insert(noteId: String, stickers: List<LocalNote.Sticker>)
    suspend fun update(stickers: List<LocalNote.Sticker>)
    suspend fun delete(stickers: List<LocalNote.Sticker>)
    fun getStickers(noteId: String): Flow<List<LocalNote.Sticker>>
}