package com.furianrt.toolspanel.internal.entities

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import com.furianrt.toolspanel.api.entities.Sticker
import kotlinx.collections.immutable.ImmutableList

@Immutable
internal class StickerPack(
    val id: String,
    val isPremium: Boolean,
    @param:DrawableRes val icon: Int,
    val stickers: ImmutableList<Sticker>,
)
