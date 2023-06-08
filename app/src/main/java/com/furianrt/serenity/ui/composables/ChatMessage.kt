package com.furianrt.serenity.ui.composables

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import com.furianrt.uikit.theme.SerenityTheme

@Composable
fun ChatMessage(modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = "Hi, i’m your personal AI powered assistant. I can do a lot of things. Let me show you!",
        style = MaterialTheme.typography.bodySmall,
        fontStyle = FontStyle.Italic,
    )
}

@Preview
@Composable
private fun MainSuccessPreview() {
    SerenityTheme {
        ChatMessage()
    }
}
