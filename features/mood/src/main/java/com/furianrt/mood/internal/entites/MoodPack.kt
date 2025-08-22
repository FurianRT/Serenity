package com.furianrt.mood.internal.entites

import androidx.annotation.DrawableRes

internal data class MoodPack(
    @DrawableRes val icon: Int,
    val moods: List<Mood>,
)