package com.furianrt.backup.internal.ui

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.IntentSender
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.furianrt.backup.R
import com.furianrt.backup.internal.domain.entities.BackupPeriod
import com.furianrt.backup.internal.domain.exceptions.AuthException
import com.furianrt.backup.internal.ui.BackupUiState.Success.SyncProgress
import com.furianrt.backup.internal.ui.composables.BackupDate
import com.furianrt.backup.internal.ui.composables.BackupPeriod
import com.furianrt.backup.internal.ui.composables.BackupPeriodDialog
import com.furianrt.backup.internal.ui.composables.ConfirmBackupDialog
import com.furianrt.backup.internal.ui.composables.ConfirmSignOutDialog
import com.furianrt.backup.internal.ui.composables.Header
import com.furianrt.backup.internal.ui.composables.QuestionsList
import com.furianrt.backup.internal.ui.composables.RestoreButton
import com.furianrt.backup.internal.ui.composables.SyncButton
import com.furianrt.backup.internal.ui.entities.Question
import com.furianrt.uikit.anim.ShakingState
import com.furianrt.uikit.anim.rememberShakingState
import com.furianrt.uikit.anim.shakable
import com.furianrt.uikit.components.DefaultToolbar
import com.furianrt.uikit.components.MovableToolbarScaffold
import com.furianrt.uikit.components.MovableToolbarState
import com.furianrt.uikit.components.SkipFirstEffect
import com.furianrt.uikit.components.SnackBar
import com.furianrt.uikit.components.SwitchWithLabel
import com.furianrt.uikit.extensions.pxToDp
import com.furianrt.uikit.theme.SerenityTheme
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.furianrt.uikit.R as uiR

@Composable
internal fun BackupScreen(
    onCloseRequest: () -> Unit,
) {
    val viewModel: BackupViewModel = hiltViewModel()
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val hapticFeedback = LocalHapticFeedback.current
    val hazeState = remember { HazeState() }
    var showSignOutConfirmationDialog by remember { mutableStateOf(false) }

    val authLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                viewModel.onEvent(BackupScreenEvent.OnBackupResolutionComplete(result.data))
            }

            Activity.RESULT_CANCELED -> {
                viewModel.onEvent(
                    BackupScreenEvent.OnBackupResolutionFailure(
                        AuthException.ResolutionCanceled()
                    )
                )
            }
        }
    }

    val snackBarHostState = remember { SnackbarHostState() }
    var showBackupPeriodDialog by remember { mutableStateOf(false) }
    var showConfirmBackupDialog by remember { mutableStateOf(false) }

    val onCloseRequestState by rememberUpdatedState(onCloseRequest)

    LaunchedEffect(Unit) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->
                when (effect) {
                    is BackupEffect.CloseScreen -> onCloseRequestState()
                    is BackupEffect.ShowConfirmSignOutDialog -> showSignOutConfirmationDialog = true
                    is BackupEffect.ShowBackupPeriodDialog -> showBackupPeriodDialog = true
                    is BackupEffect.ShowBackupResolution -> {
                        try {
                            val intentSenderRequest = IntentSenderRequest
                                .Builder(effect.intentSender)
                                .build()
                            authLauncher.launch(intentSenderRequest)
                        } catch (e: IntentSender.SendIntentException) {
                            val error = AuthException.SendIntentException()
                            viewModel.onEvent(BackupScreenEvent.OnBackupResolutionFailure(error))
                        } catch (e: ActivityNotFoundException) {
                            val error = AuthException.ActivityNotFoundException()
                            viewModel.onEvent(BackupScreenEvent.OnBackupResolutionFailure(error))
                        }
                    }

                    is BackupEffect.ShowErrorToast -> {
                        snackBarHostState.currentSnackbarData?.dismiss()
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.Reject)
                        snackBarHostState.showSnackbar(
                            message = effect.text,
                            duration = SnackbarDuration.Short,
                        )
                    }

                    is BackupEffect.ShowConfirmBackupDialog -> showConfirmBackupDialog = true
                }
            }
    }

    ScreenContent(
        modifier = Modifier.haze(hazeState),
        uiState = uiState,
        snackBarHostState = snackBarHostState,
        onEvent = viewModel::onEvent,
    )
    if (showSignOutConfirmationDialog) {
        ConfirmSignOutDialog(
            hazeState = hazeState,
            onDismissRequest = { showSignOutConfirmationDialog = false },
            onConfirmClick = { viewModel.onEvent(BackupScreenEvent.OnSignOutConfirmClick) },
        )
    }
    if (showBackupPeriodDialog) {
        BackupPeriodDialog(
            selectedPeriod = (viewModel.state.value as? BackupUiState.Success)?.backupPeriod
                ?: BackupPeriod.TreeDays,
            hazeState = hazeState,
            onPeriodSelected = { viewModel.onEvent(BackupScreenEvent.OnBackupPeriodSelected(it)) },
            onDismissRequest = { showBackupPeriodDialog = false },
        )
    }
    if (showConfirmBackupDialog) {
        ConfirmBackupDialog(
            hazeState = hazeState,
            onDismissRequest = { showConfirmBackupDialog = false },
            onConfirmClick = { viewModel.onEvent(BackupScreenEvent.OnConfirmBackupClick) },
        )
    }
}

@Composable
private fun ScreenContent(
    uiState: BackupUiState,
    modifier: Modifier = Modifier,
    snackBarHostState: SnackbarHostState = SnackbarHostState(),
    onEvent: (event: BackupScreenEvent) -> Unit = {},
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
                title = stringResource(R.string.backup_google_drive_title),
                onBackClick = { onEvent(BackupScreenEvent.OnButtonBackClick) },
            )
        }
    ) { topPadding ->
        when (uiState) {
            is BackupUiState.Success -> SuccessContent(
                uiState = uiState,
                onEvent = onEvent,
                scrollState = scrollState,
                snackBarHostState = snackBarHostState,
                toolbarPadding = topPadding,
            )

            is BackupUiState.Loading -> LoadingContent()
        }
        SnackbarHost(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding(),
            hostState = snackBarHostState,
            snackbar = { data ->
                SnackBar(
                    title = data.visuals.message,
                    icon = painterResource(uiR.drawable.ic_error),
                    tonalColor = MaterialTheme.colorScheme.tertiaryContainer,
                )
            },
        )
    }
}

@Composable
private fun SuccessContent(
    uiState: BackupUiState.Success,
    scrollState: ScrollState,
    snackBarHostState: SnackbarHostState,
    toolbarPadding: Dp,
    modifier: Modifier = Modifier,
    onEvent: (event: BackupScreenEvent) -> Unit = {},
) {
    val hapticFeedback = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    val hazeState = remember { HazeState() }
    var backupBlockHeight by remember { mutableIntStateOf(0) }

    val backupShakeState = rememberShakingState(
        strength = ShakingState.Strength.Strong,
        direction = ShakingState.Direction.LEFT_THEN_RIGHT,
    )

    val restoreShakeState = rememberShakingState(
        strength = ShakingState.Strength.Strong,
        direction = ShakingState.Direction.LEFT_THEN_RIGHT,
    )

    val errorText = stringResource(uiR.string.general_error)
    val successText = stringResource(R.string.backup_success_message)

    SkipFirstEffect(uiState.hasSyncError) {
        if (uiState.syncProgress is SyncProgress.Failure) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.Reject)
            if (uiState.syncProgress.backup) {
                backupShakeState.shake()
            }
            if (uiState.syncProgress.restore) {
                restoreShakeState.shake()
            }
            snackBarHostState.showSnackbar(
                message = errorText,
                duration = SnackbarDuration.Short,
            )
        }
    }

    LaunchedEffect(uiState.isSyncSuccess) {
        if (uiState.isSyncSuccess) {
            scope.launch {
                snackBarHostState.showSnackbar(
                    message = successText,
                    duration = SnackbarDuration.Short,
                )
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .haze(hazeState)
                .verticalScroll(scrollState)
                .padding(top = toolbarPadding, bottom = backupBlockHeight.pxToDp()),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Header(
                modifier = Modifier.padding(top = 4.dp),
                authState = uiState.authState,
                onSingInClick = { onEvent(BackupScreenEvent.OnSignInClick) },
                onSingOutClick = { onEvent(BackupScreenEvent.OnSignOutClick) },
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(16.dp))
                SwitchWithLabel(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    title = stringResource(R.string.backup_auto_backup_title),
                    isChecked = uiState.isAutoBackupEnabled,
                    onCheckedChange = { isChecked ->
                        if (isChecked) {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.ToggleOn)
                        } else {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.ToggleOff)
                        }
                        onEvent(BackupScreenEvent.OnAutoBackupCheckChange(isChecked))
                    },
                    enabled = uiState.isSignedIn,
                )
                Spacer(Modifier.height(8.dp))
                BackupPeriod(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    period = uiState.backupPeriod,
                    isEnabled = uiState.isSignedIn && uiState.isAutoBackupEnabled,
                    onClick = { onEvent(BackupScreenEvent.OnBackupPeriodClick) },
                )
                Spacer(Modifier.height(10.dp))
                RestoreButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .shakable(restoreShakeState),
                    isEnabled = uiState.isSignedIn && !uiState.isSyncInProgress,
                    onClick = {
                        if (!uiState.isSyncInProgress) {
                            onEvent(BackupScreenEvent.OnButtonRestoreClick)
                        }
                    },
                )
                if (uiState.questions.isNotEmpty()) {
                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 24.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                    Spacer(Modifier.height(24.dp))
                    QuestionsList(
                        modifier = Modifier.padding(start = 24.dp, end = 24.dp),
                        questions = uiState.questions,
                        isSignedIn = uiState.isSignedIn,
                        onQuestionClick = { onEvent(BackupScreenEvent.OnQuestionClick(it)) },
                    )
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .hazeChild(
                    state = hazeState,
                    style = HazeDefaults.style(
                        backgroundColor = MaterialTheme.colorScheme.surface,
                        tint = HazeTint(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
                        noiseFactor = 0f,
                        blurRadius = 12.dp,
                    ),
                )
                .background(MaterialTheme.colorScheme.outlineVariant)
                .onSizeChanged { backupBlockHeight = it.height }
                .padding(bottom = 24.dp)
                .align(Alignment.BottomCenter)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.tertiary,
            )
            SyncButton(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .shakable(backupShakeState),
                text = when (val progress = uiState.syncProgress) {
                    is SyncProgress.BackupStarting -> {
                        stringResource(R.string.backup_starting_message)
                    }

                    is SyncProgress.RestoreStarting -> {
                        stringResource(R.string.restore_starting_message)
                    }

                    is SyncProgress.BackupProgress -> stringResource(
                        R.string.backup_progress_message,
                        progress.syncedNotesCount,
                        progress.totalNotesCount,
                    )

                    is SyncProgress.RestoreProgress -> stringResource(
                        R.string.restore_progress_message,
                        progress.syncedNotesCount,
                        progress.totalNotesCount,
                    )

                    else -> stringResource(R.string.backup_backup_data_title)
                },
                progress = when (val progress = uiState.syncProgress) {
                    is SyncProgress.BackupStarting, SyncProgress.RestoreStarting -> 0f
                    is SyncProgress.BackupProgress -> {
                        progress.syncedNotesCount / progress.totalNotesCount.toFloat()
                    }
                    is SyncProgress.RestoreProgress -> {
                        progress.syncedNotesCount / progress.totalNotesCount.toFloat()
                    }

                    else -> null
                },
                isEnabled = uiState.isSignedIn,
                onClick = {
                    if (!uiState.isSyncInProgress) {
                        onEvent(BackupScreenEvent.OnButtonBackupClick)
                    }
                },
            )
            BackupDate(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .alpha(if (uiState.isSignedIn) 1f else 0.5f),
                date = uiState.lastSyncDate,
            )
        }
    }
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier)
}

@Composable
@Preview
private fun PreviewSignedIn() {
    SerenityTheme {
        ScreenContent(
            uiState = BackupUiState.Success(
                isAutoBackupEnabled = true,
                backupPeriod = BackupPeriod.TreeDays,
                lastSyncDate = BackupUiState.Success.SyncDate.None,
                questions = buildPreviewQuestionsList(expandedIndex = 0),
                authState = BackupUiState.Success.AuthState.SignedIn(
                    email = "felmemfmelflmfe",
                    isLoading = false,
                ),
                syncProgress = SyncProgress.Idle,
            ),
        )
    }
}

@Composable
@Preview
private fun PreviewSignedOut() {
    SerenityTheme {
        ScreenContent(
            uiState = BackupUiState.Success(
                isAutoBackupEnabled = true,
                backupPeriod = BackupPeriod.TreeDays,
                lastSyncDate = BackupUiState.Success.SyncDate.None,
                questions = buildPreviewQuestionsList(expandedIndex = 1),
                authState = BackupUiState.Success.AuthState.SignedOut(isLoading = false),
                syncProgress = SyncProgress.Idle,
            ),
        )
    }
}

@Composable
@Preview
private fun PreviewLoading() {
    SerenityTheme {
        ScreenContent(
            uiState = BackupUiState.Success(
                isAutoBackupEnabled = false,
                backupPeriod = BackupPeriod.TreeDays,
                lastSyncDate = BackupUiState.Success.SyncDate.None,
                questions = buildPreviewQuestionsList(expandedIndex = null),
                authState = BackupUiState.Success.AuthState.SignedIn(
                    email = "felmemfmelflmfe",
                    isLoading = true,
                ),
                syncProgress = SyncProgress.Idle,
            ),
        )
    }
}

@Composable
private fun buildPreviewQuestionsList(expandedIndex: Int?) = persistentListOf(
    Question(
        id = "",
        title = stringResource(R.string.backup_popular_question_1_title),
        answer = stringResource(R.string.backup_popular_question_1_answer),
        isExpanded = expandedIndex == 0,
    ),
    Question(
        id = "",
        title = stringResource(R.string.backup_popular_question_2_title),
        answer = stringResource(R.string.backup_popular_question_2_answer),
        isExpanded = expandedIndex == 1,
    ),
    Question(
        id = "",
        title = stringResource(R.string.backup_popular_question_3_title),
        answer = stringResource(R.string.backup_popular_question_3_answer),
        isExpanded = expandedIndex == 2,
    ),
)
