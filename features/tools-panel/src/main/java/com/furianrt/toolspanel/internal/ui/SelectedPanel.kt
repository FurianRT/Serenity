package com.furianrt.toolspanel.internal.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import com.furianrt.notelistui.composables.NoteTitleState
import com.furianrt.toolspanel.R
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground

@Composable
internal fun SelectedPanel(
    noteTitleState: NoteTitleState = NoteTitleState(),
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = { },
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_panel_bold),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
        IconButton(
            onClick = {
                noteTitleState.annotatedString = buildAnnotatedString {
                    append(noteTitleState.annotatedString)
                    addStyle(
                        style = SpanStyle(fontStyle = FontStyle.Italic),
                        start = noteTitleState.selection.min,
                        end = noteTitleState.selection.max,
                    )
                }
            },
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_panel_italic),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
        IconButton(
            onClick = { },
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_panel_underline),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
        IconButton(
            onClick = { },
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_panel_strikethrough),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
        IconButton(
            onClick = { },
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_panel_font_color),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
        IconButton(
            onClick = {
                noteTitleState.annotatedString = buildAnnotatedString {
                    append(noteTitleState.annotatedString)
                    addStyle(
                        style = SpanStyle(background = Color.Red),
                        start = noteTitleState.selection.min,
                        end = noteTitleState.selection.max,
                    )
                }
            },
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_panel_fill_color),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@PreviewWithBackground
@Composable
private fun SelectedPanelPreview() {
    SerenityTheme {
        SelectedPanel()
    }
}
