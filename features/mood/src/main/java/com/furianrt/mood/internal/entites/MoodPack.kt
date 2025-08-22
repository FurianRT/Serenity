package com.furianrt.mood.internal.entites

import androidx.annotation.DrawableRes

internal data class MoodPack(
    @param:DrawableRes val icon: Int,
    val moods: List<Mood>,
)