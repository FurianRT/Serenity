package com.furianrt.search.internal.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.furianrt.notesearch.R
import com.furianrt.uikit.components.ButtonBack
import com.furianrt.uikit.constants.ToolbarConstants
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground

@Composable
internal fun Toolbar(
    queryState: TextFieldState,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onCalendarClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .height(ToolbarConstants.toolbarHeight)
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ButtonBack(
            onClick = onBackClick,
        )
        SearchBar(
            modifier = Modifier.weight(1f),
            state = queryState,
        )
        ButtonCalendar(
            onClick = onCalendarClick,
        )
    }
}

@Composable
fun ButtonCalendar(
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
internal fun SearchBar(
    state: TextFieldState,
    modifier: Modifier = Modifier,
) {
    BasicTextField(
        modifier = modifier
            .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        state = state,
        textStyle = MaterialTheme.typography.bodyMedium,
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
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        Toolbar(
            queryState = TextFieldState(),
        )
    }
}
