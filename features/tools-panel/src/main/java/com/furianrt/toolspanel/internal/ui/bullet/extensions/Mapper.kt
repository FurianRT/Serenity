package com.furianrt.toolspanel.internal.ui.bullet.extensions

import com.furianrt.notelistui.composables.title.NoteTitleState
import com.furianrt.toolspanel.internal.entities.BulletEntry

internal fun BulletEntry.toBulletListType(): NoteTitleState.BulletListType = when (this) {
    BulletEntry.DOTS -> NoteTitleState.BulletListType.Dots
    BulletEntry.DONE -> NoteTitleState.BulletListType.Done
    BulletEntry.CROSS -> NoteTitleState.BulletListType.Cross
    BulletEntry.STAR -> NoteTitleState.BulletListType.Star
    BulletEntry.CANDLE -> NoteTitleState.BulletListType.Candle
    BulletEntry.HEART -> NoteTitleState.BulletListType.Hart
    BulletEntry.FLOWER -> NoteTitleState.BulletListType.Flower
    BulletEntry.KNIFE -> NoteTitleState.BulletListType.Knife
    BulletEntry.SCROLL -> NoteTitleState.BulletListType.Scroll
    BulletEntry.PENCIL -> NoteTitleState.BulletListType.Pencil
    BulletEntry.SUN -> NoteTitleState.BulletListType.Sun
    BulletEntry.MOON -> NoteTitleState.BulletListType.Moon
}
