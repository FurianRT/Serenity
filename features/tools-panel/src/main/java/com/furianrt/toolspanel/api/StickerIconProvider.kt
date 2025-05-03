package com.furianrt.toolspanel.api

interface StickerIconProvider {
    fun getIcon(stickerId: String): Int
}