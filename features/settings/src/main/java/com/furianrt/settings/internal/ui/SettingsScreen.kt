package com.furianrt.settings.internal.ui

import android.content.Context
import android.content.Intent
import androidx.annotation.IntRange
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.furianrt.core.indexOfFirstOrNull
import com.furianrt.domain.entities.AppLocale
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.settings.R
import com.furianrt.settings.internal.ui.composables.AppFontDialog
import com.furianrt.settings.internal.ui.composables.BadRatingDialog
import com.furianrt.settings.internal.ui.composables.LocaleDialog
import com.furianrt.settings.internal.ui.entities.UiTheme
import com.furianrt.uikit.components.DefaultToolbar
import com.furianrt.uikit.components.GeneralButton
import com.furianrt.uikit.components.MovableToolbarScaffold
import com.furianrt.uikit.components.MovableToolbarState
import com.furianrt.uikit.components.SkipFirstEffect
import com.furianrt.uikit.components.SnackBar
import com.furianrt.uikit.components.TagItem
import com.furianrt.uikit.entities.UiThemeColor
import com.furianrt.uikit.extensions.applyIf
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.extensions.dpToPx
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.IntentCreator
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import com.furianrt.uikit.R as uiR

private data class FontsDialogState(
    val fonts: List<UiNoteFontFamily>,
    val selectedFont: UiNoteFontFamily,
)

private data class LocaleDialogState(
    val locale: List<AppLocale>,
    val selectedLocale: AppLocale,
)

@Composable
internal fun SettingsScreen(
    openSecurityScreen: () -> Unit,
    openBackupScreen: () -> Unit,
    openNoteSettingsScreen: () -> Unit,
    onCloseRequest: () -> Unit,
) {
    val viewModel: SettingsViewModel = hiltViewModel()
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val hazeState = remember { HazeState() }
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val onCloseRequestState by rememberUpdatedState(onCloseRequest)
    val openBackupScreenState by rememberUpdatedState(openBackupScreen)
    val openSecurityScreenState by rememberUpdatedState(openSecurityScreen)
    val openNoteSettingsScreenState by rememberUpdatedState(openNoteSettingsScreen)

    val snackBarHostState = remember { SnackbarHostState() }
    var showBadRatingDialog by remember { mutableStateOf(false) }
    var fontsDialogState: FontsDialogState? by remember { mutableStateOf(null) }
    var localeDialogState: LocaleDialogState? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->
                when (effect) {
                    is SettingsEffect.CloseScreen -> onCloseRequestState()
                    is SettingsEffect.OpenSecurityScreen -> openSecurityScreenState()
                    is SettingsEffect.OpenBackupScreen -> openBackupScreenState()
                    is SettingsEffect.SendFeedbackEmail -> IntentCreator.emailIntent(
                        email = effect.supportEmail,
                        subject = effect.text,
                    ).onSuccess { intent ->
                        context.startActivity(intent)
                    }.onFailure { error ->
                        error.printStackTrace()
                        snackBarHostState.currentSnackbarData?.dismiss()
                        snackBarHostState.showSnackbar(
                            message = context.getString(R.string.settings_feedback_email_error),
                            duration = SnackbarDuration.Short,
                        )
                    }

                    is SettingsEffect.OpenMarketPage -> openAppMarketPage(context, effect.url)
                    is SettingsEffect.ShowBadRatingDialog -> showBadRatingDialog = true
                    is SettingsEffect.ShowFontDialog -> fontsDialogState = FontsDialogState(
                        fonts = effect.fonts,
                        selectedFont = effect.selectedFont,
                    )

                    is SettingsEffect.OpenLink -> {
                        context.startActivity(Intent(Intent.ACTION_VIEW, effect.url.toUri()))
                    }

                    is SettingsEffect.ShowLocaleDialog -> {
                        localeDialogState = LocaleDialogState(effect.locale, effect.selectedLocale)
                    }

                    is SettingsEffect.OpenNoteSettingsScreen -> openNoteSettingsScreenState()
                }
            }
    }

    ScreenContent(
        modifier = Modifier.hazeSource(hazeState),
        uiState = uiState,
        snackBarHostState = snackBarHostState,
        onEvent = viewModel::onEvent,
    )

    if (showBadRatingDialog) {
        BadRatingDialog(
            hazeState = hazeState,
            onSendClick = { viewModel.onEvent(SettingsEvent.OnButtonFeedbackClick) },
            onDismissRequest = { showBadRatingDialog = false },
        )
    }
    fontsDialogState?.let { state ->
        AppFontDialog(
            fonts = state.fonts,
            selectedFont = state.selectedFont,
            hazeState = hazeState,
            onFontSelected = { viewModel.onEvent(SettingsEvent.OnFontSelected(it)) },
            onDismissRequest = { fontsDialogState = null }
        )
    }
    localeDialogState?.let { state ->
        LocaleDialog(
            locales = state.locale,
            selectedLocale = state.selectedLocale,
            hazeState = hazeState,
            onLocaleSelected = { viewModel.onEvent(SettingsEvent.OnLocaleSelected(it)) },
            onDismissRequest = { localeDialogState = null }
        )
    }
}

@Composable
private fun ScreenContent(
    uiState: SettingsUiState,
    snackBarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onEvent: (event: SettingsEvent) -> Unit = {},
) {
    val scrollState = rememberScrollState()
    val toolbarState = remember { MovableToolbarState() }

    val statusBarPv = WindowInsets.statusBars.asPaddingValues()
    val statusBarHeight = rememberSaveable { statusBarPv.calculateTopPadding().value }

    MovableToolbarScaffold(
        modifier = modifier.background(MaterialTheme.colorScheme.surface),
        state = toolbarState,
        listState = scrollState,
        enabled = false,
        toolbar = {
            DefaultToolbar(
                modifier = Modifier.padding(top = statusBarHeight.dp),
                title = stringResource(uiR.string.settings_title),
                onBackClick = { onEvent(SettingsEvent.OnButtonBackClick) },
            )
        }
    ) { topPadding ->
        when (uiState) {
            is SettingsUiState.Success -> SuccessScreen(
                uiState = uiState,
                scrollState = scrollState,
                topPadding = topPadding,
                onEvent = onEvent,
            )

            is SettingsUiState.Loading -> LoadingScreen()
        }
        SnackbarHost(
            modifier = Modifier
                .navigationBarsPadding()
                .align(Alignment.BottomCenter),
            hostState = snackBarHostState,
            snackbar = { data ->
                SnackBar(
                    title = data.visuals.message,
                    icon = painterResource(uiR.drawable.ic_email),
                    tonalColor = MaterialTheme.colorScheme.tertiary,
                )
            },
        )
    }
}

@Composable
private fun SuccessScreen(
    uiState: SettingsUiState.Success,
    scrollState: ScrollState,
    topPadding: Dp,
    onEvent: (event: SettingsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(top = topPadding + 8.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        GeneralButton(
            modifier = Modifier.padding(horizontal = 8.dp),
            title = stringResource(R.string.settings_security_title),
            iconPainter = painterResource(R.drawable.ic_lock),
            onClick = { onEvent(SettingsEvent.OnButtonSecurityClick) },
        )
        GeneralButton(
            modifier = Modifier.padding(horizontal = 8.dp),
            title = stringResource(R.string.settings_backup_title),
            iconPainter = painterResource(R.drawable.ic_cloud),
            onClick = { onEvent(SettingsEvent.OnButtonBackupClick) },
        )
        GeneralButton(
            modifier = Modifier.padding(horizontal = 8.dp),
            title = stringResource(R.string.settings_font_title),
            iconPainter = painterResource(R.drawable.ic_settings_font),
            onClick = { onEvent(SettingsEvent.OnButtonFontClick) },
        )
        GeneralButton(
            modifier = Modifier.padding(horizontal = 8.dp),
            title = stringResource(R.string.settings_note_content_title),
            iconPainter = painterResource(R.drawable.ic_note_content),
            onClick = { onEvent(SettingsEvent.OnButtonNoteSettingsClick) },
        )
        ThemeSelector(
            modifier = Modifier.padding(top = 10.dp, bottom = 4.dp),
            themes = uiState.themes,
            onThemeSelected = { onEvent(SettingsEvent.OnAppThemeSelected(it)) },
            onColorSelected = { onEvent(SettingsEvent.OnAppThemeColorSelected(it)) },
        )

        GeneralButton(
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp),
            title = stringResource(R.string.settings_language_title),
            iconPainter = painterResource(R.drawable.ic_language),
            hint = uiState.locale.text,
            onClick = { onEvent(SettingsEvent.OnLocaleClick) },
        )
        Rating(
            modifier = Modifier.padding(horizontal = 8.dp),
            rating = uiState.rating,
            onSelected = { onEvent(SettingsEvent.OnRatingSelected(it)) },
        )
        /* GeneralButton(
             modifier = Modifier.padding(top = 16.dp, start = 8.dp, end = 8.dp),
             title = stringResource(id = R.string.settings_donate_title),
             iconPainter = painterResource(id = R.drawable.ic_hart),
             onClick = {},
         )*/
        GeneralButton(
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(horizontal = 8.dp),
            title = stringResource(R.string.settings_feedback_title),
            iconPainter = painterResource(R.drawable.ic_mail),
            onClick = { onEvent(SettingsEvent.OnButtonReportIssueClick) },
        )
        GeneralButton(
            modifier = Modifier.padding(horizontal = 8.dp),
            title = stringResource(R.string.settings_terms_and_conditions_title),
            iconPainter = painterResource(uiR.drawable.ic_text_snippet),
            onClick = { onEvent(SettingsEvent.OnButtonTermsAndConditionsClick) },
        )
        GeneralButton(
            modifier = Modifier.padding(horizontal = 8.dp),
            title = stringResource(id = R.string.settings_privacy_policy_title),
            iconPainter = painterResource(uiR.drawable.ic_text_snippet),
            onClick = { onEvent(SettingsEvent.OnButtonPrivacyPolicyClick) },
        )
        Spacer(modifier = Modifier.weight(1f))
        Version(
            modifier = Modifier.padding(top = 8.dp),
            name = uiState.appVersion,
        )
    }
}

@Composable
private fun ThemeSelector(
    themes: List<UiTheme>,
    onThemeSelected: (theme: UiTheme) -> Unit,
    onColorSelected: (color: UiThemeColor) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedThemeIndex = themes.indexOfFirst { it.isSelected }
    val selectedTheme = themes[selectedThemeIndex]
    val selectedColorIndex = remember(themes, selectedTheme) {
        selectedTheme.colors.indexOfFirstOrNull { it == selectedTheme.selectedColor }
    }
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = selectedColorIndex ?: 0,
        initialFirstVisibleItemScrollOffset = -24.dp.dpToPx().toInt(),
    )
    SkipFirstEffect(selectedThemeIndex) {
        listState.scrollToItem(0)
    }
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_theme),
                contentDescription = null,
            )
            Text(
                text = stringResource(R.string.settings_theme_title),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            themes.forEach { theme ->
                TagItem(
                    title = when (theme) {
                        is UiTheme.Light -> stringResource(R.string.settings_light_theme_title)
                        is UiTheme.Dark -> stringResource(R.string.settings_dark_theme_title)
                    },
                    isRemovable = false,
                    textStyle = MaterialTheme.typography.bodySmall,
                    background = if (theme.isSelected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.secondaryContainer
                    },
                    textColor = if (theme.isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        Color.Unspecified
                    },
                    horizontalPadding = 20.dp,
                    onClick = { onThemeSelected(theme) },
                )
            }
        }
        LazyRow(
            modifier = Modifier.systemGestureExclusion(),
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 20.dp),
        ) {
            items(count = selectedTheme.colors.count()) { index ->
                ThemeItem(
                    theme = selectedTheme.colors[index],
                    selected = selectedTheme.selectedColor,
                    onSelected = onColorSelected,
                )
            }
        }
    }
}

@Composable
private fun ThemeItem(
    theme: UiThemeColor,
    selected: UiThemeColor?,
    onSelected: (color: UiThemeColor) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = modifier
            .size(50.dp)
            .clip(shape)
            .background(theme.surface)
            .applyIf(theme == selected) {
                Modifier.border(1.dp, theme.primaryContainer, shape)
            }
            .clickableNoRipple { onSelected(theme) },
    )
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
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(5) { index ->
                val isFilled by remember(rating, index) { mutableStateOf(rating >= index + 1) }
                val scale = remember { Animatable(1f) }
                SkipFirstEffect(rating, isFilled) {
                    if (isFilled) {
                        delay(50L * index)
                        scale.animateTo(
                            targetValue = 1.15f,
                            animationSpec = tween(durationMillis = 150),
                        )
                        scale.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(durationMillis = 150),
                        )
                    }
                }

                Icon(
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = scale.value
                            scaleY = scale.value
                        }
                        .clickableNoRipple { onSelected(index + 1) },
                    painter = if (isFilled) {
                        painterResource(R.drawable.ic_star_filled)
                    } else {
                        painterResource(R.drawable.ic_star_outlined)
                    },
                    contentDescription = (index + 1).toString(),
                    tint = MaterialTheme.colorScheme.surfaceContainer,
                )
            }
        }
    }
}

@Composable
private fun Version(
    name: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            modifier = Modifier.alpha(0.5f),
            text = stringResource(R.string.settings_version_title, name),
            style = MaterialTheme.typography.labelSmall,
        )
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
                themes = listOf(
                    UiTheme.Light(
                        colors = listOf(
                            UiThemeColor.LIGHT_BLUE,
                        ),
                        selectedColor = UiThemeColor.DISTANT_CASTLE_GREEN,
                        isSelected = false,
                    ),
                    UiTheme.Dark(
                        colors = listOf(
                            UiThemeColor.SCANDI_GRANDPA_GRAY_DARK,
                            UiThemeColor.DISTANT_CASTLE_GREEN,
                            UiThemeColor.VAMPIRE_RED_DARK,
                            UiThemeColor.EUPHORIA_BLUE_DARK,
                            UiThemeColor.EUPHORIA_VIOLET,
                            UiThemeColor.EUPHORIA_BLUE,
                            UiThemeColor.EUPHORIA_PINK,
                        ),
                        selectedColor = UiThemeColor.DISTANT_CASTLE_GREEN,
                        isSelected = true,
                    )
                ),
                rating = 4,
                appVersion = "1.0",
                locale = AppLocale.ENGLISH,
            ),
        )
    }
}
