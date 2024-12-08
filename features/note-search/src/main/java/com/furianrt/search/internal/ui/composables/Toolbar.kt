package com.furianrt.search.internal.ui.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.furianrt.core.buildImmutableList
import com.furianrt.notesearch.R
import com.furianrt.uikit.R as uiR
import com.furianrt.search.internal.ui.entities.SelectedFilter
import com.furianrt.uikit.components.ButtonBack
import com.furianrt.uikit.components.TagItem
import com.furianrt.uikit.constants.ToolbarConstants
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.extensions.toDateString
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.time.LocalDate

private const val DATE_PATTERN = "dd.MM.yy"

@Composable
internal fun Toolbar(
    selectedFilters: ImmutableList<SelectedFilter>,
    queryState: TextFieldState,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onCalendarClick: () -> Unit = {},
    onUnselectedTagClick: (tag: SelectedFilter.Tag) -> Unit = {},
    onRemoveFilterClick: (filter: SelectedFilter) -> Unit = {},
    onClearQueryClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .animateContentSize(),
    ) {
        Row(
            modifier = Modifier
                .height(ToolbarConstants.bigToolbarHeight)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            ButtonBack(onClick = onBackClick)
            SearchBar(
                modifier = Modifier.weight(1f),
                state = queryState,
                onClearClick = onClearQueryClick,
            )
            ButtonCalendar(onClick = onCalendarClick)
        }
        if (selectedFilters.isNotEmpty()) {
            SelectedFiltersList(
                filters = selectedFilters,
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
    IconButton(
        modifier = modifier,
        onClick = onClick,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_calendar),
            contentDescription = null,
            tint = Color.Unspecified,
        )
    }
}

@Composable
private fun SearchBar(
    state: TextFieldState,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val showCloseButton by remember { derivedStateOf { state.text.isNotEmpty() } }
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(16.dp))
            .padding(start = 16.dp, end = 10.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicTextField(
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester),
            state = state,
            textStyle = MaterialTheme.typography.bodyMedium,
            lineLimits = TextFieldLineLimits.SingleLine,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.secondary),
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
                tint = Color.Unspecified,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun SelectedFiltersList(
    filters: ImmutableList<SelectedFilter>,
    onUnselectedTagClick: (tag: SelectedFilter.Tag) -> Unit,
    onRemoveFilterClick: (filter: SelectedFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .systemGestureExclusion(),
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
            queryState = TextFieldState(),
            selectedFilters = buildImmutableList {
                add(SelectedFilter.DateRange(start = LocalDate.now(), end = LocalDate.now()))
                repeat(5) { index ->
                    add(SelectedFilter.Tag(title = "Title$index", isSelected = index == 0))
                }
            }
        )
    }
}

@Composable
@PreviewWithBackground
private fun PreviewWithoutTags() {
    SerenityTheme {
        Toolbar(
            queryState = TextFieldState("test query"),
            selectedFilters = persistentListOf(),
        )
    }
}
