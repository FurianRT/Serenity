package com.furianrt.toolspanel.internal.entities

internal sealed class BulletEntry(
    open val isPremium: Boolean,
) {
    data class Dots(
        override val isPremium: Boolean,
    ) : BulletEntry(isPremium)
}