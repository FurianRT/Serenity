package com.furianrt.toolspanel.internal.domain

import com.furianrt.toolspanel.internal.entities.BulletEntry
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class BulletListHolder @Inject constructor() {
    fun getBulletListEntries(): List<BulletEntry> = listOf(
        BulletEntry.Dots(isPremium = false)
    )
}