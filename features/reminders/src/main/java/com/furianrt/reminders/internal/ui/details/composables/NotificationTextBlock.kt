package com.furianrt.reminders.internal.ui.details.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.furianrt.reminders.R
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import com.furianrt.uikit.R as uiR

@Composable
internal fun NotificationTextBlock(
    text: String,
    onClick: () -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.background)
            .clickable(onClick = onClick)
            .padding(start = 16.dp, end = 8.dp, top = 14.dp, bottom = 14.dp)
            .animateContentSize(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier
                .weight(1f)
                .alpha(if (text.isEmpty()) 0.5f else 1f),
            text = text.ifEmpty { stringResource(R.string.reminders_notification_text_hint) },
            style = MaterialTheme.typography.bodyMedium,
        )
        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 40.dp) {
            IconButton(
                onClick = { if (text.isEmpty()) onClick() else onClearClick() },
            ) {
                Icon(
                    modifier = Modifier.alpha(0.5f),
                    painter = if (text.isEmpty()) {
                        painterResource(uiR.drawable.ic_action_edit)
                    } else {
                        painterResource(uiR.drawable.ic_exit)
                    },
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = null,
                )
            }
        }
    }
}

@PreviewWithBackground
@Composable
private fun PreviewEmpty() {
    SerenityTheme {
        NotificationTextBlock(
            text = "",
            onClick = {},
            onClearClick = {},
        )
    }
}

@PreviewWithBackground
@Composable
private fun PreviewNotEmpty() {
    SerenityTheme {
        NotificationTextBlock(
            text = "How was your day?",
            onClick = {},
            onClearClick = {},
        )
    }
}
