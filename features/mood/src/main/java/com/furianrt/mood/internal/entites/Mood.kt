package com.furianrt.mood.internal.entites

import androidx.annotation.DrawableRes

data class Mood(
    val id: String,
    val level: Level,
    @param:DrawableRes val icon: Int,
) {
    enum class Level {
        TERRIBLE,
        BAD,
        SAD,
        NORMAL,
        GOOD,
        PERFECT,
    }
}