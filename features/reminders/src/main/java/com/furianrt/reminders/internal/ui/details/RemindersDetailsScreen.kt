package com.furianrt.reminders.internal.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.furianrt.reminders.R
import com.furianrt.reminders.internal.ui.details.composables.DaysOfWeekPanel
import com.furianrt.reminders.internal.ui.details.composables.NotificationTextBlock
import com.furianrt.reminders.internal.ui.details.composables.NotificationTextDialog
import com.furianrt.reminders.internal.ui.details.composables.TimePicker
import com.furianrt.reminders.internal.ui.entities.DayItem
import com.furianrt.uikit.components.AppBackground
import com.furianrt.uikit.components.DefaultToolbar
import com.furianrt.uikit.components.MovableToolbarScaffold
import com.furianrt.uikit.components.RegularButton
import com.furianrt.uikit.entities.UiThemeColor
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import com.kizitonwose.calendar.core.daysOfWeek
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalTime
import com.furianrt.uikit.R as uiR

@Composable
internal fun RemindersDetailsScreen(
    onCloseRequest: () -> Unit,
) {
    val viewModel: RemindersDetailsViewModel = hiltViewModel()
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val onCloseRequestState by rememberUpdatedState(onCloseRequest)

    val hazeState = rememberHazeState()
    val haptic = LocalHapticFeedback.current

    var notificationTextDialogState by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->
                when (effect) {
                    is RemindersDetailsEffect.CloseScreen -> onCloseRequestState()
                    is RemindersDetailsEffect.ShowNotificationTextDialog -> {
                        notificationTextDialogState = effect.text
                    }

                    is RemindersDetailsEffect.PerformTimeHaptic -> {
                        haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                    }
                }
            }
    }
    Content(
        modifier = Modifier.hazeSource(hazeState),
        uiState = uiState,
        onEvent = viewModel::onEvent,
    )

    notificationTextDialogState?.let { dialogState ->
        NotificationTextDialog(
            initialText = dialogState,
            hazeState = hazeState,
            onTextEntered = { text ->
                viewModel.onEvent(RemindersDetailsEvent.OnNotificationTextEntered(text))
            },
            onDismissRequest = { notificationTextDialogState = null },
        )
    }
}

@Composable
private fun Content(
    uiState: RemindersDetailsUiState,
    onEvent: (event: RemindersDetailsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    MovableToolbarScaffold(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        listState = rememberScrollState(),
        enabled = false,
        toolbar = {
            DefaultToolbar(
                modifier = Modifier.statusBarsPadding(),
                title = stringResource(R.string.reminders_details_screen_title),
                onBackClick = { onEvent(RemindersDetailsEvent.OnCloseScreenClick) },
            )
        },
    ) { topPadding ->
        AppBackground(
            theme = uiState.theme,
        )
        when (uiState.content) {
            is RemindersDetailsUiState.Content.Loading -> LoadingContent(
                modifier = Modifier.padding(top = topPadding),
            )

            is RemindersDetailsUiState.Content.Success -> SuccessContent(
                uiState = uiState.content,
                topPadding = topPadding,
                onEvent = onEvent,
            )
        }
    }
}

@Composable
private fun SuccessContent(
    uiState: RemindersDetailsUiState.Content.Success,
    onEvent: (event: RemindersDetailsEvent) -> Unit,
    topPadding: Dp,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = topPadding),
        verticalArrangement = Arrangement.spacedBy(32.dp),
    ) {
        TimePicker(
            startTime = uiState.initialTime,
            onSnappedTimeChanged = { onEvent(RemindersDetailsEvent.OnTimeSelected(it)) },
            onSnappedTime = {},
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .background(
                    color = MaterialTheme.colorScheme.inverseSurface,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                )
                .padding(vertical = 20.dp, horizontal = 16.dp),
        ) {
            DaysOfWeekPanel(
                daysOfWeek = uiState.daysOfWeek,
                onDayClick = { onEvent(RemindersDetailsEvent.OnDayClick(it)) },
            )
            Spacer(Modifier.height(32.dp))
            NotificationTextBlock(
                text = uiState.notificationText,
                onClick = { onEvent(RemindersDetailsEvent.OnEnterNotificationTextClick) },
            )
            Spacer(Modifier.weight(1f))
            RegularButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp, bottom = 24.dp),
                text = stringResource(uiR.string.action_save),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                textStyle = MaterialTheme.typography.titleLarge,
                onClick = { onEvent(RemindersDetailsEvent.OnSaveClick) },
            )
        }
    }
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize(),
    )
}

@PreviewWithBackground
@Composable
private fun Preview() {
    SerenityTheme {
        Content(
            uiState = RemindersDetailsUiState(
                theme = UiThemeColor.STORM_IN_THE_NIGHT_BLUE_LIGHT,
                content = RemindersDetailsUiState.Content.Success(
                    initialTime = LocalTime.now(),
                    notificationText = "How was your day?",
                    daysOfWeek = daysOfWeek().mapIndexed { index, day ->
                        DayItem(
                            day = day,
                            isSelected = index % 2 == 0,
                        )
                    },
                ),
            ),
            onEvent = {},
        )
    }
}
