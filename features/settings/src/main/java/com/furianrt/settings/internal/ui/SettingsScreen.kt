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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import com.furianrt.domain.entities.AppLocale
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.settings.R
import com.furianrt.settings.internal.ui.composables.AppFontDialog
import com.furianrt.settings.internal.ui.composables.BadRatingDialog
import com.furianrt.settings.internal.ui.composables.LocaleDialog
import com.furianrt.uikit.components.DefaultToolbar
import com.furianrt.uikit.components.GeneralButton
import com.furianrt.uikit.components.MovableToolbarScaffold
import com.furianrt.uikit.components.MovableToolbarState
import com.furianrt.uikit.components.SkipFirstEffect
import com.furianrt.uikit.components.SnackBar
import com.furianrt.uikit.entities.UiThemeColor
import com.furianrt.uikit.extensions.applyIf
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.extensions.dpToPx
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.IntentCreator
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import com.furianrt.uikit.R as uiR

private data class FontsDialogState(
    val fonts: ImmutableList<UiNoteFontFamily>,
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
                        subject = context.getString(
                            R.string.settings_feedback_email_subject,
                            effect.text,
                        ),
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
                }
            }
    }

    ScreenContent(
        modifier = Modifier.haze(hazeState),
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

    MovableToolbarScaffold(
        modifier = modifier.background(MaterialTheme.colorScheme.surface),
        state = toolbarState,
        listState = scrollState,
        enabled = false,
        toolbar = {
            DefaultToolbar(
                modifier = Modifier.statusBarsPadding(),
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
        GeneralButton(
            modifier = Modifier.padding(horizontal = 8.dp),
            title = stringResource(id = R.string.settings_font_title),
            iconPainter = painterResource(id = R.drawable.font_svgrepo_com),
            onClick = { onEvent(SettingsEvent.OnButtonFontClick) },
        )
        ThemeSelector(
            modifier = Modifier.padding(vertical = 4.dp),
            themes = uiState.themes,
            selected = uiState.selectedThemeColor,
            onSelected = { onEvent(SettingsEvent.OnAppThemeColorSelected(it)) },
        )

        GeneralButton(
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 12.dp),
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
            title = stringResource(id = R.string.settings_feedback_title),
            iconPainter = painterResource(id = R.drawable.ic_mail),
            onClick = { onEvent(SettingsEvent.OnButtonFeedbackClick) },
        )
        GeneralButton(
            modifier = Modifier.padding(horizontal = 8.dp),
            title = stringResource(id = R.string.settings_terms_and_conditions_title),
            iconPainter = painterResource(id = uiR.drawable.ic_text_snippet),
            onClick = { onEvent(SettingsEvent.OnButtonTermsAndConditionsClick) },
        )
        GeneralButton(
            modifier = Modifier.padding(horizontal = 8.dp),
            title = stringResource(id = R.string.settings_privacy_policy_title),
            iconPainter = painterResource(id = uiR.drawable.ic_text_snippet),
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
    themes: ImmutableList<UiThemeColor>,
    selected: UiThemeColor,
    onSelected: (color: UiThemeColor) -> Unit,
    modifier: Modifier = Modifier,
) {
    val initialIndex = remember(themes) { themes.indexOf(selected) }
    Column(
        modifier = modifier.fillMaxWidth(),
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
                initialFirstVisibleItemScrollOffset = -24.dp.dpToPx().toInt(),
            ),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
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
            .background(theme.primary)
            .applyIf(theme == selected) {
                Modifier.border(1.dp, theme.accent, shape)
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
                themes = persistentListOf(
                    UiThemeColor.SCANDI_GRANDPA_GRAY_DARK,
                    UiThemeColor.DISTANT_CASTLE_GREEN,
                    UiThemeColor.VAMPIRE_RED_DARK,
                    UiThemeColor.EUPHORIA_BLUE_DARK,
                    UiThemeColor.EUPHORIA_VIOLET,
                    UiThemeColor.EUPHORIA_BLUE,
                    UiThemeColor.EUPHORIA_PINK,
                ),
                selectedThemeColor = UiThemeColor.DISTANT_CASTLE_GREEN,
                rating = 4,
                appVersion = "1.0",
                locale = AppLocale.ENGLISH,
            ),
        )
    }
}
