package com.furianrt.reminders.internal.ui.help.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.furianrt.reminders.R
import com.furianrt.uikit.R as uiR
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground

@Composable
internal fun HelpOption(
    title: String,
    hint: String,
    warningTint: Color,
    actionText: String?,
    hasWarning: Boolean,
    modifier: Modifier = Modifier,
    onActionClick: () -> Unit = {},
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = title,
                style = MaterialTheme.typography.bodyMedium,
            )
            if (hasWarning) {
                Icon(
                    painter = painterResource(uiR.drawable.ic_warning),
                    tint = warningTint,
                    contentDescription = null,
                )
            } else {
                Icon(
                    painter = painterResource(uiR.drawable.ic_action_done),
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = null,
                )
            }
        }
        if (hasWarning) {
            Spacer(Modifier.height(8.dp))
            Text(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .alpha(0.5f),
                text = hint,
                style = MaterialTheme.typography.labelSmall,
            )
            if (actionText != null) {
                TextButton(
                    onClick = onActionClick,
                ) {
                    Text(
                        text = actionText,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primaryContainer,
                    )
                }
            }
        }
    }
}

@PreviewWithBackground
@Composable
private fun PreviewWithWarning() {
    SerenityTheme {
        HelpOption(
            title = stringResource(R.string.reminders_troubleshooting_alarms_permission_title),
            hint = stringResource(R.string.reminders_troubleshooting_alarms_permission_hint),
            warningTint = MaterialTheme.colorScheme.errorContainer,
            actionText = stringResource(R.string.reminders_troubleshooting_open_settings_title),
            hasWarning = true,
        )
    }
}

@PreviewWithBackground
@Composable
private fun PreviewWithoutWarning() {
    SerenityTheme {
        HelpOption(
            title = stringResource(R.string.reminders_troubleshooting_alarms_permission_title),
            hint = stringResource(R.string.reminders_troubleshooting_alarms_permission_hint),
            warningTint = MaterialTheme.colorScheme.errorContainer,
            actionText = stringResource(R.string.reminders_troubleshooting_open_settings_title),
            hasWarning = false,
        )
    }
}
