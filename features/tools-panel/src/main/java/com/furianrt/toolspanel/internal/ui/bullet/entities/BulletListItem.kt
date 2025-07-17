package com.furianrt.toolspanel.internal.ui.bullet.entities

internal sealed class BulletListItem(
    open val isPremium: Boolean,
) {
    data class Dots(
        override val isPremium: Boolean,
    ) : BulletListItem(isPremium)
}