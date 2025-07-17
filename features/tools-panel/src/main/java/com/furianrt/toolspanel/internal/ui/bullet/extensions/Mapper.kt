package com.furianrt.toolspanel.internal.ui.bullet.extensions

import com.furianrt.notelistui.composables.title.NoteTitleState
import com.furianrt.toolspanel.internal.entities.BulletEntry
import com.furianrt.toolspanel.internal.ui.bullet.entities.BulletListItem

internal fun BulletEntry.toBulletListItem() = when (this) {
    is BulletEntry.Dots -> BulletListItem.Dots(isPremium)
}

internal fun BulletListItem.toBulletListType(): NoteTitleState.BulletListType = when(this) {
    is BulletListItem.Dots -> NoteTitleState.BulletListType.Dots
}