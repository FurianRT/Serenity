package com.furianrt.toolspanel.api

interface StickerIconProvider {
    suspend fun getIcon(stickerId: String): Int
}