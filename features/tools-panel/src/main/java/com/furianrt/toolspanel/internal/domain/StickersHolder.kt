package com.furianrt.toolspanel.internal.domain

import com.furianrt.core.buildImmutableList
import com.furianrt.toolspanel.R
import com.furianrt.toolspanel.api.StickerIconProvider
import com.furianrt.toolspanel.api.entities.Sticker
import com.furianrt.toolspanel.internal.entities.StickerPack
import kotlinx.collections.immutable.ImmutableList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class StickersHolder @Inject constructor() : StickerIconProvider {
    private var cache: ImmutableList<StickerPack>? = null

    fun getStickersPacks(): ImmutableList<StickerPack> {
        return cache ?: loadPacks().also { cache = it }
    }

    override fun getIcon(stickerId: String): Int = getStickersPacks()
        .flatMap(StickerPack::stickers)
        .first { it.id == stickerId }
        .icon

    private fun loadPacks(): ImmutableList<StickerPack> = buildImmutableList {
        add(getPack1())
        add(getPack1())
        add(getPack1())
        add(getPack1())
        add(getPack1())
        add(getPack1())
        add(getPack1())
        add(getPack1())
    }

    private fun getPack1(): StickerPack {
        val stickers = buildImmutableList {
            add(Sticker(id = "pack_1_sticker_1", icon = R.drawable.sticker_pack_1_item_1))
            add(Sticker(id = "pack_1_sticker_2", icon = R.drawable.sticker_pack_1_item_2))
            add(Sticker(id = "pack_1_sticker_3", icon = R.drawable.sticker_pack_1_item_3))
            add(Sticker(id = "pack_1_sticker_4", icon = R.drawable.sticker_pack_1_item_4))
            add(Sticker(id = "pack_1_sticker_5", icon = R.drawable.sticker_pack_1_item_5))
            add(Sticker(id = "pack_1_sticker_6", icon = R.drawable.sticker_pack_1_item_6))
            add(Sticker(id = "pack_1_sticker_7", icon = R.drawable.sticker_pack_1_item_7))
            add(Sticker(id = "pack_1_sticker_8", icon = R.drawable.sticker_pack_1_item_8))
            add(Sticker(id = "pack_1_sticker_9", icon = R.drawable.sticker_pack_1_item_9))
            add(Sticker(id = "pack_1_sticker_10", icon = R.drawable.sticker_pack_1_item_10))
            add(Sticker(id = "pack_1_sticker_11", icon = R.drawable.sticker_pack_1_item_11))
            add(Sticker(id = "pack_1_sticker_12", icon = R.drawable.sticker_pack_1_item_12))
            add(Sticker(id = "pack_1_sticker_13", icon = R.drawable.sticker_pack_1_item_13))
            add(Sticker(id = "pack_1_sticker_14", icon = R.drawable.sticker_pack_1_item_14))
            add(Sticker(id = "pack_1_sticker_15", icon = R.drawable.sticker_pack_1_item_15))
            add(Sticker(id = "pack_1_sticker_16", icon = R.drawable.sticker_pack_1_item_16))
            add(Sticker(id = "pack_1_sticker_17", icon = R.drawable.sticker_pack_1_item_17))
            add(Sticker(id = "pack_1_sticker_18", icon = R.drawable.sticker_pack_1_item_18))
            add(Sticker(id = "pack_1_sticker_19", icon = R.drawable.sticker_pack_1_item_19))
            add(Sticker(id = "pack_1_sticker_20", icon = R.drawable.sticker_pack_1_item_20))
            add(Sticker(id = "pack_1_sticker_21", icon = R.drawable.sticker_pack_1_item_21))
            add(Sticker(id = "pack_1_sticker_22", icon = R.drawable.sticker_pack_1_item_22))
            add(Sticker(id = "pack_1_sticker_23", icon = R.drawable.sticker_pack_1_item_23))
            add(Sticker(id = "pack_1_sticker_24", icon = R.drawable.sticker_pack_1_item_24))
            add(Sticker(id = "pack_1_sticker_25", icon = R.drawable.sticker_pack_1_item_25))
            add(Sticker(id = "pack_1_sticker_26", icon = R.drawable.sticker_pack_1_item_26))
            add(Sticker(id = "pack_1_sticker_27", icon = R.drawable.sticker_pack_1_item_27))
            add(Sticker(id = "pack_1_sticker_28", icon = R.drawable.sticker_pack_1_item_28))
            add(Sticker(id = "pack_1_sticker_29", icon = R.drawable.sticker_pack_1_item_29))
            add(Sticker(id = "pack_1_sticker_30", icon = R.drawable.sticker_pack_1_item_30))
            add(Sticker(id = "pack_1_sticker_31", icon = R.drawable.sticker_pack_1_item_31))
            add(Sticker(id = "pack_1_sticker_32", icon = R.drawable.sticker_pack_1_item_32))
            add(Sticker(id = "pack_1_sticker_33", icon = R.drawable.sticker_pack_1_item_33))
            add(Sticker(id = "pack_1_sticker_34", icon = R.drawable.sticker_pack_1_item_34))
            add(Sticker(id = "pack_1_sticker_35", icon = R.drawable.sticker_pack_1_item_35))
            add(Sticker(id = "pack_1_sticker_36", icon = R.drawable.sticker_pack_1_item_36))
            add(Sticker(id = "pack_1_sticker_37", icon = R.drawable.sticker_pack_1_item_37))
            add(Sticker(id = "pack_1_sticker_38", icon = R.drawable.sticker_pack_1_item_38))
            add(Sticker(id = "pack_1_sticker_39", icon = R.drawable.sticker_pack_1_item_39))
            add(Sticker(id = "pack_1_sticker_40", icon = R.drawable.sticker_pack_1_item_40))
            add(Sticker(id = "pack_1_sticker_41", icon = R.drawable.sticker_pack_1_item_41))
            add(Sticker(id = "pack_1_sticker_42", icon = R.drawable.sticker_pack_1_item_42))
            add(Sticker(id = "pack_1_sticker_43", icon = R.drawable.sticker_pack_1_item_43))
            add(Sticker(id = "pack_1_sticker_44", icon = R.drawable.sticker_pack_1_item_44))
            add(Sticker(id = "pack_1_sticker_45", icon = R.drawable.sticker_pack_1_item_45))
            add(Sticker(id = "pack_1_sticker_46", icon = R.drawable.sticker_pack_1_item_46))
            add(Sticker(id = "pack_1_sticker_47", icon = R.drawable.sticker_pack_1_item_47))
            add(Sticker(id = "pack_1_sticker_48", icon = R.drawable.sticker_pack_1_item_48))
            add(Sticker(id = "pack_1_sticker_49", icon = R.drawable.sticker_pack_1_item_49))
            add(Sticker(id = "pack_1_sticker_50", icon = R.drawable.sticker_pack_1_item_50))
            add(Sticker(id = "pack_1_sticker_51", icon = R.drawable.sticker_pack_1_item_51))
        }

        return StickerPack(
            id = "pack_1",
            isPremium = false,
            icon = R.drawable.sticker_pack_1_item_1,
            stickers = stickers,
        )
    }
}