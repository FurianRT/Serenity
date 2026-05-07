package com.furianrt.reminders.internal.ui.details.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.furianrt.reminders.R
import com.furianrt.uikit.R as uiR
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground

@Composable
internal fun NotificationTextBlock(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.background)
            .clickable(onClick = onClick)
            .padding(vertical = 20.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            modifier = Modifier
                .weight(1f)
                .alpha(if (text.isEmpty()) 0.5f else 1f),
            text = text.ifEmpty { stringResource(R.string.reminders_notification_text_hint) },
            style = MaterialTheme.typography.bodyMedium,
        )
        Icon(
            modifier = Modifier.alpha(0.5f),
            painter = painterResource(uiR.drawable.ic_action_edit),
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = null,
        )
    }
}

@PreviewWithBackground
@Composable
private fun PreviewEmpty() {
    SerenityTheme {
        NotificationTextBlock(
            text = "",
            onClick = {},
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
        )
    }
}
