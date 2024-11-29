package com.furianrt.uikit.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import com.furianrt.core.buildImmutableList
import com.furianrt.uikit.R
import com.furianrt.uikit.extensions.applyIf
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.extensions.firstMostVisibleItemInfo
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.LocalAuth
import com.furianrt.uikit.utils.PreviewWithBackground
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.yearMonth
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeChild
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.absoluteValue
import kotlin.math.max

private const val MIN_YEAR = 1970
private const val MAX_YEAR = 2050
private const val SELECTION_DELAY = 100L
private const val MODE_CHANGE_ANIM_DURATION = 250

private enum class Mode {
    DAY_PICKER, YEAR_MONTH_PICKER
}

@Immutable
private data class SelectedYearMonth(val yearMonth: YearMonth)

@Immutable
data class SelectedDate(val date: LocalDate)

@Composable
fun SingleChoiceCalendar(
    selectedDate: SelectedDate,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
    onDateSelected: (date: SelectedDate) -> Unit = {},
    onDismissRequest: () -> Unit = {},
) {
    var selected by remember(selectedDate) { mutableStateOf(selectedDate) }
    val scope = rememberCoroutineScope()
    val calendarState = rememberCalendarState(
        startMonth = YearMonth.of(MIN_YEAR, Month.JANUARY),
        endMonth = YearMonth.of(MAX_YEAR, Month.DECEMBER),
        firstVisibleMonth = selectedDate.date.yearMonth,
        outDateStyle = OutDateStyle.EndOfGrid,
    )
    CalendarDialog(
        modifier = modifier,
        state = calendarState,
        selectedDate = selected,
        hazeState = hazeState,
        onDateSelected = { date ->
            scope.launch {
                selected = date
                delay(SELECTION_DELAY)
                onDateSelected(date)
                onDismissRequest()
            }
        },
        onDismissRequest = onDismissRequest,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarDialog(
    state: CalendarState,
    selectedDate: SelectedDate,
    hazeState: HazeState,
    onDateSelected: (date: SelectedDate) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val auth = LocalAuth.current

    var mode by remember { mutableStateOf(Mode.DAY_PICKER) }

    var selectedYearMonth by remember(selectedDate) {
        mutableStateOf(SelectedYearMonth(selectedDate.date.yearMonth))
    }

    var dialogSize by remember { mutableStateOf(IntSize.Zero) }

    LaunchedEffect(Unit) {
        snapshotFlow { state.layoutInfo.firstMostVisibleItemInfo() }
            .collect { info ->
                state.layoutInfo.visibleMonthsInfo.find { it.index == info.index }?.let {
                    selectedYearMonth = SelectedYearMonth(it.month.yearMonth)
                }
            }
    }

    LifecycleStartEffect(Unit) {
        scope.launch {
            if (!auth.isAuthorized()) {
                onDismissRequest()
            }
        }
        onStopOrDispose {}
    }

    BasicAlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickableNoRipple(onClick = onDismissRequest),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(32.dp))
                    .hazeChild(
                        state = hazeState,
                        style = HazeDefaults.style(
                            backgroundColor = MaterialTheme.colorScheme.surface,
                            blurRadius = 20.dp,
                        ),
                    )
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.3f))
                    .wrapContentSize()
                    .animateContentSize(
                        animationSpec = spring(
                            stiffness = Spring.StiffnessHigh,
                            visibilityThreshold = IntSize.VisibilityThreshold,
                        ),
                        alignment = Alignment.Center,
                    )
                    .clickableNoRipple {},
                contentAlignment = Alignment.Center,
            ) {
                AnimatedContent(
                    targetState = mode,
                    transitionSpec = {
                        fadeIn(tween(MODE_CHANGE_ANIM_DURATION))
                            .togetherWith(fadeOut(tween(MODE_CHANGE_ANIM_DURATION)))
                    },
                    label = "ModeAnim"
                ) { targetState ->
                    when (targetState) {
                        Mode.DAY_PICKER -> DayPickerContent(
                            modifier = Modifier.onSizeChanged { dialogSize = it },
                            state = state,
                            selectedDate = selectedDate,
                            selectedYearMonth = selectedYearMonth,
                            onDateSelected = onDateSelected,
                            onMonthClick = { mode = Mode.YEAR_MONTH_PICKER },
                        )

                        Mode.YEAR_MONTH_PICKER -> YearMonthPickerContent(
                            modifier = Modifier.width(density.run { dialogSize.width.toDp() }),
                            initialYearMonth = selectedYearMonth,
                            onYearMonthSelected = {
                                scope.launch { state.scrollToMonth(it.yearMonth) }
                                mode = Mode.DAY_PICKER
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DayPickerContent(
    state: CalendarState,
    selectedDate: SelectedDate,
    selectedYearMonth: SelectedYearMonth,
    onDateSelected: (date: SelectedDate) -> Unit,
    onMonthClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        MonthHeader(
            selected = selectedYearMonth,
            onClick = onMonthClick,
        )
        HorizontalCalendar(
            state = state,
            dayContent = { day ->
                DayCell(
                    day = day,
                    isSelected = day.date == selectedDate.date,
                    onClick = { onDateSelected(SelectedDate(it.date)) },
                )
            },
            monthBody = { _, container ->
                Box(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center,
                    content = { container() },
                )
            },
        )
    }
}

@Composable
private fun YearMonthPickerContent(
    initialYearMonth: SelectedYearMonth,
    onYearMonthSelected: (yearMonth: SelectedYearMonth) -> Unit,
    modifier: Modifier = Modifier,
) {
    val monthsArray = stringArrayResource(R.array.months_array)
    val months: ImmutableList<String> = remember { persistentListOf(*monthsArray) }
    val years = remember {
        buildImmutableList {
            for (year in MIN_YEAR..MAX_YEAR) {
                add(year.toString())
            }
        }
    }
    val yearIndex = remember(initialYearMonth) {
        years.indexOf(initialYearMonth.yearMonth.year.toString())
    }

    val itemHeight = 48.dp

    var selectedYearMonth by remember { mutableStateOf(initialYearMonth) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .height(itemHeight)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(32.dp)),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
            ) {
                MonthYearList(
                    modifier = Modifier.weight(1f),
                    items = months,
                    initialIndex = initialYearMonth.yearMonth.month.ordinal,
                    itemHeight = itemHeight,
                    onItemSelected = { index ->
                        selectedYearMonth = selectedYearMonth.copy(
                            yearMonth = selectedYearMonth.yearMonth.withMonth(index + 1),
                        )
                    },
                )
                MonthYearList(
                    modifier = Modifier.weight(1f),
                    items = years,
                    initialIndex = yearIndex,
                    itemHeight = itemHeight,
                    onItemSelected = { index ->
                        val year = years[index].toInt()
                        selectedYearMonth = selectedYearMonth.copy(
                            yearMonth = selectedYearMonth.yearMonth.withYear(year)
                        )
                    },
                )
            }
        }
        TextButton(
            modifier = Modifier.padding(end = 24.dp, top = 8.dp, bottom = 12.dp),
            onClick = { onYearMonthSelected(selectedYearMonth) },
        ) {
            Text(
                text = stringResource(R.string.action_done),
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Composable
private fun MonthYearList(
    items: ImmutableList<String>,
    initialIndex: Int,
    itemHeight: Dp,
    onItemSelected: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val listHeight = itemHeight * 5
    val listHeightPx = LocalDensity.current.run { listHeight.toPx() }
    val itemHeightPx = LocalDensity.current.run { itemHeight.toPx() }

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)

    var userInteracted by remember { mutableStateOf(false) }
    val isListDragging by listState.interactionSource.collectIsDraggedAsState()
    userInteracted = isListDragging || userInteracted

    LaunchedEffect(Unit) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { index ->
                if (userInteracted && listState.firstVisibleItemScrollOffset != 0) {
                    view.performHapticFeedback(HapticFeedbackConstants.TEXT_HANDLE_MOVE)
                }
                onItemSelected(index)
            }
    }

    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            if (userInteracted) {
                view.performHapticFeedback(HapticFeedbackConstants.TEXT_HANDLE_MOVE)
            }
        }
    }

    LazyColumn(
        modifier = modifier.height(listHeight),
        state = listState,
        flingBehavior = rememberSnapFlingBehavior(listState),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(count = 2) {
            Spacer(Modifier.height(itemHeight))
        }
        items(count = items.count(), key = { it }) { index ->
            val scale = remember { Animatable(1f) }
            LaunchedEffect(listState.isScrollInProgress) {
                val isCurrentItem = index == listState.firstVisibleItemIndex
                if (userInteracted && !listState.isScrollInProgress && isCurrentItem) {
                    scale.animateTo(
                        targetValue = 1.1f,
                        animationSpec = tween(durationMillis = 100),
                    )
                    scale.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 100),
                    )
                }
            }
            Box(
                modifier = Modifier
                    .height(itemHeight)
                    .clickableNoRipple {
                        userInteracted = true
                        scope.launch { listState.animateScrollToItem(index) }
                    }
                    .graphicsLayer {
                        val itemOffset = listState.layoutInfo
                            .visibleItemsInfo
                            .find { it.index == index + 2 }
                            ?.offset
                        if (itemOffset != null) {
                            val center = listHeightPx / 2
                            val resultOffset = itemOffset + itemHeightPx / 2f
                            val offsetPercent = (resultOffset - center).absoluteValue / center
                            alpha = max(0f, 1f - offsetPercent)
                            if (scale.value != 1f) {
                                scaleX = scale.value
                                scaleY = scale.value
                            } else {
                                scaleX = max(0.9f, 1f - offsetPercent)
                                scaleY = max(0.9f, 1f - offsetPercent)
                            }
                        }
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = items[index],
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
        items(count = 2) {
            Spacer(Modifier.height(itemHeight))
        }
    }
}

@Composable
private fun DayCell(
    day: CalendarDay,
    isSelected: Boolean,
    onClick: (day: CalendarDay) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .alpha(if (day.position == DayPosition.MonthDate) 1f else 0.5f)
            .padding(horizontal = 4.dp, vertical = 6.dp)
            .clip(CircleShape)
            .aspectRatio(ratio = 1f, matchHeightConstraintsFirst = false)
            .fillMaxSize()
            .applyIf(isSelected) { Modifier.background(MaterialTheme.colorScheme.primaryContainer) }
            .clickable { onClick(day) },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun MonthHeader(
    selected: SelectedYearMonth,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val title = remember(selected) {
        val month = selected.yearMonth.month.getDisplayName(
            TextStyle.FULL_STANDALONE,
            Locale.US,
        ).replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.US) else it.toString()
        }
        val year = selected.yearMonth.year.toString()
        "$month $year"
    }
    Row(
        modifier = modifier
            .padding(start = 20.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            text = title,
            style = MaterialTheme.typography.titleMedium,
        )
        Icon(
            modifier = Modifier
                .alpha(0.4f)
                .size(20.dp),
            painter = painterResource(R.drawable.ic_action_edit),
            tint = Color.Unspecified,
            contentDescription = null,
        )
    }
}

@Composable
@Preview
private fun SingleChoicePreview() {
    SerenityTheme {
        SingleChoiceCalendar(
            selectedDate = SelectedDate(LocalDate.now()),
            hazeState = HazeState(),
        )
    }
}

@Composable
@PreviewWithBackground
private fun YearMonthPickerPreview() {
    SerenityTheme {
        YearMonthPickerContent(
            initialYearMonth = SelectedYearMonth(
                yearMonth = YearMonth.now(),
            ),
            onYearMonthSelected = {},
        )
    }
}