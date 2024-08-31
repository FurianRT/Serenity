package com.furianrt.mediaselector.internal.ui.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.furianrt.mediaselector.internal.ui.entities.SelectionState
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.theme.SerenityTheme

@Composable
internal fun CheckBox(
    state: SelectionState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        modifier = modifier
            .size(24.dp)
            .clickableNoRipple(onClick = onClick),
        targetState = state,
        contentKey = { it.javaClass.simpleName},
        label = "CheckBoxAnim",
    ) { targetState ->
        when (targetState) {
            is SelectionState.Default -> Box(
                modifier = Modifier
                    .background(color = Color.Black.copy(alpha = 0.1f), shape = CircleShape)
                    .border(width = 1.5.dp, color = Color.White, shape = CircleShape),
            )

            is SelectionState.Selected -> Box(
                modifier = Modifier.background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape,
                ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = targetState.order.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
        }
    }
}

@Composable
@Preview
private fun PreviewSelected() {
    SerenityTheme {
        CheckBox(
            onClick = {},
            state = SelectionState.Selected(order = 1),
        )
    }
}

@Composable
@Preview
private fun PreviewDefault() {
    SerenityTheme {
        CheckBox(
            onClick = {},
            state = SelectionState.Default,
        )
    }
}
