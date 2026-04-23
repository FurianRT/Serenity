package com.furianrt.toolspanel.api

import com.furianrt.toolspanel.api.entities.Sticker

interface StickerIconProvider {
    suspend fun getIcon(stickerId: String): Sticker.Icon?
}