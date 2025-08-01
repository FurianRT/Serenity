package com.furianrt.search.internal.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.furianrt.core.buildImmutableList
import com.furianrt.notesearch.R
import com.furianrt.search.internal.ui.entities.SelectedFilter
import com.furianrt.uikit.components.ButtonBack
import com.furianrt.uikit.components.OneTimeEffect
import com.furianrt.uikit.components.TagItem
import com.furianrt.uikit.constants.ToolbarConstants
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.extensions.clickableWithScaleAnim
import com.furianrt.uikit.extensions.toDateString
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.time.LocalDate
import com.furianrt.uikit.R as uiR

private const val DATE_PATTERN = "dd.MM.yy"
private const val BACK_BUTTON_ANIM_DURATION = 200

@Composable
internal fun Toolbar(
    selectedFilters: ImmutableList<SelectedFilter>,
    queryState: TextFieldState,
    notesCount: Int,
    selectedNotesCount: Int,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onCalendarClick: () -> Unit = {},
    onDateFilterClick: (date: SelectedFilter.DateRange) -> Unit = {},
    onUnselectedTagClick: (tag: SelectedFilter.Tag) -> Unit = {},
    onRemoveFilterClick: (filter: SelectedFilter) -> Unit = {},
    onClearQueryClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onCloseSelectionClick: () -> Unit = {},
) {
    val focusRequester = remember { FocusRequester() }
    val isInspectionMode = LocalInspectionMode.current
    var showBackButton by rememberSaveable { mutableStateOf(isInspectionMode) }
    OneTimeEffect {
        showBackButton = true
        focusRequester.requestFocus()
    }
    val test = if (selectedNotesCount > 0) persistentListOf() else selectedFilters
    Crossfade(
        modifier = modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .animateContentSize()
            .systemGestureExclusion(),
        targetState = selectedNotesCount > 0,
    ) { targetState ->
        if (targetState) {
            SelectedContent(
                notesCount = notesCount,
                selectedNotesCount = selectedNotesCount.coerceAtLeast(1),
                onDeleteClick = onDeleteClick,
                onCloseSelectionClick = onCloseSelectionClick,
            )
        } else {
            UnselectedContent(
                selectedFilters = test,
                queryState = queryState,
                showBackButton = showBackButton,
                focusRequester = focusRequester,
                onBackClick = {
                    showBackButton = false
                    onBackClick()
                },
                onCalendarClick = onCalendarClick,
                onDateFilterClick = onDateFilterClick,
                onUnselectedTagClick = onUnselectedTagClick,
                onRemoveFilterClick = onRemoveFilterClick,
                onClearQueryClick = onClearQueryClick,
            )
        }
    }
}

@Composable
private fun SelectedContent(
    notesCount: Int,
    selectedNotesCount: Int,
    onDeleteClick: () -> Unit,
    onCloseSelectionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(ToolbarConstants.bigToolbarHeight)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = modifier
                .minimumInteractiveComponentSize()
                .clickableWithScaleAnim(
                    maxScale = 1.1f,
                    indication = ripple(bounded = false, radius = 20.dp),
                    onClick = onCloseSelectionClick,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(uiR.drawable.ic_exit),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
        Text(
            modifier = Modifier.weight(1f),
            text = "$selectedNotesCount/$notesCount",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
        Box(
            modifier = modifier
                .minimumInteractiveComponentSize()
                .clickableWithScaleAnim(
                    maxScale = 1.1f,
                    indication = ripple(bounded = false, radius = 20.dp),
                    onClick = onDeleteClick,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(uiR.drawable.ic_delete),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun UnselectedContent(
    selectedFilters: ImmutableList<SelectedFilter>,
    queryState: TextFieldState,
    showBackButton: Boolean,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onCalendarClick: () -> Unit,
    onDateFilterClick: (date: SelectedFilter.DateRange) -> Unit,
    onUnselectedTagClick: (tag: SelectedFilter.Tag) -> Unit,
    onRemoveFilterClick: (filter: SelectedFilter) -> Unit,
    onClearQueryClick: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .height(ToolbarConstants.bigToolbarHeight)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Box(modifier = Modifier.widthIn(min = 8.dp)) {
                this@Row.AnimatedVisibility(
                    enter = expandHorizontally(tween(BACK_BUTTON_ANIM_DURATION)),
                    exit = shrinkHorizontally(tween(BACK_BUTTON_ANIM_DURATION)),
                    visible = showBackButton,
                ) {
                    ButtonBack(
                        onClick = {
                            focusManager.clearFocus()
                            onBackClick()
                        },
                    )
                }
            }
            SearchBar(
                modifier = Modifier.weight(1f),
                state = queryState,
                focusRequester = focusRequester,
                onClearClick = onClearQueryClick,
            )
            ButtonCalendar(
                modifier = Modifier.padding(horizontal = 4.dp),
                onClick = onCalendarClick
            )
        }
        if (selectedFilters.isNotEmpty()) {
            SelectedFiltersList(
                filters = selectedFilters,
                onDateFilterClick = onDateFilterClick,
                onUnselectedTagClick = onUnselectedTagClick,
                onRemoveFilterClick = onRemoveFilterClick,
            )
        }
    }
}

@Composable
private fun ButtonCalendar(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.clickableWithScaleAnim(
            maxScale = 1.1f,
            indication = ripple(bounded = false, radius = 20.dp),
            onClick = onClick,
        ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            modifier = Modifier.padding(8.dp),
            painter = painterResource(R.drawable.ic_calendar),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun SearchBar(
    state: TextFieldState,
    focusRequester: FocusRequester,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val showCloseButton by remember { derivedStateOf { state.text.isNotEmpty() } }
    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(16.dp))
            .padding(start = 16.dp, end = 10.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicTextField(
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester),
            state = state,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
            ),
            lineLimits = TextFieldLineLimits.SingleLine,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.surfaceContainer),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done,
                showKeyboardOnFocus = true,
            ),
            decorator = { innerTextField ->
                if (state.text.isEmpty()) {
                    Text(
                        modifier = Modifier.alpha(0.5f),
                        text = stringResource(R.string.notes_search_bar_title),
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
                innerTextField()
            },
        )
        if (showCloseButton) {
            Icon(
                modifier = Modifier
                    .size(20.dp)
                    .alpha(0.5f)
                    .clickableNoRipple(onClick = onClearClick),
                painter = painterResource(uiR.drawable.ic_exit),
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun SelectedFiltersList(
    filters: ImmutableList<SelectedFilter>,
    onDateFilterClick: (date: SelectedFilter.DateRange) -> Unit,
    onUnselectedTagClick: (tag: SelectedFilter.Tag) -> Unit,
    onRemoveFilterClick: (filter: SelectedFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(
            count = filters.count(),
            key = { filters[it].id + filters[it].isSelected.toString() },
            contentType = { filters[it]::class.simpleName }
        ) { index ->
            when (val filter = filters[index]) {
                is SelectedFilter.Tag -> if (filter.isSelected) {
                    TagItem(
                        modifier = Modifier.animateItem(),
                        title = filter.title,
                        isRemovable = true,
                        onRemoveClick = { onRemoveFilterClick(filter) },
                    )
                } else {
                    TagItem(
                        modifier = Modifier
                            .alpha(0.5f)
                            .animateItem(),
                        title = filter.title,
                        isRemovable = false,
                        onRemoveClick = { onRemoveFilterClick(filter) },
                        onClick = { onUnselectedTagClick(filter) },
                    )
                }

                is SelectedFilter.DateRange -> TagItem(
                    modifier = Modifier.animateItem(),
                    title = if (filter.end != null) {
                        filter.start.toDateString(DATE_PATTERN) +
                                " - " + filter.end.toDateString(DATE_PATTERN)
                    } else {
                        filter.start.toDateString()
                    },
                    isRemovable = true,
                    onClick = { onDateFilterClick(filter) },
                    onRemoveClick = { onRemoveFilterClick(filter) },
                )
            }
        }
    }
}

@Composable
@PreviewWithBackground
private fun PreviewWithTags() {
    SerenityTheme {
        Toolbar(
            notesCount = 1,
            selectedNotesCount = 0,
            queryState = TextFieldState(),
            selectedFilters = buildImmutableList {
                add(SelectedFilter.DateRange(start = LocalDate.now(), end = LocalDate.now()))
                repeat(5) { index ->
                    add(SelectedFilter.Tag(title = "Title$index", isSelected = index == 0))
                }
            },
        )
    }
}

@Composable
@PreviewWithBackground
private fun PreviewWithoutTags() {
    SerenityTheme {
        Toolbar(
            notesCount = 1,
            selectedNotesCount = 0,
            queryState = TextFieldState("test query"),
            selectedFilters = persistentListOf(),
        )
    }
}

@Composable
@PreviewWithBackground
private fun PreviewSelected() {
    SerenityTheme {
        Toolbar(
            notesCount = 10,
            selectedNotesCount = 2,
            queryState = TextFieldState("test query"),
            selectedFilters = persistentListOf(),
        )
    }
}
