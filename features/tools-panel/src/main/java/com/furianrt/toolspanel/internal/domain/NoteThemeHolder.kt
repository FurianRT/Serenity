package com.furianrt.toolspanel.internal.domain

import com.furianrt.notelistui.entities.UiNoteBackground
import com.furianrt.notelistui.entities.UiNoteBackgroundImage
import com.furianrt.notelistui.entities.UiNoteTheme
import com.furianrt.toolspanel.R
import com.furianrt.toolspanel.api.NoteThemeProvider
import com.furianrt.uikit.entities.UiThemeColor
import com.furianrt.uikit.entities.colorScheme
import javax.inject.Inject

internal class NoteThemesHolder @Inject constructor() : NoteThemeProvider {

    override suspend fun findTheme(colorId: String?, imageId: String?): UiNoteTheme? {
        val pictureTheme = getPictureThemes()
            .find { it.colorId == colorId && it.imageId == imageId }
        val pattern = getPatternImages().find { it.id == imageId }
        val solidTheme = getSolidThemes().find { it.colorId == colorId }
        return when {
            pictureTheme != null -> pictureTheme
            pattern != null -> UiNoteTheme.Image.Pattern(
                color = solidTheme?.color,
                image = pattern,
            )

            solidTheme != null -> solidTheme

            else -> null
        }
    }

    override fun getSolidThemes(): List<UiNoteTheme.Solid> {
        return getDarkSolidThemes() + getLightSolidThemes()
    }

    override fun getPictureThemes(): List<UiNoteTheme.Image.Picture> = listOf(
        UiNoteTheme.Image.Picture(
            color = UiNoteBackground(
                id = "picture_color_0",
                isLight = false,
                colorScheme = UiThemeColor.TOTAL_BLACK.colorScheme,
            ),
            image = UiNoteBackgroundImage(
                id = "picture_image_0",
                resId = R.drawable.background_picture_0,
                scaleType = UiNoteBackgroundImage.ScaleType.CROP_ALIGN_BOTTOM,
            )
        ),
        UiNoteTheme.Image.Picture(
            color = UiNoteBackground(
                id = "picture_color_5",
                isLight = false,
                colorScheme = UiThemeColor.AVATAR_BLUE.colorScheme,
            ),
            image = UiNoteBackgroundImage(
                id = "picture_image_5",
                resId = R.drawable.background_picture_5,
                scaleType = UiNoteBackgroundImage.ScaleType.FILL,
            )
        ),
        UiNoteTheme.Image.Picture(
            color = UiNoteBackground(
                id = "picture_color_3",
                isLight = false,
                colorScheme = UiThemeColor.BLUE_NIGHT.colorScheme,
            ),
            image = UiNoteBackgroundImage(
                id = "picture_image_3",
                resId = R.drawable.background_picture_3,
                scaleType = UiNoteBackgroundImage.ScaleType.CROP_ALIGN_BOTTOM,
            )
        ),
        UiNoteTheme.Image.Picture(
            color = UiNoteBackground(
                id = "picture_color_11",
                isLight = false,
                colorScheme = UiThemeColor.BLUE_NIGHT_2.colorScheme,
            ),
            image = UiNoteBackgroundImage(
                id = "picture_image_11",
                resId = R.drawable.background_picture_11,
                scaleType = UiNoteBackgroundImage.ScaleType.FILL,
            )
        ),
        UiNoteTheme.Image.Picture(
            color = UiNoteBackground(
                id = "picture_color_4",
                isLight = false,
                colorScheme = UiThemeColor.GREEN_FOREST.colorScheme,
            ),
            image = UiNoteBackgroundImage(
                id = "picture_image_4",
                resId = R.drawable.background_picture_4,
                scaleType = UiNoteBackgroundImage.ScaleType.CROP_ALIGN_BOTTOM,
            )
        ),
        UiNoteTheme.Image.Picture(
            color = UiNoteBackground(
                id = "picture_color_12",
                isLight = false,
                colorScheme = UiThemeColor.TOTAL_BLACK.colorScheme,
            ),
            image = UiNoteBackgroundImage(
                id = "picture_image_12",
                resId = R.drawable.background_picture_12,
                scaleType = UiNoteBackgroundImage.ScaleType.FILL,
            )
        ),
        UiNoteTheme.Image.Picture(
            color = UiNoteBackground(
                id = "picture_color_10",
                isLight = false,
                colorScheme = UiThemeColor.TOTAL_BLACK.colorScheme,
            ),
            image = UiNoteBackgroundImage(
                id = "picture_image_10",
                resId = R.drawable.background_picture_10,
                scaleType = UiNoteBackgroundImage.ScaleType.FILL,
            )
        ),
        UiNoteTheme.Image.Picture(
            color = UiNoteBackground(
                id = "picture_color_6",
                isLight = false,
                colorScheme = UiThemeColor.FIREFLY_OLIVE.colorScheme,
            ),
            image = UiNoteBackgroundImage(
                id = "picture_image_6",
                resId = R.drawable.background_picture_6,
                scaleType = UiNoteBackgroundImage.ScaleType.CENTER,
            )
        ),
        UiNoteTheme.Image.Picture(
            color = UiNoteBackground(
                id = "picture_color_2",
                isLight = false,
                colorScheme = UiThemeColor.TOTAL_BLACK.colorScheme,
            ),
            image = UiNoteBackgroundImage(
                id = "picture_image_2",
                resId = R.drawable.background_picture_2,
                scaleType = UiNoteBackgroundImage.ScaleType.CROP_ALIGN_CENTER,
            )
        ),
        UiNoteTheme.Image.Picture(
            color = UiNoteBackground(
                id = "picture_color_1",
                isLight = false,
                colorScheme = UiThemeColor.NIGHT_RAIN.colorScheme,
            ),
            image = UiNoteBackgroundImage(
                id = "picture_image_1",
                resId = R.drawable.background_picture_1,
                scaleType = UiNoteBackgroundImage.ScaleType.FILL,
            )
        ),
        UiNoteTheme.Image.Picture(
            color = UiNoteBackground(
                id = "picture_color_13",
                isLight = false,
                colorScheme = UiThemeColor.PINK_DARK.colorScheme,
            ),
            image = UiNoteBackgroundImage(
                id = "picture_image_13",
                resId = R.drawable.background_picture_13,
                scaleType = UiNoteBackgroundImage.ScaleType.FILL,
            )
        ),
        UiNoteTheme.Image.Picture(
            color = UiNoteBackground(
                id = "picture_color_14",
                isLight = false,
                colorScheme = UiThemeColor.TOTAL_BLACK.colorScheme,
            ),
            image = UiNoteBackgroundImage(
                id = "picture_image_14",
                resId = R.drawable.background_picture_14,
                scaleType = UiNoteBackgroundImage.ScaleType.CROP_ALIGN_TOP,
            )
        ),
        UiNoteTheme.Image.Picture(
            color = UiNoteBackground(
                id = "picture_color_15",
                isLight = false,
                colorScheme = UiThemeColor.BLUE_NIGHT_3.colorScheme,
            ),
            image = UiNoteBackgroundImage(
                id = "picture_image_15",
                resId = R.drawable.background_picture_15,
                scaleType = UiNoteBackgroundImage.ScaleType.CROP_ALIGN_BOTTOM,
            )
        ),
        UiNoteTheme.Image.Picture(
            color = UiNoteBackground(
                id = "picture_color_16",
                isLight = false,
                colorScheme = UiThemeColor.ORANGE.colorScheme,
            ),
            image = UiNoteBackgroundImage(
                id = "picture_image_16",
                resId = R.drawable.background_picture_16,
                scaleType = UiNoteBackgroundImage.ScaleType.CROP_ALIGN_BOTTOM,
            )
        ),
        UiNoteTheme.Image.Picture(
            color = UiNoteBackground(
                id = "picture_color_17",
                isLight = false,
                colorScheme = UiThemeColor.PINK_PANTHER.colorScheme,
            ),
            image = UiNoteBackgroundImage(
                id = "picture_image_17",
                resId = R.drawable.background_picture_17,
                scaleType = UiNoteBackgroundImage.ScaleType.CROP_ALIGN_BOTTOM,
            )
        ),
        UiNoteTheme.Image.Picture(
            color = UiNoteBackground(
                id = "picture_color_7",
                isLight = true,
                colorScheme = UiThemeColor.LIGHT_BLUE.colorScheme,
            ),
            image = UiNoteBackgroundImage(
                id = "picture_image_7",
                resId = R.drawable.background_picture_7,
            )
        ),
        UiNoteTheme.Image.Picture(
            color = UiNoteBackground(
                id = "picture_color_8",
                isLight = true,
                colorScheme = UiThemeColor.GREEN_LIGHT.colorScheme,
            ),
            image = UiNoteBackgroundImage(
                id = "picture_image_8",
                resId = R.drawable.background_picture_8,
                scaleType = UiNoteBackgroundImage.ScaleType.FILL,
            )
        ),
        UiNoteTheme.Image.Picture(
            color = UiNoteBackground(
                id = "picture_color_9",
                isLight = true,
                colorScheme = UiThemeColor.PINK_LIGHT.colorScheme,
            ),
            image = UiNoteBackgroundImage(
                id = "picture_image_9",
                resId = R.drawable.background_picture_9,
                scaleType = UiNoteBackgroundImage.ScaleType.FILL,
            )
        ),
    )

    override fun getPatternImages(): List<UiNoteBackgroundImage> = listOf(
        UiNoteBackgroundImage(
            id = "pattern_0",
            resId = R.drawable.background_pattern_0,
        ),
        UiNoteBackgroundImage(
            id = "pattern_1",
            resId = R.drawable.background_pattern_1,
        ),
        UiNoteBackgroundImage(
            id = "pattern_2",
            resId = R.drawable.background_pattern_2,
        ),
        UiNoteBackgroundImage(
            id = "pattern_3",
            resId = R.drawable.background_pattern_3,
        ),
        UiNoteBackgroundImage(
            id = "pattern_4",
            resId = R.drawable.background_pattern_4,
        ),
    )

    private fun getDarkSolidThemes(): List<UiNoteTheme.Solid> = listOf(
        UiNoteTheme.Solid(
            color = UiNoteBackground(
                id = "solid_dark_01",
                isLight = false,
                colorScheme = UiThemeColor.TOTAL_BLACK.colorScheme,
            ),
        ),
        UiNoteTheme.Solid(
            color = UiNoteBackground(
                id = "solid_dark_0",
                isLight = false,
                colorScheme = UiThemeColor.SCANDI_GRANDPA_GRAY_DARK.colorScheme,
            ),
        ),
        UiNoteTheme.Solid(
            color = UiNoteBackground(
                id = "solid_dark_1",
                isLight = false,
                colorScheme = UiThemeColor.EUPHORIA_BLUE_DARK.colorScheme,
            ),
        ),
        UiNoteTheme.Solid(
            color = UiNoteBackground(
                id = "solid_dark_2",
                isLight = false,
                colorScheme = UiThemeColor.DISTANT_CASTLE_BLUE.colorScheme,
            ),
        ),
        UiNoteTheme.Solid(
            color = UiNoteBackground(
                id = "solid_dark_3",
                isLight = false,
                colorScheme = UiThemeColor.STORM_IN_THE_NIGHT_BLUE.colorScheme,
            ),
        ),
        UiNoteTheme.Solid(
            UiNoteBackground(
                id = "solid_dark_4",
                isLight = false,
                colorScheme = UiThemeColor.STORM_IN_THE_NIGHT_BLUE_LIGHT.colorScheme,
            ),
        ),
        UiNoteTheme.Solid(
            color = UiNoteBackground(
                id = "solid_dark_5",
                isLight = false,
                colorScheme = UiThemeColor.EUPHORIA_BLUE.colorScheme,
            ),
        ),
        UiNoteTheme.Solid(
            color = UiNoteBackground(
                id = "solid_dark_6",
                isLight = false,
                colorScheme = UiThemeColor.UNICORN_2012_BLUE.colorScheme,
            ),
        ),
        UiNoteTheme.Solid(
            color = UiNoteBackground(
                id = "solid_dark_7",
                isLight = false,
                colorScheme = UiThemeColor.EUPHORIA_PINK.colorScheme,
            ),
        ),
        UiNoteTheme.Solid(
            color = UiNoteBackground(
                id = "solid_dark_8",
                isLight = false,
                colorScheme = UiThemeColor.UNICORN_2012_PINK_DARK.colorScheme,
            ),
        ),
        UiNoteTheme.Solid(
            color = UiNoteBackground(
                id = "solid_dark_9",
                isLight = false,
                colorScheme = UiThemeColor.EUPHORIA_VIOLET.colorScheme,
            ),
        ),
        UiNoteTheme.Solid(
            color = UiNoteBackground(
                id = "solid_dark_10",
                isLight = false,
                colorScheme = UiThemeColor.VAMPIRE_RED_DARK.colorScheme,
            ),
        ),
        UiNoteTheme.Solid(
            color = UiNoteBackground(
                id = "solid_dark_11",
                isLight = false,
                colorScheme = UiThemeColor.IRIS_RED.colorScheme,
            ),
        ),
        UiNoteTheme.Solid(
            color = UiNoteBackground(
                id = "solid_dark_12",
                isLight = false,
                colorScheme = UiThemeColor.IRIS_RED_DARK.colorScheme,
            ),
        ),
        UiNoteTheme.Solid(
            color = UiNoteBackground(
                id = "solid_dark_13",
                isLight = false,
                colorScheme = UiThemeColor.DISTANT_CASTLE_GREEN.colorScheme,
            ),
        ),
    )

    private fun getLightSolidThemes(): List<UiNoteTheme.Solid> = listOf(
        UiNoteTheme.Solid(
            UiNoteBackground(
                id = "solid_light_01",
                isLight = true,
                colorScheme = UiThemeColor.LIGHT_WHITE.colorScheme,
            ),
        ),
        UiNoteTheme.Solid(
            UiNoteBackground(
                id = "solid_light_0",
                isLight = true,
                colorScheme = UiThemeColor.LIGHT_BLUE.colorScheme,
            ),
        ),
        UiNoteTheme.Solid(
            UiNoteBackground(
                id = "solid_light_1",
                isLight = true,
                colorScheme = UiThemeColor.GREEN_LIGHT.colorScheme,
            ),
        ),
        UiNoteTheme.Solid(
            UiNoteBackground(
                id = "solid_light_2",
                isLight = true,
                colorScheme = UiThemeColor.PINK_LIGHT.colorScheme,
            ),
        ),
        UiNoteTheme.Solid(
            UiNoteBackground(
                id = "solid_light_3",
                isLight = true,
                colorScheme = UiThemeColor.PURPLE_LIGHT.colorScheme,
            ),
        ),
        UiNoteTheme.Solid(
            UiNoteBackground(
                id = "solid_light_4",
                isLight = true,
                colorScheme = UiThemeColor.ORANGE_LIGHT.colorScheme,
            ),
        ),
    )
}