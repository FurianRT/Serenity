package com.furianrt.backup.internal.ui

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.furianrt.backup.R
import com.furianrt.backup.internal.ui.entities.Question
import com.furianrt.uikit.components.DefaultToolbar
import com.furianrt.uikit.components.SkipFirstEffect
import com.furianrt.uikit.components.SwitchWithLabel
import com.furianrt.uikit.extensions.applyIf
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.extensions.clickableWithScaleAnim
import com.furianrt.uikit.extensions.drawBottomShadow
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun BackupScreen(
    onCloseRequest: () -> Unit,
) {
    val viewModel: BackupViewModel = hiltViewModel()
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is BackupEffect.CloseScreen -> onCloseRequest()
            }
        }
    }

    ScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
    )
}

@Composable
private fun ScreenContent(
    uiState: BackupUiState,
    onEvent: (event: BackupScreenEvent) -> Unit = {},
) {
    val scrollState = rememberScrollState()
    Scaffold(
        modifier = Modifier,
        topBar = {
            DefaultToolbar(
                modifier = Modifier
                    .drawBehind {
                        if (scrollState.canScrollBackward) {
                            drawBottomShadow(elevation = 8.dp)
                        }
                    }
                    .statusBarsPadding(),
                title = stringResource(R.string.backup_google_drive_title),
                onBackClick = { onEvent(BackupScreenEvent.OnButtonBackClick) },
            )
        },
        contentWindowInsets = WindowInsets.statusBars,
    ) { paddingValues ->
        when (uiState) {
            is BackupUiState.Success -> SuccessContent(
                modifier = Modifier.padding(paddingValues),
                uiState = uiState,
                onEvent = onEvent,
                scrollState = scrollState,
            )

            is BackupUiState.Loading -> LoadingContent(
                modifier = Modifier.padding(paddingValues),
            )
        }
    }
}

@Composable
private fun SuccessContent(
    uiState: BackupUiState.Success,
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
    onEvent: (event: BackupScreenEvent) -> Unit = {},
) {
    val view = LocalView.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Header(
            authState = uiState.authState,
            onSingInClick = { onEvent(BackupScreenEvent.OnSignInClick) },
            onSingOutClick = { onEvent(BackupScreenEvent.OnSignOunClick) },
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                )
                .padding(vertical = 16.dp),
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
                isEnabled = uiState.isSignedIn,
                onClick = { onEvent(BackupScreenEvent.OnBackupPeriodClick) },
            )
            Spacer(Modifier.height(40.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                BackupButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.backup_backup_title),
                    enabled = uiState.isSignedIn,
                    onClick = {},
                )
                BackupButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.backup_restore_title),
                    enabled = uiState.isSignedIn,
                    onClick = {},
                )
            }
            Spacer(Modifier.height(24.dp))
            BackupDate(
                date = uiState.lastSyncDateTime,
            )
            if (uiState.questions.isNotEmpty()) {
                HorizontalDivider(
                    modifier = Modifier.padding(start = 24.dp, top = 40.dp, bottom = 32.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.tertiary,
                )
                QuestionsList(
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
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
private fun Header(
    authState: BackupUiState.Success.AuthState,
    onSingInClick: () -> Unit,
    onSingOutClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isPlaying by remember { mutableStateOf(true) }
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.anim_backup_profile),
    )
    val lottieState = animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isPlaying,
    )
    SkipFirstEffect(lottieState.isPlaying) {
        isPlaying = lottieState.isPlaying
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 8.dp, top = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        LottieAnimation(
            modifier = Modifier
                .size(64.dp)
                .clickableWithScaleAnim { isPlaying = true },
            composition = composition,
            progress = { lottieState.progress },
        )
        when (authState) {
            is BackupUiState.Success.AuthState.SignedOut -> SignedOutHeader(
                onSingInClick = onSingInClick,
            )

            is BackupUiState.Success.AuthState.SignedIn -> SignedInHeader(
                email = authState.email,
                onSingOutClick = onSingOutClick,
            )
        }
    }
}

@Composable
private fun BackupPeriod(
    period: String,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(enabled = isEnabled, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .applyIf(!isEnabled) { Modifier.alpha(0.5f) },
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(R.string.backup_auto_backup_period_title),
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            modifier = Modifier.alpha(0.5f),
            text = period,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
private fun BackupButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Button(
        modifier = modifier.applyIf(!enabled) { Modifier.alpha(0.5f) },
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Text(
            modifier = Modifier.padding(vertical = 4.dp),
            text = text,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun BackupDate(
    date: String?,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier.alpha(0.5f),
        text = stringResource(
            R.string.backup_last_sync_time_title,
            date ?: stringResource(R.string.backup_last_sync_time_none_title),
        ),
        style = MaterialTheme.typography.labelMedium,
    )
}

@Composable
private fun QuestionsList(
    questions: ImmutableList<Question>,
    isSignedIn: Boolean,
    onQuestionClick: (question: Question) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        questions.forEachIndexed { index, question ->
            QuestionItem(
                question = question,
                index = index,
                isSignedIn = isSignedIn,
                onClick = onQuestionClick,
            )
        }
    }
}

@Composable
private fun QuestionItem(
    question: Question,
    index: Int,
    isSignedIn: Boolean,
    onClick: (question: Question) -> Unit,
    modifier: Modifier = Modifier,
) {
    val alpha by animateFloatAsState(
        targetValue = if (question.isExpanded || !isSignedIn) 1f else 0.5f,
    )
    Column(
        modifier = modifier
            .animateContentSize()
            .clickableNoRipple { onClick(question) },
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            modifier = Modifier.alpha(alpha),
            text = "${index + 1}. ${question.title}",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.ExtraBold,
        )
        AnimatedVisibility(
            modifier = Modifier.alpha(alpha),
            visible = question.isExpanded,
            enter = fadeIn(),
            exit = ExitTransition.None,
        ) {
            Text(
                text = question.answer,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun SignedInHeader(
    email: String,
    onSingOutClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val wholeText = stringResource(R.string.backup_tap_to_sing_out_title)
    val underlinePart = stringResource(R.string.backup_tap_to_sing_out_underline_part)
    val underlinePartIndex = wholeText.indexOf(underlinePart)
    val title = remember {
        buildAnnotatedString {
            append(wholeText)
            addStyle(
                style = SpanStyle(
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.ExtraBold,
                ),
                start = underlinePartIndex,
                end = underlinePartIndex + underlinePart.length,
            )
        }
    }
    Column(
        modifier = modifier
            .alpha(0.5f)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onSingOutClick)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = email,
            style = MaterialTheme.typography.bodySmall,
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun SignedOutHeader(
    onSingInClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 250,
                delayMillis = 1000,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
    )
    val wholeText = stringResource(R.string.backup_tap_to_sing_in_title)
    val underlinePart = stringResource(R.string.backup_tap_to_sing_in_underline_part)
    val underlinePartIndex = wholeText.indexOf(underlinePart)
    val title = remember {
        buildAnnotatedString {
            append(wholeText)
            addStyle(
                style = SpanStyle(
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.ExtraBold,
                ),
                start = underlinePartIndex,
                end = underlinePartIndex + underlinePart.length,
            )
        }
    }
    Text(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onSingInClick)
            .padding(horizontal = 8.dp, vertical = 16.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        text = title,
        style = MaterialTheme.typography.bodyMedium,
    )
}

@Composable
@PreviewWithBackground
private fun PreviewSignedIn() {
    SerenityTheme {
        ScreenContent(
            uiState = BackupUiState.Success(
                isAutoBackupEnabled = true,
                backupPeriod = "1 day",
                lastSyncDateTime = null,
                questions = persistentListOf(
                    Question(
                        id = "",
                        title = stringResource(R.string.backup_popular_question_1_title),
                        answer = stringResource(R.string.backup_popular_question_1_answer),
                        isExpanded = false,
                    ),
                    Question(
                        id = "",
                        title = stringResource(R.string.backup_popular_question_2_title),
                        answer = stringResource(R.string.backup_popular_question_2_answer),
                        isExpanded = true,
                    ),
                    Question(
                        id = "",
                        title = stringResource(R.string.backup_popular_question_3_title),
                        answer = stringResource(R.string.backup_popular_question_3_answer),
                        isExpanded = false,
                    ),
                ),
                authState = BackupUiState.Success.AuthState.SignedIn(
                    email = "felmemfmelflmfe",
                ),
            ),
        )
    }
}

@Composable
@PreviewWithBackground
private fun PreviewSignedOut() {
    SerenityTheme {
        ScreenContent(
            uiState = BackupUiState.Success(
                isAutoBackupEnabled = true,
                backupPeriod = "1 day",
                lastSyncDateTime = null,
                questions = persistentListOf(
                    Question(
                        id = "",
                        title = stringResource(R.string.backup_popular_question_1_title),
                        answer = stringResource(R.string.backup_popular_question_1_answer),
                        isExpanded = true,
                    ),
                    Question(
                        id = "",
                        title = stringResource(R.string.backup_popular_question_2_title),
                        answer = stringResource(R.string.backup_popular_question_2_answer),
                        isExpanded = false,
                    ),
                    Question(
                        id = "",
                        title = stringResource(R.string.backup_popular_question_3_title),
                        answer = stringResource(R.string.backup_popular_question_3_answer),
                        isExpanded = false,
                    ),
                ),
                authState = BackupUiState.Success.AuthState.SignedOut,
            ),
        )
    }
}
