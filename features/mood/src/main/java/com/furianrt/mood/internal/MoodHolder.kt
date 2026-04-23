package com.furianrt.mood.internal

import com.furianrt.mood.R
import com.furianrt.mood.internal.entites.Mood
import com.furianrt.mood.internal.entites.MoodPack

internal object MoodHolder {

    fun getMoodPacks(): List<MoodPack> = listOf(
        pack5(),
        pack4(),
        raccoonPack(),
        rabbitPack(),
        catPack(),
        dogPack(),
        pack3(),
        pack1(),
        pack2(),
    )

    private fun pack2() = MoodPack(
        icon = R.drawable.mood_pack_2_default,
        moods = listOf(
            Mood(
                id = "mood_pack_2_terrible",
                level = Mood.Level.TERRIBLE,
                icon = R.drawable.mood_pack_2_terrible,
            ),
            Mood(
                id = "mood_pack_2_bad",
                level = Mood.Level.BAD,
                icon = R.drawable.mood_pack_2_bad,
            ),
            Mood(
                id = "mood_pack_2_sad",
                level = Mood.Level.SAD,
                icon = R.drawable.mood_pack_2_sad,
            ),
            Mood(
                id = "mood_pack_2_normal",
                level = Mood.Level.NORMAL,
                icon = R.drawable.mood_pack_2_normal,
            ),
            Mood(
                id = "mood_pack_2_good",
                level = Mood.Level.GOOD,
                icon = R.drawable.mood_pack_2_good,
            ),
            Mood(
                id = "mood_pack_2_perfect",
                level = Mood.Level.PERFECT,
                icon = R.drawable.mood_pack_2_perfect,
            ),
        ),
    )

    private fun pack1() = MoodPack(
        icon = R.drawable.mood_pack_1_default,
        moods = listOf(
            Mood(
                id = "mood_pack_1_terrible",
                level = Mood.Level.TERRIBLE,
                icon = R.drawable.mood_pack_1_terrible,
            ),
            Mood(
                id = "mood_pack_1_bad",
                level = Mood.Level.BAD,
                icon = R.drawable.mood_pack_1_bad,
            ),
            Mood(
                id = "mood_pack_1_sad",
                level = Mood.Level.SAD,
                icon = R.drawable.mood_pack_1_sad,
            ),
            Mood(
                id = "mood_pack_1_normal",
                level = Mood.Level.NORMAL,
                icon = R.drawable.mood_pack_1_normal,
            ),
            Mood(
                id = "mood_pack_1_good",
                level = Mood.Level.GOOD,
                icon = R.drawable.mood_pack_1_good,
            ),
            Mood(
                id = "mood_pack_1_perfect",
                level = Mood.Level.PERFECT,
                icon = R.drawable.mood_pack_1_perfect,
            ),
        ),
    )

    private fun pack3() = MoodPack(
        icon = R.drawable.mood_pack_3_default,
        moods = listOf(
            Mood(
                id = "mood_pack_3_terrible",
                level = Mood.Level.TERRIBLE,
                icon = R.drawable.mood_pack_3_terrible,
            ),
            Mood(
                id = "mood_pack_3_bad",
                level = Mood.Level.BAD,
                icon = R.drawable.mood_pack_3_bad,
            ),
            Mood(
                id = "mood_pack_3_sad",
                level = Mood.Level.SAD,
                icon = R.drawable.mood_pack_3_sad,
            ),
            Mood(
                id = "mood_pack_3_normal",
                level = Mood.Level.NORMAL,
                icon = R.drawable.mood_pack_3_normal,
            ),
            Mood(
                id = "mood_pack_3_good",
                level = Mood.Level.GOOD,
                icon = R.drawable.mood_pack_3_good,
            ),
            Mood(
                id = "mood_pack_3_perfect",
                level = Mood.Level.PERFECT,
                icon = R.drawable.mood_pack_3_perfect,
            ),
        ),
    )

    private fun dogPack() = MoodPack(
        icon = R.drawable.mood_dog_default,
        moods = listOf(
            Mood(
                id = "dog_terrible",
                level = Mood.Level.TERRIBLE,
                icon = R.drawable.mood_dog_terrible,
            ),
            Mood(
                id = "dog_bad",
                level = Mood.Level.BAD,
                icon = R.drawable.mood_dog_bad,
            ),
            Mood(
                id = "dog_sad",
                level = Mood.Level.SAD,
                icon = R.drawable.mood_dog_sad,
            ),
            Mood(
                id = "dog_normal",
                level = Mood.Level.NORMAL,
                icon = R.drawable.mood_dog_normal,
            ),
            Mood(
                id = "dog_good",
                level = Mood.Level.GOOD,
                icon = R.drawable.mood_dog_good,
            ),
            Mood(
                id = "dog_perfect",
                level = Mood.Level.PERFECT,
                icon = R.drawable.mood_dog_perfect,
            ),
        ),
    )

    private fun catPack() = MoodPack(
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
    )

    private fun rabbitPack() = MoodPack(
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
    )

    private fun raccoonPack() = MoodPack(
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
    )

    private fun pack4() = MoodPack(
        icon = R.drawable.mood_pack_4_default,
        moods = listOf(
            Mood(
                id = "mood_pack_4_terrible",
                level = Mood.Level.TERRIBLE,
                icon = R.drawable.mood_pack_4_terrible,
            ),
            Mood(
                id = "mood_pack_4_bad",
                level = Mood.Level.BAD,
                icon = R.drawable.mood_pack_4_bad,
            ),
            Mood(
                id = "mood_pack_4_sad",
                level = Mood.Level.SAD,
                icon = R.drawable.mood_pack_4_sad,
            ),
            Mood(
                id = "mood_pack_4_normal",
                level = Mood.Level.NORMAL,
                icon = R.drawable.mood_pack_4_normal,
            ),
            Mood(
                id = "mood_pack_4_good",
                level = Mood.Level.GOOD,
                icon = R.drawable.mood_pack_4_good,
            ),
            Mood(
                id = "mood_pack_4_perfect",
                level = Mood.Level.PERFECT,
                icon = R.drawable.mood_pack_4_perfect,
            ),
        ),
    )

    private fun pack5() = MoodPack(
        icon = R.drawable.mood_pack_5_default,
        moods = listOf(
            Mood(
                id = "mood_pack_5_terrible",
                level = Mood.Level.TERRIBLE,
                icon = R.drawable.mood_pack_5_terrible,
            ),
            Mood(
                id = "mood_pack_5_bad",
                level = Mood.Level.BAD,
                icon = R.drawable.mood_pack_5_bad,
            ),
            Mood(
                id = "mood_pack_5_sad",
                level = Mood.Level.SAD,
                icon = R.drawable.mood_pack_5_sad,
            ),
            Mood(
                id = "mood_pack_5_normal",
                level = Mood.Level.NORMAL,
                icon = R.drawable.mood_pack_5_normal,
            ),
            Mood(
                id = "mood_pack_5_good",
                level = Mood.Level.GOOD,
                icon = R.drawable.mood_pack_5_good,
            ),
            Mood(
                id = "mood_pack_5_perfect",
                level = Mood.Level.PERFECT,
                icon = R.drawable.mood_pack_5_perfect,
            ),
        ),
    )
}