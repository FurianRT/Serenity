package com.furianrt.mood.internal.entites

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import com.furianrt.mood.api.entities.Mood

@Immutable
internal data class MoodPack(
    @param:DrawableRes val icon: Int,
    val moods: List<Mood>,
)