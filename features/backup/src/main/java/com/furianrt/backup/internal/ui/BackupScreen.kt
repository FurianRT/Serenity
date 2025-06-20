package com.furianrt.backup.internal.ui

import android.content.ActivityNotFoundException
import android.content.IntentSender
import android.view.HapticFeedbackConstants
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateBounds
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.furianrt.backup.internal.ui.composables.SyncButton
import com.furianrt.backup.internal.ui.composables.SyncError
import com.furianrt.backup.internal.ui.entities.Question
import com.furianrt.uikit.components.DefaultToolbar
import com.furianrt.uikit.components.MovableToolbarScaffold
import com.furianrt.uikit.components.MovableToolbarState
import com.furianrt.uikit.components.SkipFirstEffect
import com.furianrt.uikit.components.SnackBar
import com.furianrt.uikit.components.SwitchWithLabel
import com.furianrt.uikit.theme.SerenityTheme
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import kotlinx.collections.immutable.persistentListOf
import com.furianrt.uikit.R as uiR

@Composable
internal fun BackupScreen(
    onCloseRequest: () -> Unit,
) {
    val viewModel: BackupViewModel = hiltViewModel()
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    val view = LocalView.current
    val hazeState = remember { HazeState() }
    var showSignOutConfirmationDialog by remember { mutableStateOf(false) }

    val authLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            viewModel.onEvent(BackupScreenEvent.OnBackupResolutionComplete(result.data))
        }
    }

    val snackBarHostState = remember { SnackbarHostState() }
    var showBackupPeriodDialog by remember { mutableStateOf(false) }
    var showConfirmBackupDialog by remember { mutableStateOf(false) }

    val onCloseRequestState by rememberUpdatedState(onCloseRequest)

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
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
                    view.performHapticFeedback(HapticFeedbackConstants.REJECT)
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

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SuccessContent(
    uiState: BackupUiState.Success,
    scrollState: ScrollState,
    toolbarPadding: Dp,
    modifier: Modifier = Modifier,
    onEvent: (event: BackupScreenEvent) -> Unit = {},
) {
    val view = LocalView.current

    SkipFirstEffect(uiState.hasSyncError) {
        if (uiState.hasSyncError) {
            view.performHapticFeedback(HapticFeedbackConstants.REJECT)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(top = toolbarPadding),
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
                )
                .padding(vertical = 16.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SwitchWithLabel(
                modifier = Modifier.padding(horizontal = 12.dp),
                title = stringResource(R.string.backup_auto_backup_title),
                isChecked = uiState.isAutoBackupEnabled,
                onCheckedChange = { isChecked ->
                    view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                    onEvent(BackupScreenEvent.OnAutoBackupCheckChange(isChecked))
                },
                enabled = uiState.isSignedIn,
            )
            Spacer(Modifier.height(16.dp))
            BackupPeriod(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                period = uiState.backupPeriod,
                isEnabled = uiState.isSignedIn && uiState.isAutoBackupEnabled,
                onClick = { onEvent(BackupScreenEvent.OnBackupPeriodClick) },
            )
            Spacer(Modifier.height(40.dp))
            LookaheadScope {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .animateContentSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    AnimatedVisibility(
                        modifier = Modifier.weight(1f),
                        visible = !uiState.isRestoreInProgress,
                        enter = fadeIn(tween(500)),
                        exit = ExitTransition.None,
                    ) {
                        SyncButton(
                            modifier = Modifier.animateBounds(this@LookaheadScope),
                            text = when (val progress = uiState.syncProgress) {
                                is SyncProgress.BackupStarting -> {
                                    stringResource(R.string.backup_starting_message)
                                }

                                is SyncProgress.BackupProgress -> stringResource(
                                    R.string.backup_progress_message,
                                    progress.syncedNotesCount,
                                    progress.totalNotesCount,
                                )

                                else -> stringResource(uiR.string.action_backup)
                            },
                            progress = when (val progress = uiState.syncProgress) {
                                is SyncProgress.BackupStarting -> 0f
                                is SyncProgress.BackupProgress -> {
                                    progress.syncedNotesCount / progress.totalNotesCount.toFloat()
                                }

                                else -> null
                            },
                            hasError = uiState.syncProgress is SyncProgress.Failure &&
                                    uiState.syncProgress.backup,
                            isEnabled = uiState.isSignedIn,
                            onClick = {
                                if (!uiState.isSyncInProgress) {
                                    onEvent(BackupScreenEvent.OnButtonBackupClick)
                                }
                            },
                        )
                    }
                    AnimatedVisibility(
                        modifier = Modifier.weight(1f),
                        visible = !uiState.isBackupInProgress,
                        enter = fadeIn(tween(500)),
                        exit = ExitTransition.None,
                    ) {
                        SyncButton(
                            modifier = Modifier.animateBounds(this@LookaheadScope),
                            text = when (val progress = uiState.syncProgress) {
                                is SyncProgress.RestoreStarting -> {
                                    stringResource(R.string.restore_starting_message)
                                }

                                is SyncProgress.RestoreProgress -> stringResource(
                                    R.string.restore_progress_message,
                                    progress.syncedNotesCount,
                                    progress.totalNotesCount,
                                )

                                else -> stringResource(uiR.string.action_restore)
                            },
                            progress = when (val progress = uiState.syncProgress) {
                                is SyncProgress.RestoreStarting -> 0f
                                is SyncProgress.RestoreProgress -> {
                                    progress.syncedNotesCount / progress.totalNotesCount.toFloat()
                                }

                                else -> null
                            },
                            hasError = uiState.syncProgress is SyncProgress.Failure &&
                                    uiState.syncProgress.restore,
                            isEnabled = uiState.isSignedIn,
                            onClick = {
                                if (!uiState.isSyncInProgress) {
                                    onEvent(BackupScreenEvent.OnButtonRestoreClick)
                                }
                            },
                        )
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            if (uiState.syncProgress is SyncProgress.Failure) {
                SyncError(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                )
            } else {
                BackupDate(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    date = uiState.lastSyncDate,
                )
            }
            if (uiState.questions.isNotEmpty()) {
                HorizontalDivider(
                    modifier = Modifier.padding(start = 24.dp, top = 40.dp, bottom = 32.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.tertiary,
                )
                QuestionsList(
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 16.dp),
                    questions = uiState.questions,
                    isSignedIn = uiState.isSignedIn,
                    onQuestionClick = { onEvent(BackupScreenEvent.OnQuestionClick(it)) },
                )
            }
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
