package com.furianrt.settings.internal.domain

import com.furianrt.core.buildImmutableList
import com.furianrt.domain.managers.ResourcesManager
import com.furianrt.settings.R
import com.furianrt.settings.internal.entities.AppTheme
import com.furianrt.uikit.entities.UiThemeColor
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import javax.inject.Inject

internal class GetAppThemeListUseCase @Inject constructor(
    private val resourcesManager: ResourcesManager,
) {
    operator fun invoke(): ImmutableList<AppTheme> = buildThemesList()

    private fun buildThemesList(): ImmutableList<AppTheme> = buildImmutableList {
        add(
            AppTheme(
                title = resourcesManager.getString(R.string.settings_app_theme_scandi_grandpa_title),
                colors = persistentListOf(
                    UiThemeColor.SCANDI_GRANDPA_GRAY_DARK,
                    UiThemeColor.SCANDI_GRANDPA_BROWN,
                    UiThemeColor.SCANDI_GRANDPA_YELLOW,
                    UiThemeColor.SCANDI_GRANDPA_GRAY,
                ),
            )
        )
        add(
            AppTheme(
                title = resourcesManager.getString(R.string.settings_app_theme_cleopatra_title),
                colors = persistentListOf(
                    UiThemeColor.CLEOPATRA_BLUE,
                    UiThemeColor.CLEOPATRA_YELLOW,
                    UiThemeColor.CLEOPATRA_BROWN,
                    UiThemeColor.CLEOPATRA_ORANGE,
                ),
            )
        )
        add(
            AppTheme(
                title = resourcesManager.getString(R.string.settings_app_theme_vampire_title),
                colors = persistentListOf(
                    UiThemeColor.VAMPIRE_RED_LIGHT,
                    UiThemeColor.VAMPIRE_RED_DARK,
                    UiThemeColor.VAMPIRE_RED,
                    UiThemeColor.VAMPIRE_BLACK,
                ),
            )
        )
        add(
            AppTheme(
                title = resourcesManager.getString(R.string.settings_app_theme_euphoria_title),
                colors = persistentListOf(
                    UiThemeColor.EUPHORIA_BLUE_DARK,
                    UiThemeColor.EUPHORIA_VIOLET,
                    UiThemeColor.EUPHORIA_BLUE,
                    UiThemeColor.EUPHORIA_PINK,
                ),
            )
        )
        add(
            AppTheme(
                title = resourcesManager.getString(R.string.settings_app_theme_iris_title),
                colors = persistentListOf(
                    UiThemeColor.IRIS_PINK,
                    UiThemeColor.IRIS_RED,
                    UiThemeColor.IRIS_RED_DARK,
                    UiThemeColor.IRIS_GREEN,
                ),
            )
        )
        add(
            AppTheme(
                title = resourcesManager.getString(R.string.settings_app_theme_storm_in_the_night_title),
                colors = persistentListOf(
                    UiThemeColor.STORM_IN_THE_NIGHT_BLUE,
                    UiThemeColor.STORM_IN_THE_NIGHT_BLUE_DARK,
                    UiThemeColor.STORM_IN_THE_NIGHT_BLACK,
                    UiThemeColor.STORM_IN_THE_NIGHT_BLUE_LIGHT,
                    UiThemeColor.STORM_IN_THE_NIGHT_GRAY,
                ),
            )
        )
        add(
            AppTheme(
                title = resourcesManager.getString(R.string.settings_app_theme_unicorn_2012_title),
                colors = persistentListOf(
                    UiThemeColor.UNICORN_2012_PINK,
                    UiThemeColor.UNICORN_2012_BLUE,
                    UiThemeColor.UNICORN_2012_YELLOW,
                    UiThemeColor.UNICORN_2012_PINK_DARK,
                    UiThemeColor.UNICORN_2012_RED,
                ),
            )
        )
        add(
            AppTheme(
                title = resourcesManager.getString(R.string.settings_app_theme_space_cowboy_title),
                colors = persistentListOf(
                    UiThemeColor.SPACE_COWBOY_BROWN_LIGHT,
                    UiThemeColor.SPACE_COWBOY_BROWN,
                    UiThemeColor.SPACE_COWBOY_BROWN_DARK,
                    UiThemeColor.SPACE_COWBOY_BLUE,
                ),
            )
        )
        add(
            AppTheme(
                title = resourcesManager.getString(R.string.settings_app_theme_distant_castle_title),
                colors = persistentListOf(
                    UiThemeColor.DISTANT_CASTLE_BROWN,
                    UiThemeColor.DISTANT_CASTLE_PINK,
                    UiThemeColor.DISTANT_CASTLE_GREEN,
                    UiThemeColor.DISTANT_CASTLE_BLUE,
                ),
            )
        )
        add(
            AppTheme(
                title = resourcesManager.getString(R.string.settings_app_theme_olive_tree_title),
                colors = persistentListOf(
                    UiThemeColor.OLIVE_TREE_BROWN,
                    UiThemeColor.OLIVE_TREE_GREEN,
                    UiThemeColor.OLIVE_TREE_BROWN_DARK,
                    UiThemeColor.OLIVE_TREE_GREEN_DARK,
                ),
            )
        )
        add(
            AppTheme(
                title = resourcesManager.getString(R.string.settings_app_theme_beige_censorship_title),
                colors = persistentListOf(
                    UiThemeColor.BEIGE_CENSORSHIP_BROWN_LIGHT,
                    UiThemeColor.BEIGE_CENSORSHIP_BROWN_DARK,
                    UiThemeColor.BEIGE_CENSORSHIP_BLACK,
                    UiThemeColor.BEIGE_CENSORSHIP_BROWN,
                ),
            )
        )
    }
}