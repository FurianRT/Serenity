package com.furianrt.domain.repositories

import com.furianrt.domain.entities.CustomSticker
import com.furianrt.domain.entities.LocalNote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface StickersRepository {
    suspend fun insert(noteId: String, stickers: List<LocalNote.Sticker>)
    suspend fun update(stickers: List<LocalNote.Sticker>)
    suspend fun delete(stickers: List<LocalNote.Sticker>)
    fun getStickers(noteId: String): Flow<List<LocalNote.Sticker>>

    suspend fun upsertCustomStickers(
        stickers: List<CustomSticker>,
        updateFile: Boolean = true,
    )

    suspend fun deleteCustomSticker(
        sticker: CustomSticker,
        updateHiddenFlag: Boolean = true,
    )

    fun getNotHiddenCustomStickers(): Flow<List<CustomSticker>>
    fun getHiddenCustomStickers(): Flow<List<CustomSticker>>
    fun getAllCustomStickers(): Flow<List<CustomSticker>>

    companion object {
        fun mock(): StickersRepository = object : StickersRepository {
            override fun getAllCustomStickers(): Flow<List<CustomSticker>> = flowOf(emptyList())
            override fun getNotHiddenCustomStickers(): Flow<List<CustomSticker>> =
                flowOf(emptyList())

            override fun getHiddenCustomStickers(): Flow<List<CustomSticker>> = flowOf(emptyList())
            override suspend fun deleteCustomSticker(
                sticker: CustomSticker,
                updateHiddenFlag: Boolean,
            ) = Unit

            override suspend fun delete(stickers: List<LocalNote.Sticker>) = Unit
            override fun getStickers(noteId: String): Flow<List<LocalNote.Sticker>> =
                flowOf(emptyList())

            override suspend fun insert(noteId: String, stickers: List<LocalNote.Sticker>) = Unit
            override suspend fun update(stickers: List<LocalNote.Sticker>) = Unit
            override suspend fun upsertCustomStickers(
                stickers: List<CustomSticker>,
                updateFile: Boolean,
            ) = Unit
        }
    }
}