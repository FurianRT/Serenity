package com.furianrt.reminders.internal.ui.list

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.furianrt.permissions.extensions.openAlarmsSettingsScreen
import com.furianrt.permissions.extensions.openNotificationsSettingsScreen
import com.furianrt.permissions.ui.NotificationsPermissionDialog
import com.furianrt.permissions.utils.PermissionsUtils
import com.furianrt.reminders.R
import com.furianrt.reminders.internal.ui.entities.DayItem
import com.furianrt.reminders.internal.ui.list.composables.ReminderListItem
import com.furianrt.reminders.internal.ui.list.composables.TroubleshootingBlock
import com.furianrt.uikit.R as uiR
import com.furianrt.reminders.internal.ui.list.entities.ReminderItem
import com.furianrt.uikit.components.AppBackground
import com.furianrt.uikit.components.DefaultToolbar
import com.furianrt.uikit.components.MovableToolbarScaffold
import com.furianrt.uikit.components.RegularButton
import com.furianrt.uikit.entities.UiThemeColor
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.kizitonwose.calendar.core.daysOfWeek
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.collectLatest

private const val TROUBLESHOOTING_KEY = "troubleshooting"
private const val ADD_REMINDER_BUTTON__KEY = "add_reminder_button"

@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun RemindersListScreen(
    openReminderDetailsScreen: (reminderId: String?) -> Unit,
    openTroubleShootingScreen: () -> Unit,
    onCloseRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: RemindersListViewModel = hiltViewModel()
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val context = LocalContext.current

    val onCloseRequestState by rememberUpdatedState(onCloseRequest)
    val openReminderDetailsScreenState by rememberUpdatedState(openReminderDetailsScreen)
    val openTroubleShootingScreenState by rememberUpdatedState(openTroubleShootingScreen)

    val notificationsPermissionState = rememberPermissionState(
        permission = PermissionsUtils.getNotificationsPermission(),
        onPermissionResult = {
            viewModel.onEvent(RemindersListEvent.OnNotificationsPermissionSelected)
        },
    )

    var showNotificationsPermissionDialog by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val hazeState = rememberHazeState()

    LaunchedEffect(Unit) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->
                when (effect) {
                    is RemindersListEffect.CloseScreen -> onCloseRequestState()
                    is RemindersListEffect.OpenReminderDetailsScreen -> {
                        openReminderDetailsScreenState(effect.reminderId)
                    }

                    is RemindersListEffect.OpenTroubleShootingScreen -> {
                        openTroubleShootingScreenState()
                    }

                    is RemindersListEffect.RequestNotificationsPermission -> {
                        notificationsPermissionState.launchPermissionRequest()
                    }

                    is RemindersListEffect.ShowNotificationsPermissionsDeniedDialog -> {
                        showNotificationsPermissionDialog = true
                    }

                    is RemindersListEffect.OpenAlarmsSettingsScreen -> {
                        context.openAlarmsSettingsScreen()
                    }
                }
            }
    }
    Content(
        modifier = modifier.hazeSource(hazeState),
        uiState = uiState,
        listState = listState,
        onEvent = viewModel::onEvent,
    )

    if (showNotificationsPermissionDialog) {
        NotificationsPermissionDialog(
            hazeState = hazeState,
            onSettingsClick = context::openNotificationsSettingsScreen,
            onDismissRequest = { showNotificationsPermissionDialog = false },
        )
    }
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    uiState: RemindersListUiState,
    listState: LazyListState,
    onEvent: (event: RemindersListEvent) -> Unit,
) {
    val backgroundHazeState = rememberHazeState()
    MovableToolbarScaffold(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        listState = listState,
        enabled = false,
        toolbar = {
            DefaultToolbar(
                modifier = Modifier.statusBarsPadding(),
                title = stringResource(R.string.reminders_screen_title),
                onBackClick = { onEvent(RemindersListEvent.OnCloseScreenClick) },
            )
        },
    ) { topPadding ->
        AppBackground(
            modifier = Modifier.hazeSource(backgroundHazeState),
            theme = uiState.theme,
        )
        AnimatedContent(
            targetState = uiState.content,
            contentKey = { it::class.java },
            transitionSpec = {
                if (initialState is RemindersListUiState.Content.Loading) {
                    EnterTransition.None.togetherWith(ExitTransition.None)
                } else {
                    fadeIn(
                        animationSpec = spring(stiffness = Spring.StiffnessLow),
                    ).togetherWith(
                        fadeOut(
                            animationSpec = spring(stiffness = Spring.StiffnessMedium),
                        ),
                    )
                }
            },
        ) { targetState ->
            when (targetState) {
                is RemindersListUiState.Content.Loading -> LoadingContent(
                    modifier = Modifier.padding(top = topPadding),
                )

                is RemindersListUiState.Content.Success -> SuccessContent(
                    uiState = targetState,
                    listState = listState,
                    hazeState = backgroundHazeState,
                    topPadding = topPadding,
                    onEvent = onEvent,
                )

                is RemindersListUiState.Content.Empty -> EmptyContent(
                    modifier = Modifier.padding(top = topPadding),
                    onEvent = onEvent,
                )
            }
        }
    }
}

@Composable
private fun SuccessContent(
    modifier: Modifier = Modifier,
    uiState: RemindersListUiState.Content.Success,
    listState: LazyListState,
    hazeState: HazeState,
    topPadding: Dp,
    onEvent: (event: RemindersListEvent) -> Unit,
) {
    val haptic = LocalHapticFeedback.current
    LazyColumn(
        modifier = modifier,
        state = listState,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 16.dp + topPadding,
            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 32.dp,
        ),
    ) {
        items(
            items = uiState.reminders,
            key = ReminderItem::id,
        ) { item ->
            ReminderListItem(
                modifier = Modifier.animateItem(),
                item = item,
                hazeState = hazeState,
                onClick = { onEvent(RemindersListEvent.OnReminderClick(it)) },
                onDeleteClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                    onEvent(RemindersListEvent.OnDeleteReminderClick(it))
                },
            )
        }
        item(
            key = ADD_REMINDER_BUTTON__KEY,
        ) {
            RegularButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 20.dp)
                    .animateItem(),
                contentPadding = PaddingValues(vertical = 6.dp),
                text = stringResource(R.string.reminders_add_reminder_button_title),
                icon = painterResource(uiR.drawable.ic_add),
                onClick = { onEvent(RemindersListEvent.OnAddReminderClick) },
            )
        }
        item(
            key = TROUBLESHOOTING_KEY,
        ) {
            TroubleshootingBlock(
                modifier = Modifier.animateItem(),
                hazeState = hazeState,
                onClick = { onEvent(RemindersListEvent.OnTroubleShootingClick) },
            )
        }
    }
}

@Composable
private fun EmptyContent(
    modifier: Modifier = Modifier,
    onEvent: (event: RemindersListEvent) -> Unit,
) {
    Column(
        modifier = modifier
            .padding(start = 24.dp, end = 24.dp, top = 40.dp, bottom = 24.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            modifier = Modifier.size(80.dp),
            painter = painterResource(R.drawable.reminer_screen_logo),
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = null,
        )
        Spacer(Modifier.height(32.dp))
        Text(
            text = stringResource(R.string.reminders_screen_description),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(40.dp))
        RegularButton(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 6.dp),
            text = stringResource(R.string.reminders_add_reminder_button_title),
            icon = painterResource(uiR.drawable.ic_add),
            onClick = { onEvent(RemindersListEvent.OnAddReminderClick) }
        )
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
private fun EmptyPreview() {
    SerenityTheme {
        Content(
            uiState = RemindersListUiState(
                theme = UiThemeColor.STORM_IN_THE_NIGHT_BLUE_LIGHT,
                content = RemindersListUiState.Content.Empty,
            ),
            listState = rememberLazyListState(),
            onEvent = {},
        )
    }
}

@PreviewWithBackground
@Composable
private fun SuccessPreview() {
    SerenityTheme {
        Content(
            uiState = RemindersListUiState(
                theme = UiThemeColor.STORM_IN_THE_NIGHT_BLUE_LIGHT,
                content = RemindersListUiState.Content.Success(
                    reminders = listOf(
                        ReminderItem(
                            id = "1",
                            title = "How was your day",
                            time = "6:00 AM",
                            daysOfWeek = daysOfWeek().mapIndexed { index, day ->
                                DayItem(
                                    day = day,
                                    isSelected = index % 2 == 0,
                                )
                            },
                        ),
                        ReminderItem(
                            id = "2",
                            title = null,
                            time = "6:00 AM",
                            daysOfWeek = daysOfWeek().mapIndexed { index, day ->
                                DayItem(
                                    day = day,
                                    isSelected = index % 2 == 0,
                                )
                            },
                        ),
                        ReminderItem(
                            id = "3",
                            title = "How was your day",
                            time = "6:00 AM",
                            daysOfWeek = emptyList(),
                        ),
                        ReminderItem(
                            id = "4",
                            title = null,
                            time = "6:00 AM",
                            daysOfWeek = emptyList(),
                        ),
                    ),
                ),
            ),
            listState = rememberLazyListState(),
            onEvent = {},
        )
    }
}
