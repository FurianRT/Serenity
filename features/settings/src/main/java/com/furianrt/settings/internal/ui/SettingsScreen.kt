package com.furianrt.settings.internal.ui

import android.content.Context
import android.content.Intent
import androidx.annotation.IntRange
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.furianrt.core.buildImmutableList
import com.furianrt.settings.R
import com.furianrt.settings.internal.entities.AppTheme
import com.furianrt.uikit.components.DefaultToolbar
import com.furianrt.uikit.components.GeneralButton
import com.furianrt.uikit.components.SnackBar
import com.furianrt.uikit.entities.UiThemeColor
import com.furianrt.uikit.extensions.applyIf
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.extensions.drawBottomShadow
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.EmailSender
import com.furianrt.uikit.utils.PreviewWithBackground
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import com.furianrt.uikit.R as uiR
import androidx.core.net.toUri

@Composable
internal fun SettingsScreen(
    openSecurityScreen: () -> Unit,
    openBackupScreen: () -> Unit,
    onCloseRequest: () -> Unit,
) {
    val viewModel: SettingsViewModel = hiltViewModel()
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    val context = LocalContext.current

    val onCloseRequestState by rememberUpdatedState(onCloseRequest)
    val openBackupScreenState by rememberUpdatedState(openBackupScreen)
    val openSecurityScreenState by rememberUpdatedState(openSecurityScreen)

    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SettingsEffect.CloseScreen -> onCloseRequestState()
                is SettingsEffect.OpenSecurityScreen -> openSecurityScreenState()
                is SettingsEffect.OpenBackupScreen -> openBackupScreenState()
                is SettingsEffect.SendFeedbackEmail -> {
                    EmailSender.send(
                        context = context,
                        email = effect.supportEmail,
                        subject = context.getString(
                            R.string.settings_feedback_email_subject,
                            effect.text,
                        ),
                    ).onFailure { error ->
                        error.printStackTrace()
                        snackBarHostState.currentSnackbarData?.dismiss()
                        snackBarHostState.showSnackbar(
                            message = context.getString(R.string.settings_feedback_email_error),
                            duration = SnackbarDuration.Short,
                        )
                    }
                }

                is SettingsEffect.OpenMarketPage -> openAppMarketPage(context, effect.url)
            }
        }
    }

    ScreenContent(uiState, snackBarHostState, viewModel::onEvent)
}

@Composable
private fun ScreenContent(
    uiState: SettingsUiState,
    snackBarHostState: SnackbarHostState,
    onEvent: (event: SettingsEvent) -> Unit = {},
) {
    val scrollState = rememberScrollState()
    val shadowColor = MaterialTheme.colorScheme.surfaceDim
    Scaffold(
        modifier = Modifier.systemBarsPadding(),
        topBar = {
            DefaultToolbar(
                modifier = Modifier.drawBehind {
                    if (scrollState.canScrollBackward) {
                        drawBottomShadow(color = shadowColor)
                    }
                },
                title = stringResource(uiR.string.settings_title),
                onBackClick = { onEvent(SettingsEvent.OnButtonBackClick) },
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                snackbar = { data ->
                    SnackBar(
                        title = data.visuals.message,
                        icon = painterResource(uiR.drawable.ic_email),
                        tonalColor = MaterialTheme.colorScheme.tertiary,
                    )
                },
            )
        },
    ) { paddingValues ->
        when (uiState) {
            is SettingsUiState.Success -> SuccessScreen(
                modifier = Modifier.padding(paddingValues),
                uiState = uiState,
                scrollState = scrollState,
                onEvent = onEvent,
            )

            is SettingsUiState.Loading -> LoadingScreen(
                modifier = Modifier.padding(paddingValues),
            )
        }
    }
}

@Composable
private fun SuccessScreen(
    uiState: SettingsUiState.Success,
    scrollState: ScrollState,
    onEvent: (event: SettingsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        GeneralButton(
            modifier = Modifier.padding(horizontal = 8.dp),
            title = stringResource(id = R.string.settings_security_title),
            iconPainter = painterResource(id = R.drawable.ic_lock),
            onClick = { onEvent(SettingsEvent.OnButtonSecurityClick) },
        )
        GeneralButton(
            modifier = Modifier.padding(horizontal = 8.dp),
            title = stringResource(id = R.string.settings_backup_title),
            iconPainter = painterResource(id = R.drawable.ic_cloud),
            onClick = { onEvent(SettingsEvent.OnButtonBackupClick) },
        )
        ThemeSelector(
            modifier = Modifier.padding(vertical = 4.dp),
            themes = uiState.themes,
            selected = uiState.selectedThemeColor,
            onSelected = { onEvent(SettingsEvent.OnAppThemeColorSelected(it)) },
        )
        GeneralButton(
            modifier = Modifier.padding(horizontal = 8.dp),
            title = stringResource(R.string.settings_language_title),
            iconPainter = painterResource(R.drawable.ic_language),
            hint = "English",
            onClick = {},
        )
        Rating(
            modifier = Modifier.padding(horizontal = 8.dp),
            rating = uiState.rating,
            onSelected = { onEvent(SettingsEvent.OnRatingSelected(it)) },
        )
        GeneralButton(
            modifier = Modifier.padding(top = 16.dp, start = 8.dp, end = 8.dp),
            title = stringResource(id = R.string.settings_donate_title),
            iconPainter = painterResource(id = R.drawable.ic_hart),
            onClick = {},
        )
        GeneralButton(
            modifier = Modifier.padding(horizontal = 8.dp),
            title = stringResource(id = R.string.settings_feedback_title),
            iconPainter = painterResource(id = R.drawable.ic_mail),
            onClick = { onEvent(SettingsEvent.OnButtonFeedbackClick) },
        )
        GeneralButton(
            modifier = Modifier.padding(horizontal = 8.dp),
            title = stringResource(id = R.string.settings_about_title),
            iconPainter = painterResource(id = uiR.drawable.ic_error),
            onClick = {},
        )
    }
}

@Composable
private fun ThemeSelector(
    themes: ImmutableList<AppTheme>,
    selected: UiThemeColor,
    onSelected: (color: UiThemeColor) -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val initialIndex = remember(themes) {
        themes.indexOfFirst { theme -> theme.colors.any { it.id == selected.id } }
    }
    Column(
        modifier = modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_theme),
                contentDescription = stringResource(id = R.string.settings_theme_title),
                tint = Color.Unspecified,
            )
            Text(
                text = stringResource(id = R.string.settings_theme_title),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        LazyRow(
            modifier = Modifier.systemGestureExclusion(),
            state = rememberLazyListState(
                initialFirstVisibleItemIndex = initialIndex,
                initialFirstVisibleItemScrollOffset = -density.run { 24.dp.toPx() }.toInt(),
            ),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(horizontal = 20.dp),
        ) {
            items(count = themes.count()) { index ->
                ThemeItem(
                    theme = themes[index],
                    selected = selected,
                    onSelected = onSelected,
                )
            }
        }
    }
}

@Composable
private fun ThemeItem(
    theme: AppTheme,
    selected: UiThemeColor?,
    onSelected: (color: UiThemeColor) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            modifier = Modifier
                .padding(start = 4.dp)
                .alpha(0.5f),
            text = theme.title,
            style = MaterialTheme.typography.labelSmall,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val shape = RoundedCornerShape(16.dp)
            theme.colors.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(shape)
                        .background(color.primary)
                        .applyIf(color == selected) {
                            Modifier.border(1.dp, color.accent, shape)
                        }
                        .clickableNoRipple { onSelected(color) },
                )
            }
        }
    }
}

@Composable
private fun Rating(
    @IntRange(0, 5) rating: Int,
    onSelected: (rating: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(
            text = stringResource(id = R.string.settings_rate_us_title),
            style = MaterialTheme.typography.bodyMedium,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            repeat(5) { index ->
                Icon(
                    modifier = Modifier.clickableNoRipple { onSelected(index + 1) },
                    painter = if (rating >= index + 1) {
                        painterResource(id = R.drawable.ic_star_filled)
                    } else {
                        painterResource(id = R.drawable.ic_star_outlined)
                    },
                    contentDescription = (index + 1).toString(),
                    tint = Color.Unspecified,
                )
            }
        }
    }
}

@Composable
private fun LoadingScreen(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize())
}

private fun openAppMarketPage(
    context: Context,
    url: String,
) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = url.toUri()
    }
    context.startActivity(intent)
}

@Composable
@PreviewWithBackground
private fun ScreenContentPreview() {
    SerenityTheme {
        ScreenContent(
            snackBarHostState = SnackbarHostState(),
            uiState = SettingsUiState.Success(
                themes = buildImmutableList {
                    add(
                        AppTheme(
                            title = stringResource(R.string.settings_app_theme_scandi_grandpa_title),
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
                            title = stringResource(R.string.settings_app_theme_distant_castle_title),
                            colors = persistentListOf(
                                UiThemeColor.DISTANT_CASTLE_BROWN,
                                UiThemeColor.DISTANT_CASTLE_GREEN,
                                UiThemeColor.DISTANT_CASTLE_BLUE,
                            ),
                        )
                    )
                },
                selectedThemeColor = UiThemeColor.DISTANT_CASTLE_GREEN,
                rating = 4,
            ),
        )
    }
}
