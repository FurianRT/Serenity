package com.furianrt.toolspanel.internal.domain

import com.furianrt.core.DispatchersProvider
import com.furianrt.domain.repositories.MediaRepository
import com.furianrt.notelistui.entities.UiNoteBackground
import com.furianrt.notelistui.entities.UiNoteBackgroundImage
import com.furianrt.notelistui.entities.UiNoteTheme
import com.furianrt.toolspanel.R
import com.furianrt.uikit.R as uiR
import com.furianrt.toolspanel.api.NoteThemeProvider
import com.furianrt.toolspanel.internal.ui.background.custom.extensions.toUiNoteTheme
import com.furianrt.uikit.entities.UiThemeColor
import com.furianrt.uikit.entities.colorScheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class NoteThemesHolder @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val dispatchers: DispatchersProvider,
) : NoteThemeProvider {

    override suspend fun findTheme(
        colorId: String?,
        imageId: String?,
    ): UiNoteTheme? = withContext(dispatchers.default) {
        val pictureTheme = getPictureThemes()
            .find { it.colorId == colorId && it.imageId == imageId }
        if (pictureTheme != null) {
            return@withContext pictureTheme
        }

        val solidTheme = getSolidThemes().find { it.colorId == colorId }
        val pattern = getPatternImages().find { it.id == imageId }
        if (pattern != null) {
            return@withContext UiNoteTheme.Image.Pattern(
                color = solidTheme?.color,
                image = pattern,
            )
        }

        if (solidTheme != null) {
            return@withContext solidTheme
        }

        val custom = mediaRepository.getAllCustomNoteBackgrounds().first().find { it.id == imageId }
        if (custom != null) {
            return@withContext custom.toUiNoteTheme()
        }

        return@withContext null
    }

    override fun getSolidThemes(): List<UiNoteTheme.Solid> = solidThemes()
    override fun getPictureThemes(): List<UiNoteTheme.Image.Picture> = pictureThemes()
    override fun getPatternImages(): List<UiNoteBackgroundImage> = patternImages()

    companion object {
        internal fun pictureThemes(): List<UiNoteTheme.Image.Picture> = listOf(
            UiNoteTheme.Image.Picture(
                color = UiNoteBackground(
                    id = "picture_color_0",
                    isLight = false,
                    colorScheme = UiThemeColor.APP_BACKGROUND_PICTURE_10.colorScheme,
                ),
                image = UiNoteBackgroundImage(
                    id = "picture_image_0",
                    source = UiNoteBackgroundImage.Source.Resource(uiR.drawable.app_background_picture_10),
                    scaleType = UiNoteBackgroundImage.ScaleType.CROP_ALIGN_BOTTOM,
                )
            ),
            UiNoteTheme.Image.Picture(
                color = UiNoteBackground(
                    id = "picture_color_5",
                    isLight = false,
                    colorScheme = UiThemeColor.APP_BACKGROUND_PICTURE_11.colorScheme,
                ),
                image = UiNoteBackgroundImage(
                    id = "picture_image_5",
                    source = UiNoteBackgroundImage.Source.Resource(uiR.drawable.app_background_picture_11),
                    scaleType = UiNoteBackgroundImage.ScaleType.FILL,
                )
            ),
            UiNoteTheme.Image.Picture(
                color = UiNoteBackground(
                    id = "picture_color_3",
                    isLight = false,
                    colorScheme = UiThemeColor.APP_BACKGROUND_PICTURE_1.colorScheme,
                ),
                image = UiNoteBackgroundImage(
                    id = "picture_image_3",
                    source = UiNoteBackgroundImage.Source.Resource(uiR.drawable.app_background_picture_1),
                    scaleType = UiNoteBackgroundImage.ScaleType.CROP_ALIGN_BOTTOM,
                )
            ),
            UiNoteTheme.Image.Picture(
                color = UiNoteBackground(
                    id = "picture_color_21",
                    isLight = false,
                    colorScheme = UiThemeColor.APP_BACKGROUND_PICTURE_9.colorScheme,
                ),
                image = UiNoteBackgroundImage(
                    id = "picture_image_21",
                    source = UiNoteBackgroundImage.Source.Resource(uiR.drawable.app_background_picture_9),
                    scaleType = UiNoteBackgroundImage.ScaleType.CROP_ALIGN_CENTER,
                )
            ),
            UiNoteTheme.Image.Picture(
                color = UiNoteBackground(
                    id = "picture_color_11",
                    isLight = false,
                    colorScheme = UiThemeColor.APP_BACKGROUND_PICTURE_3.colorScheme,
                ),
                image = UiNoteBackgroundImage(
                    id = "picture_image_11",
                    source = UiNoteBackgroundImage.Source.Resource(uiR.drawable.app_background_picture_3),
                    scaleType = UiNoteBackgroundImage.ScaleType.FILL,
                )
            ),
            UiNoteTheme.Image.Picture(
                color = UiNoteBackground(
                    id = "picture_color_20",
                    isLight = false,
                    colorScheme = UiThemeColor.APP_BACKGROUND_PICTURE_8.colorScheme,
                ),
                image = UiNoteBackgroundImage(
                    id = "picture_image_20",
                    source = UiNoteBackgroundImage.Source.Resource(uiR.drawable.app_background_picture_8),
                    scaleType = UiNoteBackgroundImage.ScaleType.CROP_ALIGN_CENTER,
                )
            ),
            UiNoteTheme.Image.Picture(
                color = UiNoteBackground(
                    id = "picture_color_19",
                    isLight = false,
                    colorScheme = UiThemeColor.APP_BACKGROUND_PICTURE_7.colorScheme,
                ),
                image = UiNoteBackgroundImage(
                    id = "picture_image_19",
                    source = UiNoteBackgroundImage.Source.Resource(uiR.drawable.app_background_picture_7),
                    scaleType = UiNoteBackgroundImage.ScaleType.CROP_ALIGN_CENTER,
                )
            ),
            UiNoteTheme.Image.Picture(
                color = UiNoteBackground(
                    id = "picture_color_14",
                    isLight = false,
                    colorScheme = UiThemeColor.APP_BACKGROUND_PICTURE_4.colorScheme,
                ),
                image = UiNoteBackgroundImage(
                    id = "picture_image_14",
                    source = UiNoteBackgroundImage.Source.Resource(uiR.drawable.app_background_picture_4),
                    scaleType = UiNoteBackgroundImage.ScaleType.CROP_ALIGN_TOP,
                )
            ),
            UiNoteTheme.Image.Picture(
                color = UiNoteBackground(
                    id = "picture_color_10",
                    isLight = false,
                    colorScheme = UiThemeColor.APP_BACKGROUND_PICTURE_2.colorScheme,
                ),
                image = UiNoteBackgroundImage(
                    id = "picture_image_10",
                    source = UiNoteBackgroundImage.Source.Resource(uiR.drawable.app_background_picture_2),
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
                    source = UiNoteBackgroundImage.Source.Resource(R.drawable.background_picture_6),
                    scaleType = UiNoteBackgroundImage.ScaleType.CENTER,
                )
            ),
            UiNoteTheme.Image.Picture(
                color = UiNoteBackground(
                    id = "picture_color_2",
                    isLight = false,
                    colorScheme = UiThemeColor.APP_BACKGROUND_PICTURE_0.colorScheme,
                ),
                image = UiNoteBackgroundImage(
                    id = "picture_image_2",
                    source = UiNoteBackgroundImage.Source.Resource(uiR.drawable.app_background_picture_0),
                    scaleType = UiNoteBackgroundImage.ScaleType.CROP_ALIGN_CENTER,
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
                    source = UiNoteBackgroundImage.Source.Resource(R.drawable.background_picture_4),
                    scaleType = UiNoteBackgroundImage.ScaleType.CROP_ALIGN_BOTTOM,
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
                    source = UiNoteBackgroundImage.Source.Resource(R.drawable.background_picture_1),
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
                    source = UiNoteBackgroundImage.Source.Resource(R.drawable.background_picture_13),
                    scaleType = UiNoteBackgroundImage.ScaleType.FILL,
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
                    source = UiNoteBackgroundImage.Source.Resource(R.drawable.background_picture_12),
                    scaleType = UiNoteBackgroundImage.ScaleType.FILL,
                )
            ),
            UiNoteTheme.Image.Picture(
                color = UiNoteBackground(
                    id = "picture_color_15",
                    isLight = false,
                    colorScheme = UiThemeColor.APP_BACKGROUND_PICTURE_5.colorScheme,
                ),
                image = UiNoteBackgroundImage(
                    id = "picture_image_15",
                    source = UiNoteBackgroundImage.Source.Resource(uiR.drawable.app_background_picture_5),
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
                    source = UiNoteBackgroundImage.Source.Resource(R.drawable.background_picture_16),
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
                    source = UiNoteBackgroundImage.Source.Resource(R.drawable.background_picture_17),
                    scaleType = UiNoteBackgroundImage.ScaleType.CROP_ALIGN_BOTTOM,
                )
            ),
            UiNoteTheme.Image.Picture(
                color = UiNoteBackground(
                    id = "picture_color_18",
                    isLight = true,
                    colorScheme = UiThemeColor.APP_BACKGROUND_PICTURE_6.colorScheme,
                ),
                image = UiNoteBackgroundImage(
                    id = "picture_image_18",
                    source = UiNoteBackgroundImage.Source.Resource(uiR.drawable.app_background_picture_6),
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
                    source = UiNoteBackgroundImage.Source.Resource(R.drawable.background_picture_7),
                    scaleType = UiNoteBackgroundImage.ScaleType.CROP_ALIGN_CENTER,
                )
            ),
            UiNoteTheme.Image.Picture(
                color = UiNoteBackground(
                    id = "picture_color_8",
                    isLight = true,
                    colorScheme = UiThemeColor.APP_BACKGROUND_PICTURE_12.colorScheme,
                ),
                image = UiNoteBackgroundImage(
                    id = "picture_image_8",
                    source = UiNoteBackgroundImage.Source.Resource(uiR.drawable.app_background_picture_12),
                    scaleType = UiNoteBackgroundImage.ScaleType.FILL,
                )
            ),
            UiNoteTheme.Image.Picture(
                color = UiNoteBackground(
                    id = "picture_color_9",
                    isLight = true,
                    colorScheme = UiThemeColor.APP_BACKGROUND_PICTURE_13.colorScheme,
                ),
                image = UiNoteBackgroundImage(
                    id = "picture_image_9",
                    source = UiNoteBackgroundImage.Source.Resource(uiR.drawable.app_background_picture_13),
                    scaleType = UiNoteBackgroundImage.ScaleType.FILL,
                )
            ),
        )
        internal fun patternImages(): List<UiNoteBackgroundImage> = listOf(
            UiNoteBackgroundImage(
                id = "pattern_0",
                source = UiNoteBackgroundImage.Source.Resource(R.drawable.background_pattern_0),
            ),
            UiNoteBackgroundImage(
                id = "pattern_1",
                source = UiNoteBackgroundImage.Source.Resource(R.drawable.background_pattern_1),
            ),
            UiNoteBackgroundImage(
                id = "pattern_2",
                source = UiNoteBackgroundImage.Source.Resource(R.drawable.background_pattern_2),
            ),
            UiNoteBackgroundImage(
                id = "pattern_3",
                source = UiNoteBackgroundImage.Source.Resource(R.drawable.background_pattern_3),
            ),
            UiNoteBackgroundImage(
                id = "pattern_4",
                source = UiNoteBackgroundImage.Source.Resource(R.drawable.background_pattern_4),
            ),
        )
        internal fun solidThemes(): List<UiNoteTheme.Solid> = darkSolidThemes() + lightSolidThemes()
        internal fun darkSolidThemes(): List<UiNoteTheme.Solid> = listOf(
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
        internal fun lightSolidThemes(): List<UiNoteTheme.Solid> = listOf(
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
}