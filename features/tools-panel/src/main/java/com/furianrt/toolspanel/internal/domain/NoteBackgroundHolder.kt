package com.furianrt.toolspanel.internal.domain

import com.furianrt.notelistui.entities.UiNoteBackground
import com.furianrt.toolspanel.api.NoteBackgroundProvider
import com.furianrt.uikit.entities.UiThemeColor
import com.furianrt.uikit.entities.colorScheme
import javax.inject.Inject

internal class NoteBackgroundHolder @Inject constructor() : NoteBackgroundProvider {

    override fun getBackground(id: String?): UiNoteBackground? = if (id != null) {
        getDarkBackgrounds().find { it.id == id } ?: getLightBackgrounds().find { it.id == id }
    } else {
        null
    }

    override fun getDarkBackgrounds(): List<UiNoteBackground> = listOf(
        UiNoteBackground.Solid(
            id = "solid_dark_01",
            isLight = false,
            colorScheme = UiThemeColor.TOTAL_BLACK.colorScheme,
        ),
        UiNoteBackground.Solid(
            id = "solid_dark_0",
            isLight = false,
            colorScheme = UiThemeColor.SCANDI_GRANDPA_GRAY_DARK.colorScheme,
        ),
        UiNoteBackground.Solid(
            id = "solid_dark_1",
            isLight = false,
            colorScheme = UiThemeColor.EUPHORIA_BLUE_DARK.colorScheme,
        ),
        UiNoteBackground.Solid(
            id = "solid_dark_2",
            isLight = false,
            colorScheme = UiThemeColor.DISTANT_CASTLE_BLUE.colorScheme,
        ),
        UiNoteBackground.Solid(
            id = "solid_dark_3",
            isLight = false,
            colorScheme = UiThemeColor.STORM_IN_THE_NIGHT_BLUE.colorScheme,
        ),
        UiNoteBackground.Solid(
            id = "solid_dark_4",
            isLight = false,
            colorScheme = UiThemeColor.STORM_IN_THE_NIGHT_BLUE_LIGHT.colorScheme,
        ),
        UiNoteBackground.Solid(
            id = "solid_dark_5",
            isLight = false,
            colorScheme = UiThemeColor.EUPHORIA_BLUE.colorScheme,
        ),
        UiNoteBackground.Solid(
            id = "solid_dark_6",
            isLight = false,
            colorScheme = UiThemeColor.UNICORN_2012_BLUE.colorScheme,
        ),
        UiNoteBackground.Solid(
            id = "solid_dark_7",
            isLight = false,
            colorScheme = UiThemeColor.EUPHORIA_PINK.colorScheme,
        ),
        UiNoteBackground.Solid(
            id = "solid_dark_8",
            isLight = false,
            colorScheme = UiThemeColor.UNICORN_2012_PINK_DARK.colorScheme,
        ),
        UiNoteBackground.Solid(
            id = "solid_dark_9",
            isLight = false,
            colorScheme = UiThemeColor.EUPHORIA_VIOLET.colorScheme,
        ),
        UiNoteBackground.Solid(
            id = "solid_dark_10",
            isLight = false,
            colorScheme = UiThemeColor.VAMPIRE_RED_DARK.colorScheme,
        ),
        UiNoteBackground.Solid(
            id = "solid_dark_11",
            isLight = false,
            colorScheme = UiThemeColor.IRIS_RED.colorScheme,
        ),
        UiNoteBackground.Solid(
            id = "solid_dark_12",
            isLight = false,
            colorScheme = UiThemeColor.IRIS_RED_DARK.colorScheme,
        ),
    )

    override fun getLightBackgrounds(): List<UiNoteBackground> = listOf(
        UiNoteBackground.Solid(
            id = "solid_light_01",
            isLight = true,
            colorScheme = UiThemeColor.LIGHT_WHITE.colorScheme,
        ),
        UiNoteBackground.Solid(
            id = "solid_light_0",
            isLight = true,
            colorScheme = UiThemeColor.LIGHT_BLUE.colorScheme,
        ),
        UiNoteBackground.Solid(
            id = "solid_light_1",
            isLight = true,
            colorScheme = UiThemeColor.GREEN_LIGHT.colorScheme,
        ),
        UiNoteBackground.Solid(
            id = "solid_light_2",
            isLight = true,
            colorScheme = UiThemeColor.PINK_LIGHT.colorScheme,
        ),
        UiNoteBackground.Solid(
            id = "solid_light_3",
            isLight = true,
            colorScheme = UiThemeColor.PURPLE_LIGHT.colorScheme,
        ),
        UiNoteBackground.Solid(
            id = "solid_light_4",
            isLight = true,
            colorScheme = UiThemeColor.ORANGE_LIGHT.colorScheme,
        ),
    )
}