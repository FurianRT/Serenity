package com.furianrt.mood.api

import com.furianrt.mood.R
import com.furianrt.mood.api.entities.Mood
import com.furianrt.mood.internal.entites.MoodPack
import kotlin.collections.get

object MoodHolder {

    internal val moodPacks: List<MoodPack> = listOf(
        pack6(),
        pack7(),
        pack4(),
        pack5(),
        raccoonPack(),
        rabbitPack(),
        catPack(),
        dogPack(),
        pack3(),
        pack1(),
        pack2(),
    )

    private val moodMap: Map<String, Mood> by lazy {
        moodPacks.flatMap(MoodPack::moods).associateBy(Mood::id)
    }

    fun findMood(id: String?): Mood? = moodMap[id]

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

    private fun pack6() = MoodPack(
        icon = R.drawable.mood_pack_6_default,
        moods = listOf(
            Mood(
                id = "mood_pack_6_terrible",
                level = Mood.Level.TERRIBLE,
                icon = R.drawable.mood_pack_6_terrible,
            ),
            Mood(
                id = "mood_pack_6_bad",
                level = Mood.Level.BAD,
                icon = R.drawable.mood_pack_6_bad,
            ),
            Mood(
                id = "mood_pack_6_sad",
                level = Mood.Level.SAD,
                icon = R.drawable.mood_pack_6_sad,
            ),
            Mood(
                id = "mood_pack_6_normal",
                level = Mood.Level.NORMAL,
                icon = R.drawable.mood_pack_6_normal,
            ),
            Mood(
                id = "mood_pack_6_good",
                level = Mood.Level.GOOD,
                icon = R.drawable.mood_pack_6_good,
            ),
            Mood(
                id = "mood_pack_6_perfect",
                level = Mood.Level.PERFECT,
                icon = R.drawable.mood_pack_6_perfect,
            ),
        ),
    )

    private fun pack7() = MoodPack(
        icon = R.drawable.mood_pack_7_default,
        moods = listOf(
            Mood(
                id = "mood_pack_7_terrible",
                level = Mood.Level.TERRIBLE,
                icon = R.drawable.mood_pack_7_terrible,
            ),
            Mood(
                id = "mood_pack_7_bad",
                level = Mood.Level.BAD,
                icon = R.drawable.mood_pack_7_bad,
            ),
            Mood(
                id = "mood_pack_7_sad",
                level = Mood.Level.SAD,
                icon = R.drawable.mood_pack_7_sad,
            ),
            Mood(
                id = "mood_pack_7_normal",
                level = Mood.Level.NORMAL,
                icon = R.drawable.mood_pack_7_normal,
            ),
            Mood(
                id = "mood_pack_7_good",
                level = Mood.Level.GOOD,
                icon = R.drawable.mood_pack_7_good,
            ),
            Mood(
                id = "mood_pack_7_perfect",
                level = Mood.Level.PERFECT,
                icon = R.drawable.mood_pack_7_perfect,
            ),
        ),
    )
}