package com.furianrt.settings.internal.ui

import android.appwidget.AppWidgetManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.annotation.IntRange
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.furianrt.common.ErrorTracker
import com.furianrt.domain.entities.AppLocale
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.permissions.extensions.openAppSettingsScreen
import com.furianrt.settings.R
import com.furianrt.settings.internal.ui.composables.AppFontDialog
import com.furianrt.settings.internal.ui.composables.BadRatingDialog
import com.furianrt.settings.internal.ui.composables.LocaleDialog
import com.furianrt.settings.internal.ui.composables.WidgetErrorDialog
import com.furianrt.uikit.components.AppBackground
import com.furianrt.uikit.components.DefaultToolbar
import com.furianrt.uikit.components.MovableToolbarScaffold
import com.furianrt.uikit.components.OptionButtonWrapper
import com.furianrt.uikit.components.SkipFirstEffect
import com.furianrt.uikit.components.SnackBar
import com.furianrt.uikit.entities.UiThemeColor
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.IntentCreator
import com.furianrt.uikit.utils.PreviewWithBackground
import com.furianrt.widgets.internal.widgets.AllActionsWidgetReceiver
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlin.time.Duration.Companion.milliseconds
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
    openAppThemeScreen: () -> Unit,
    openRemindersScreen: () -> Unit,
    openGalleryScreen: () -> Unit,
    onCloseRequest: () -> Unit,
) {
    val viewModel: SettingsViewModel = hiltViewModel()
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val hazeState = rememberHazeState()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val hapticFeedback = LocalHapticFeedback.current

    val onCloseRequestState by rememberUpdatedState(onCloseRequest)
    val openBackupScreenState by rememberUpdatedState(openBackupScreen)
    val openSecurityScreenState by rememberUpdatedState(openSecurityScreen)
    val openNoteSettingsScreenState by rememberUpdatedState(openNoteSettingsScreen)
    val openAppThemeScreenState by rememberUpdatedState(openAppThemeScreen)
    val openRemindersScreenState by rememberUpdatedState(openRemindersScreen)
    val openGalleryScreenState by rememberUpdatedState(openGalleryScreen)

    val snackBarHostState = remember { SnackbarHostState() }
    var showBadRatingDialog by remember { mutableStateOf(false) }
    var showWidgetErrorDialog by remember { mutableStateOf(false) }
    var fontsDialogState: FontsDialogState? by remember { mutableStateOf(null) }
    var localeDialogState: LocaleDialogState? by remember { mutableStateOf(null) }

    val sendEmailErrorMessage = stringResource(uiR.string.send_email_error)
    val appNotFoundMessage = stringResource(uiR.string.app_not_found_error)

    LaunchedEffect(Unit) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->
                when (effect) {
                    is SettingsEffect.CloseScreen -> onCloseRequestState()
                    is SettingsEffect.OpenSecurityScreen -> openSecurityScreenState()
                    is SettingsEffect.OpenBackupScreen -> openBackupScreenState()
                    is SettingsEffect.OpenRemindersScreen -> openRemindersScreenState()
                    is SettingsEffect.OpenAppThemeScreen -> openAppThemeScreenState()
                    is SettingsEffect.SendFeedbackEmail -> IntentCreator.emailIntent(
                        email = effect.supportEmail,
                        subject = effect.text,
                    ).onSuccess { intent ->
                        context.startActivity(intent)
                    }.onFailure { error ->
                        ErrorTracker.printStackTrace(error)
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.Reject)
                        snackBarHostState.currentSnackbarData?.dismiss()
                        snackBarHostState.showSnackbar(
                            message = sendEmailErrorMessage,
                            duration = SnackbarDuration.Short,
                        )
                    }

                    is SettingsEffect.OpenMarketPage -> openAppMarketPage(context, effect.url)
                    is SettingsEffect.ShowBadRatingDialog -> showBadRatingDialog = true
                    is SettingsEffect.ShowWidgetErrorDialog -> showWidgetErrorDialog = true
                    is SettingsEffect.ShowFontDialog -> fontsDialogState = FontsDialogState(
                        fonts = effect.fonts,
                        selectedFont = effect.selectedFont,
                    )

                    is SettingsEffect.OpenLink -> try {
                        val intent = Intent(Intent.ACTION_VIEW, effect.url.toUri())
                        context.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        ErrorTracker.printStackTrace(e)
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.Reject)
                        snackBarHostState.currentSnackbarData?.dismiss()
                        snackBarHostState.showSnackbar(
                            message = appNotFoundMessage,
                            duration = SnackbarDuration.Short,
                        )
                    }

                    is SettingsEffect.ShowLocaleDialog -> {
                        localeDialogState = LocaleDialogState(effect.locale, effect.selectedLocale)
                    }

                    is SettingsEffect.OpenNoteSettingsScreen -> openNoteSettingsScreenState()
                    is SettingsEffect.ShowWidgetsDialog -> {
                        val provider = ComponentName(context, AllActionsWidgetReceiver::class.java)
                        val appWidgetManager = AppWidgetManager.getInstance(context)
                        if (!appWidgetManager.requestPinAppWidget(provider, null, null)) {
                            viewModel.onEvent(SettingsEvent.OnAddWidgetError)
                        }
                    }

                    is SettingsEffect.OpenGalleryScreen -> openGalleryScreenState()
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
    if (showWidgetErrorDialog) {
        WidgetErrorDialog(
            hazeState = hazeState,
            onSettingsClick = context::openAppSettingsScreen,
            onDismissRequest = { showWidgetErrorDialog = false },
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
    val hazeState = rememberHazeState()

    val statusBarPv = WindowInsets.statusBars.asPaddingValues()
    val statusBarHeight = rememberSaveable { statusBarPv.calculateTopPadding().value }

    MovableToolbarScaffold(
        modifier = modifier,
        listState = scrollState,
        enabled = false,
        toolbar = {
            DefaultToolbar(
                modifier = Modifier.padding(top = statusBarHeight.dp),
                title = stringResource(uiR.string.profile_title),
                onBackClick = { onEvent(SettingsEvent.OnButtonBackClick) },
            )
        }
    ) { topPadding ->
        AppBackground(
            modifier = Modifier.hazeSource(hazeState),
            theme = uiState.theme,
        )
        when (uiState.content) {
            is SettingsUiState.Content.Success -> SuccessScreen(
                uiState = uiState.content,
                scrollState = scrollState,
                topPadding = topPadding,
                hazeState = hazeState,
                onEvent = onEvent,
            )

            is SettingsUiState.Content.Loading -> LoadingScreen()
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
    uiState: SettingsUiState.Content.Success,
    scrollState: ScrollState,
    topPadding: Dp,
    hazeState: HazeState,
    onEvent: (event: SettingsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(start = 16.dp, end = 16.dp, top = topPadding + 12.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        OptionButtonWrapper(
            modifier = Modifier.fillMaxWidth(),
            hazeState = hazeState,
        ) {
            OptionButton(
                title = stringResource(R.string.settings_security_title),
                icon = painterResource(R.drawable.ic_lock),
                onClick = { onEvent(SettingsEvent.OnButtonSecurityClick) },
            )
            OptionButtonDivider(
                modifier = Modifier.padding(horizontal = 24.dp),
            )
            OptionButton(
                title = stringResource(R.string.settings_backup_title),
                icon = painterResource(R.drawable.ic_cloud),
                onClick = { onEvent(SettingsEvent.OnButtonBackupClick) },
            )
        }
        OptionButtonWrapper(
            modifier = Modifier.fillMaxWidth(),
            hazeState = hazeState,
        ) {
            OptionButton(
                title = stringResource(R.string.settings_reminders_title),
                icon = painterResource(R.drawable.ic_reminders),
                onClick = { onEvent(SettingsEvent.OnButtonRemindersClick) },
            )
        }
        OptionButtonWrapper(
            modifier = Modifier.fillMaxWidth(),
            hazeState = hazeState,
        ) {
            OptionButton(
                title = stringResource(uiR.string.action_gallery),
                icon = painterResource(uiR.drawable.ic_photos),
                onClick = { onEvent(SettingsEvent.OnButtonGalleryClick) },
            )
        }
        OptionButtonWrapper(
            modifier = Modifier.fillMaxWidth(),
            hazeState = hazeState,
        ) {
            OptionButton(
                title = stringResource(uiR.string.title_theme),
                icon = painterResource(R.drawable.ic_theme),
                onClick = { onEvent(SettingsEvent.OnButtonThemeClick) },
            )
            OptionButtonDivider(
                modifier = Modifier.padding(horizontal = 24.dp),
            )
            OptionButton(
                title = stringResource(R.string.settings_font_title),
                icon = painterResource(R.drawable.ic_settings_font),
                onClick = { onEvent(SettingsEvent.OnButtonFontClick) },
            )
            OptionButtonDivider(
                modifier = Modifier.padding(horizontal = 24.dp),
            )
            OptionButton(
                title = stringResource(R.string.settings_note_content_title),
                icon = painterResource(R.drawable.ic_note_content),
                onClick = { onEvent(SettingsEvent.OnButtonNoteSettingsClick) },
            )
        }
        if (uiState.showWidgetsButton) {
            OptionButtonWrapper(
                modifier = Modifier.fillMaxWidth(),
                hazeState = hazeState,
            ) {
                OptionButton(
                    title = stringResource(uiR.string.title_widgets),
                    icon = painterResource(uiR.drawable.ic_widgets),
                    onClick = { onEvent(SettingsEvent.OnButtonWidgetsClick) },
                )
            }
        }
        OptionButtonWrapper(
            modifier = Modifier.fillMaxWidth(),
            hazeState = hazeState,
        ) {
            OptionButton(
                title = stringResource(R.string.settings_language_title),
                icon = painterResource(R.drawable.ic_language),
                onClick = { onEvent(SettingsEvent.OnLocaleClick) },
            )
        }
        OptionButtonWrapper(
            modifier = Modifier.fillMaxWidth(),
            hazeState = hazeState,
        ) {
            Rating(
                modifier = Modifier.padding(
                    start = 24.dp,
                    end = 24.dp,
                    top = 16.dp,
                    bottom = 20.dp,
                ),
                rating = uiState.rating,
                onSelected = { onEvent(SettingsEvent.OnRatingSelected(it)) },
            )
        }
        OptionButtonWrapper(
            modifier = Modifier.fillMaxWidth(),
            hazeState = hazeState,
        ) {
            OptionButton(
                title = stringResource(R.string.settings_feedback_title),
                icon = painterResource(R.drawable.ic_mail),
                onClick = { onEvent(SettingsEvent.OnButtonReportIssueClick) },
            )
            OptionButtonDivider(
                modifier = Modifier.padding(horizontal = 24.dp),
            )
            OptionButton(
                title = stringResource(R.string.settings_terms_and_conditions_title),
                icon = painterResource(uiR.drawable.ic_text_snippet),
                onClick = { onEvent(SettingsEvent.OnButtonTermsAndConditionsClick) },
            )
            OptionButtonDivider(
                modifier = Modifier.padding(horizontal = 24.dp),
            )
            OptionButton(
                title = stringResource(R.string.settings_privacy_policy_title),
                icon = painterResource(uiR.drawable.ic_text_snippet),
                onClick = { onEvent(SettingsEvent.OnButtonPrivacyPolicyClick) },
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Version(
            name = uiState.appVersion,
            hazeState = hazeState,
        )
    }
}

@Composable
private fun Rating(
    @IntRange(0, 5) rating: Int,
    onSelected: (rating: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
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
                        delay((50L * index).milliseconds)
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
private fun OptionButton(
    title: String,
    icon: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = title,
            style = MaterialTheme.typography.bodyMedium,
        )
        Icon(
            modifier = Modifier.size(26.dp),
            painter = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun OptionButtonDivider(
    modifier: Modifier = Modifier,
) {
    HorizontalDivider(
        modifier = modifier,
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
    )
}

@Composable
private fun Version(
    name: String,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .hazeEffect(
                    state = hazeState,
                    style = HazeDefaults.style(
                        backgroundColor = MaterialTheme.colorScheme.surface,
                        blurRadius = 16.dp,
                        noiseFactor = 0f,
                        tint = HazeTint(Color.Transparent),
                    ),
                )
                .padding(horizontal = 6.dp)
                .alpha(0.5f),
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
    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(context, R.string.settings_app_store_open_error, Toast.LENGTH_SHORT).show()
    }
}

@Composable
@PreviewWithBackground
private fun ScreenContentPreview() {
    SerenityTheme {
        ScreenContent(
            snackBarHostState = SnackbarHostState(),
            uiState = SettingsUiState(
                theme = UiThemeColor.defaultTheme,
                content = SettingsUiState.Content.Success(
                    rating = 4,
                    appVersion = "1.0",
                    showWidgetsButton = true,
                ),
            ),
        )
    }
}
