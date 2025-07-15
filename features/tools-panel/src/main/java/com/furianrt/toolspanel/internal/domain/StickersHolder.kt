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

    override fun getIcon(stickerId: String): Int? = getStickersPacks()
        .flatMap(StickerPack::stickers)
        .firstOrNull { it.id == stickerId }
        ?.icon

    private fun loadPacks(): ImmutableList<StickerPack> = buildImmutableList {
        add(getPack4())
        add(getPack1())
        add(getPack6())
        add(getPack7())
        add(getPack8())
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

    private fun getPack4(): StickerPack {
        val stickers = buildImmutableList {
            add(Sticker(id = "pack_4_sticker_1", icon = R.drawable.sticker_pack_4_item_1))
            add(Sticker(id = "pack_4_sticker_2", icon = R.drawable.sticker_pack_4_item_2))
            add(Sticker(id = "pack_4_sticker_3", icon = R.drawable.sticker_pack_4_item_3))
            add(Sticker(id = "pack_4_sticker_4", icon = R.drawable.sticker_pack_4_item_4))
            add(Sticker(id = "pack_4_sticker_5", icon = R.drawable.sticker_pack_4_item_5))
            add(Sticker(id = "pack_4_sticker_6", icon = R.drawable.sticker_pack_4_item_6))
            add(Sticker(id = "pack_4_sticker_7", icon = R.drawable.sticker_pack_4_item_7))
            add(Sticker(id = "pack_4_sticker_8", icon = R.drawable.sticker_pack_4_item_8))
            add(Sticker(id = "pack_4_sticker_9", icon = R.drawable.sticker_pack_4_item_9))
            add(Sticker(id = "pack_4_sticker_10", icon = R.drawable.sticker_pack_4_item_10))
            add(Sticker(id = "pack_4_sticker_11", icon = R.drawable.sticker_pack_4_item_11))
            add(Sticker(id = "pack_4_sticker_12", icon = R.drawable.sticker_pack_4_item_12))
            add(Sticker(id = "pack_4_sticker_13", icon = R.drawable.sticker_pack_4_item_13))
            add(Sticker(id = "pack_4_sticker_14", icon = R.drawable.sticker_pack_4_item_14))
            add(Sticker(id = "pack_4_sticker_15", icon = R.drawable.sticker_pack_4_item_15))
            add(Sticker(id = "pack_4_sticker_16", icon = R.drawable.sticker_pack_4_item_16))
            add(Sticker(id = "pack_4_sticker_17", icon = R.drawable.sticker_pack_4_item_17))
            add(Sticker(id = "pack_4_sticker_18", icon = R.drawable.sticker_pack_4_item_18))
            add(Sticker(id = "pack_4_sticker_19", icon = R.drawable.sticker_pack_4_item_19))
            add(Sticker(id = "pack_4_sticker_20", icon = R.drawable.sticker_pack_4_item_20))
            add(Sticker(id = "pack_4_sticker_21", icon = R.drawable.sticker_pack_4_item_21))
            add(Sticker(id = "pack_4_sticker_22", icon = R.drawable.sticker_pack_4_item_22))
            add(Sticker(id = "pack_4_sticker_23", icon = R.drawable.sticker_pack_4_item_23))
            add(Sticker(id = "pack_4_sticker_24", icon = R.drawable.sticker_pack_4_item_24))
            add(Sticker(id = "pack_4_sticker_25", icon = R.drawable.sticker_pack_4_item_25))
            add(Sticker(id = "pack_4_sticker_26", icon = R.drawable.sticker_pack_4_item_26))
            add(Sticker(id = "pack_4_sticker_27", icon = R.drawable.sticker_pack_4_item_27))
            add(Sticker(id = "pack_4_sticker_28", icon = R.drawable.sticker_pack_4_item_28))
            add(Sticker(id = "pack_4_sticker_29", icon = R.drawable.sticker_pack_4_item_29))
            add(Sticker(id = "pack_4_sticker_30", icon = R.drawable.sticker_pack_4_item_30))
            add(Sticker(id = "pack_4_sticker_31", icon = R.drawable.sticker_pack_4_item_31))
            add(Sticker(id = "pack_4_sticker_32", icon = R.drawable.sticker_pack_4_item_32))
            add(Sticker(id = "pack_4_sticker_33", icon = R.drawable.sticker_pack_4_item_33))
            add(Sticker(id = "pack_4_sticker_34", icon = R.drawable.sticker_pack_4_item_34))
            add(Sticker(id = "pack_4_sticker_35", icon = R.drawable.sticker_pack_4_item_35))
            add(Sticker(id = "pack_4_sticker_36", icon = R.drawable.sticker_pack_4_item_36))
            add(Sticker(id = "pack_4_sticker_37", icon = R.drawable.sticker_pack_4_item_37))
            add(Sticker(id = "pack_4_sticker_38", icon = R.drawable.sticker_pack_4_item_38))
            add(Sticker(id = "pack_4_sticker_39", icon = R.drawable.sticker_pack_4_item_39))
            add(Sticker(id = "pack_4_sticker_40", icon = R.drawable.sticker_pack_4_item_40))
            add(Sticker(id = "pack_4_sticker_41", icon = R.drawable.sticker_pack_4_item_41))
            add(Sticker(id = "pack_4_sticker_42", icon = R.drawable.sticker_pack_4_item_42))
            add(Sticker(id = "pack_4_sticker_43", icon = R.drawable.sticker_pack_4_item_43))
        }

        return StickerPack(
            id = "pack_4",
            isPremium = false,
            icon = R.drawable.sticker_pack_4_item_39,
            stickers = stickers,
        )
    }

    private fun getPack6(): StickerPack {
        val stickers = buildImmutableList {
            add(Sticker(id = "pack_6_sticker_1", icon = R.drawable.sticker_pack_6_item_1))
            add(Sticker(id = "pack_6_sticker_2", icon = R.drawable.sticker_pack_6_item_2))
            add(Sticker(id = "pack_6_sticker_3", icon = R.drawable.sticker_pack_6_item_3))
            add(Sticker(id = "pack_6_sticker_4", icon = R.drawable.sticker_pack_6_item_4))
            add(Sticker(id = "pack_6_sticker_5", icon = R.drawable.sticker_pack_6_item_5))
            add(Sticker(id = "pack_6_sticker_6", icon = R.drawable.sticker_pack_6_item_6))
            add(Sticker(id = "pack_6_sticker_7", icon = R.drawable.sticker_pack_6_item_7))
            add(Sticker(id = "pack_6_sticker_8", icon = R.drawable.sticker_pack_6_item_8))
            add(Sticker(id = "pack_6_sticker_9", icon = R.drawable.sticker_pack_6_item_9))
            add(Sticker(id = "pack_6_sticker_10", icon = R.drawable.sticker_pack_6_item_10))
            add(Sticker(id = "pack_6_sticker_11", icon = R.drawable.sticker_pack_6_item_11))
            add(Sticker(id = "pack_6_sticker_12", icon = R.drawable.sticker_pack_6_item_12))
            add(Sticker(id = "pack_6_sticker_13", icon = R.drawable.sticker_pack_6_item_13))
            add(Sticker(id = "pack_6_sticker_14", icon = R.drawable.sticker_pack_6_item_14))
            add(Sticker(id = "pack_6_sticker_15", icon = R.drawable.sticker_pack_6_item_15))
            add(Sticker(id = "pack_6_sticker_16", icon = R.drawable.sticker_pack_6_item_16))
            add(Sticker(id = "pack_6_sticker_17", icon = R.drawable.sticker_pack_6_item_17))
            add(Sticker(id = "pack_6_sticker_18", icon = R.drawable.sticker_pack_6_item_18))
            add(Sticker(id = "pack_6_sticker_19", icon = R.drawable.sticker_pack_6_item_19))
            add(Sticker(id = "pack_6_sticker_20", icon = R.drawable.sticker_pack_6_item_20))
            add(Sticker(id = "pack_6_sticker_21", icon = R.drawable.sticker_pack_6_item_21))
            add(Sticker(id = "pack_6_sticker_22", icon = R.drawable.sticker_pack_6_item_22))
            add(Sticker(id = "pack_6_sticker_23", icon = R.drawable.sticker_pack_6_item_23))
            add(Sticker(id = "pack_6_sticker_24", icon = R.drawable.sticker_pack_6_item_24))
            add(Sticker(id = "pack_6_sticker_25", icon = R.drawable.sticker_pack_6_item_25))
            add(Sticker(id = "pack_6_sticker_26", icon = R.drawable.sticker_pack_6_item_26))
            add(Sticker(id = "pack_6_sticker_27", icon = R.drawable.sticker_pack_6_item_27))
            add(Sticker(id = "pack_6_sticker_28", icon = R.drawable.sticker_pack_6_item_28))
            add(Sticker(id = "pack_6_sticker_29", icon = R.drawable.sticker_pack_6_item_29))
            add(Sticker(id = "pack_6_sticker_30", icon = R.drawable.sticker_pack_6_item_30))
            add(Sticker(id = "pack_6_sticker_31", icon = R.drawable.sticker_pack_6_item_31))
            add(Sticker(id = "pack_6_sticker_32", icon = R.drawable.sticker_pack_6_item_32))
            add(Sticker(id = "pack_6_sticker_33", icon = R.drawable.sticker_pack_6_item_33))
            add(Sticker(id = "pack_6_sticker_34", icon = R.drawable.sticker_pack_6_item_34))
            add(Sticker(id = "pack_6_sticker_35", icon = R.drawable.sticker_pack_6_item_35))
            add(Sticker(id = "pack_6_sticker_36", icon = R.drawable.sticker_pack_6_item_36))
            add(Sticker(id = "pack_6_sticker_37", icon = R.drawable.sticker_pack_6_item_37))
            add(Sticker(id = "pack_6_sticker_38", icon = R.drawable.sticker_pack_6_item_38))
            add(Sticker(id = "pack_6_sticker_39", icon = R.drawable.sticker_pack_6_item_39))
            add(Sticker(id = "pack_6_sticker_40", icon = R.drawable.sticker_pack_6_item_40))
            add(Sticker(id = "pack_6_sticker_41", icon = R.drawable.sticker_pack_6_item_41))
            add(Sticker(id = "pack_6_sticker_42", icon = R.drawable.sticker_pack_6_item_42))
        }

        return StickerPack(
            id = "pack_6",
            isPremium = false,
            icon = R.drawable.sticker_pack_6_item_27,
            stickers = stickers,
        )
    }

    private fun getPack7(): StickerPack {
        val stickers = buildImmutableList {
            add(Sticker(id = "pack_7_sticker_1", icon = R.drawable.sticker_pack_7_item_1))
            add(Sticker(id = "pack_7_sticker_2", icon = R.drawable.sticker_pack_7_item_2))
            add(Sticker(id = "pack_7_sticker_3", icon = R.drawable.sticker_pack_7_item_3))
            add(Sticker(id = "pack_7_sticker_4", icon = R.drawable.sticker_pack_7_item_4))
            add(Sticker(id = "pack_7_sticker_5", icon = R.drawable.sticker_pack_7_item_5))
            add(Sticker(id = "pack_7_sticker_6", icon = R.drawable.sticker_pack_7_item_6))
            add(Sticker(id = "pack_7_sticker_7", icon = R.drawable.sticker_pack_7_item_7))
            add(Sticker(id = "pack_7_sticker_8", icon = R.drawable.sticker_pack_7_item_8))
            add(Sticker(id = "pack_7_sticker_9", icon = R.drawable.sticker_pack_7_item_9))
            add(Sticker(id = "pack_7_sticker_10", icon = R.drawable.sticker_pack_7_item_10))
            add(Sticker(id = "pack_7_sticker_11", icon = R.drawable.sticker_pack_7_item_11))
            add(Sticker(id = "pack_7_sticker_12", icon = R.drawable.sticker_pack_7_item_12))
            add(Sticker(id = "pack_7_sticker_13", icon = R.drawable.sticker_pack_7_item_13))
            add(Sticker(id = "pack_7_sticker_14", icon = R.drawable.sticker_pack_7_item_14))
            add(Sticker(id = "pack_7_sticker_15", icon = R.drawable.sticker_pack_7_item_15))
            add(Sticker(id = "pack_7_sticker_16", icon = R.drawable.sticker_pack_7_item_16))
            add(Sticker(id = "pack_7_sticker_17", icon = R.drawable.sticker_pack_7_item_17))
            add(Sticker(id = "pack_7_sticker_18", icon = R.drawable.sticker_pack_7_item_18))
            add(Sticker(id = "pack_7_sticker_19", icon = R.drawable.sticker_pack_7_item_19))
            add(Sticker(id = "pack_7_sticker_20", icon = R.drawable.sticker_pack_7_item_20))
            add(Sticker(id = "pack_7_sticker_21", icon = R.drawable.sticker_pack_7_item_21))
            add(Sticker(id = "pack_7_sticker_22", icon = R.drawable.sticker_pack_7_item_22))
            add(Sticker(id = "pack_7_sticker_23", icon = R.drawable.sticker_pack_7_item_23))
            add(Sticker(id = "pack_7_sticker_24", icon = R.drawable.sticker_pack_7_item_24))
            add(Sticker(id = "pack_7_sticker_25", icon = R.drawable.sticker_pack_7_item_25))
            add(Sticker(id = "pack_7_sticker_26", icon = R.drawable.sticker_pack_7_item_26))
            add(Sticker(id = "pack_7_sticker_27", icon = R.drawable.sticker_pack_7_item_27))
            add(Sticker(id = "pack_7_sticker_28", icon = R.drawable.sticker_pack_7_item_28))
            add(Sticker(id = "pack_7_sticker_29", icon = R.drawable.sticker_pack_7_item_29))
            add(Sticker(id = "pack_7_sticker_30", icon = R.drawable.sticker_pack_7_item_30))
            add(Sticker(id = "pack_7_sticker_31", icon = R.drawable.sticker_pack_7_item_31))
            add(Sticker(id = "pack_7_sticker_32", icon = R.drawable.sticker_pack_7_item_32))
            add(Sticker(id = "pack_7_sticker_33", icon = R.drawable.sticker_pack_7_item_33))
            add(Sticker(id = "pack_7_sticker_34", icon = R.drawable.sticker_pack_7_item_34))
            add(Sticker(id = "pack_7_sticker_35", icon = R.drawable.sticker_pack_7_item_35))
            add(Sticker(id = "pack_7_sticker_36", icon = R.drawable.sticker_pack_7_item_36))
            add(Sticker(id = "pack_7_sticker_37", icon = R.drawable.sticker_pack_7_item_37))
            add(Sticker(id = "pack_7_sticker_38", icon = R.drawable.sticker_pack_7_item_38))
            add(Sticker(id = "pack_7_sticker_39", icon = R.drawable.sticker_pack_7_item_39))
            add(Sticker(id = "pack_7_sticker_40", icon = R.drawable.sticker_pack_7_item_40))
            add(Sticker(id = "pack_7_sticker_41", icon = R.drawable.sticker_pack_7_item_41))
            add(Sticker(id = "pack_7_sticker_42", icon = R.drawable.sticker_pack_7_item_42))
            add(Sticker(id = "pack_7_sticker_43", icon = R.drawable.sticker_pack_7_item_43))
            add(Sticker(id = "pack_7_sticker_44", icon = R.drawable.sticker_pack_7_item_44))
        }

        return StickerPack(
            id = "pack_7",
            isPremium = false,
            icon = R.drawable.sticker_pack_7_item_40,
            stickers = stickers,
        )
    }

    private fun getPack8(): StickerPack {
        val stickers = buildImmutableList {
            add(Sticker(id = "pack_8_sticker_1", icon = R.drawable.sticker_pack_8_item_1))
            add(Sticker(id = "pack_8_sticker_2", icon = R.drawable.sticker_pack_8_item_2))
            add(Sticker(id = "pack_8_sticker_3", icon = R.drawable.sticker_pack_8_item_3))
            add(Sticker(id = "pack_8_sticker_4", icon = R.drawable.sticker_pack_8_item_4))
            add(Sticker(id = "pack_8_sticker_5", icon = R.drawable.sticker_pack_8_item_5))
            add(Sticker(id = "pack_8_sticker_6", icon = R.drawable.sticker_pack_8_item_6))
            add(Sticker(id = "pack_8_sticker_7", icon = R.drawable.sticker_pack_8_item_7))
            add(Sticker(id = "pack_8_sticker_8", icon = R.drawable.sticker_pack_8_item_8))
            add(Sticker(id = "pack_8_sticker_9", icon = R.drawable.sticker_pack_8_item_9))
            add(Sticker(id = "pack_8_sticker_10", icon = R.drawable.sticker_pack_8_item_10))
            add(Sticker(id = "pack_8_sticker_11", icon = R.drawable.sticker_pack_8_item_11))
            add(Sticker(id = "pack_8_sticker_12", icon = R.drawable.sticker_pack_8_item_12))
            add(Sticker(id = "pack_8_sticker_13", icon = R.drawable.sticker_pack_8_item_13))
            add(Sticker(id = "pack_8_sticker_14", icon = R.drawable.sticker_pack_8_item_14))
            add(Sticker(id = "pack_8_sticker_15", icon = R.drawable.sticker_pack_8_item_15))
            add(Sticker(id = "pack_8_sticker_16", icon = R.drawable.sticker_pack_8_item_16))
            add(Sticker(id = "pack_8_sticker_17", icon = R.drawable.sticker_pack_8_item_17))
            add(Sticker(id = "pack_8_sticker_18", icon = R.drawable.sticker_pack_8_item_18))
            add(Sticker(id = "pack_8_sticker_19", icon = R.drawable.sticker_pack_8_item_19))
            add(Sticker(id = "pack_8_sticker_20", icon = R.drawable.sticker_pack_8_item_20))
            add(Sticker(id = "pack_8_sticker_21", icon = R.drawable.sticker_pack_8_item_21))

        }

        return StickerPack(
            id = "pack_8",
            isPremium = false,
            icon = R.drawable.sticker_pack_8_item_21,
            stickers = stickers,
        )
    }
}