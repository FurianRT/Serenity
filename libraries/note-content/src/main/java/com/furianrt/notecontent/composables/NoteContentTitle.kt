package com.furianrt.notecontent.composables

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.furianrt.notecontent.entities.UiNoteContent
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.theme.Colors
import com.furianrt.uikit.theme.SerenityTheme

@Composable
fun NoteContentTitle(
    title: UiNoteContent.Title,
    modifier: Modifier = Modifier,
    hint: String? = null,
    isEditable: Boolean = false,
    onTitleChange: (text: String) -> Unit = {},
    onTitleClick: ((id: String) -> Unit)? = null,
) {
    var titleText by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(text = title.text))
    }

    val clickableModifier = if (onTitleClick != null && !isEditable) {
        Modifier.clickableNoRipple { onTitleClick(title.id) }
    } else {
        Modifier
    }

    BasicTextField(
        modifier = modifier
            .then(clickableModifier)
            .onFocusChanged {
                titleText = TextFieldValue(
                    text = title.text,
                    selection = TextRange(title.text.length),
                )
            },
        enabled = isEditable,
        value = titleText,
        onValueChange = { text ->
            titleText = text
            onTitleChange(text.text)
        },
        textStyle = MaterialTheme.typography.bodyMedium,
        cursorBrush = SolidColor(Colors.Blue),
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        decorationBox = { innerTextField ->
            if (hint != null && titleText.text.isEmpty()) {
                Placeholder(hint = hint)
            }
            innerTextField()
        },
    )
}

@Composable
private fun Placeholder(hint: String) {
    Text(
        modifier = Modifier.alpha(0.3f),
        text = hint,
        style = MaterialTheme.typography.labelMedium,
        fontStyle = FontStyle.Italic,
    )
}

@Preview
@Composable
private fun NoteContentTitlePreview() {
    SerenityTheme {
        NoteContentTitle(
            title = UiNoteContent.Title(
                id = "1",
                position = 0,
                text = "Kotlin is a modern programming language with a " +
                    "lot more syntactic sugar compared to Java, and as such " +
                    "there is equally more black magic",
            ),
            hint = "Text",
        )
    }
}
