package com.furianrt.mood.internal

import com.furianrt.mood.R
import com.furianrt.mood.internal.entites.Mood
import com.furianrt.mood.internal.entites.MoodPack

internal object MoodHolder {

    fun getMoodPacks(): List<MoodPack> = listOf(
        MoodPack(
            icon = R.drawable.mood_raccoon_default,
            moods = listOf(
                Mood(
                    id = "raccoon_terrible",
                    level = Mood.Level.TERRIBLE,
                    icon = R.drawable.mood_raccoon_terrible,
                ),
                Mood(
                    id = "raccoon_bad",
                    level = Mood.Level.BAD,
                    icon = R.drawable.mood_raccoon_bad,
                ),
                Mood(
                    id = "raccoon_sad",
                    level = Mood.Level.SAD,
                    icon = R.drawable.mood_raccoon_sad,
                ),
                Mood(
                    id = "raccoon_normal",
                    level = Mood.Level.NORMAL,
                    icon = R.drawable.mood_raccoon_normal,
                ),
                Mood(
                    id = "raccoon_good",
                    level = Mood.Level.GOOD,
                    icon = R.drawable.mood_raccoon_good,
                ),
                Mood(
                    id = "raccoon_perfect",
                    level = Mood.Level.PERFECT,
                    icon = R.drawable.mood_raccoon_perfect,
                ),
            ),
        ),
        MoodPack(
            icon = R.drawable.mood_rabbit_default,
            moods = listOf(
                Mood(
                    id = "rabbit_terrible",
                    level = Mood.Level.TERRIBLE,
                    icon = R.drawable.mood_rabbit_terrible,
                ),
                Mood(
                    id = "rabbit_bad",
                    level = Mood.Level.BAD,
                    icon = R.drawable.mood_rabbit_bad,
                ),
                Mood(
                    id = "rabbit_sad",
                    level = Mood.Level.SAD,
                    icon = R.drawable.mood_rabbit_sad,
                ),
                Mood(
                    id = "rabbit_normal",
                    level = Mood.Level.NORMAL,
                    icon = R.drawable.mood_rabbit_normal,
                ),
                Mood(
                    id = "rabbit_good",
                    level = Mood.Level.GOOD,
                    icon = R.drawable.mood_rabbit_good,
                ),
                Mood(
                    id = "rabbit_perfect",
                    level = Mood.Level.PERFECT,
                    icon = R.drawable.mood_rabbit_perfect,
                ),
            ),
        ),
        MoodPack(
            icon = R.drawable.mood_cat_default,
            moods = listOf(
                Mood(
                    id = "cat_terrible",
                    level = Mood.Level.TERRIBLE,
                    icon = R.drawable.mood_cat_terrible,
                ),
                Mood(
                    id = "cat_bad",
                    level = Mood.Level.BAD,
                    icon = R.drawable.mood_cat_bad,
                ),
                Mood(
                    id = "cat_sad",
                    level = Mood.Level.SAD,
                    icon = R.drawable.mood_cat_sad,
                ),
                Mood(
                    id = "cat_normal",
                    level = Mood.Level.NORMAL,
                    icon = R.drawable.mood_cat_normal,
                ),
                Mood(
                    id = "cat_good",
                    level = Mood.Level.GOOD,
                    icon = R.drawable.mood_cat_good,
                ),
                Mood(
                    id = "cat_perfect",
                    level = Mood.Level.PERFECT,
                    icon = R.drawable.mood_cat_perfect,
                ),
            ),
        ),
    )
}