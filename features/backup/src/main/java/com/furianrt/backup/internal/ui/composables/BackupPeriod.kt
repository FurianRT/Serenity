package com.furianrt.backup.internal.ui.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.furianrt.backup.R
import com.furianrt.backup.internal.domain.entities.BackupPeriod
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground

@Composable
internal fun BackupPeriod(
    period: BackupPeriod,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val alpha by animateFloatAsState(targetValue = if (isEnabled) 1f else 0.5f)
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(enabled = isEnabled, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .alpha(alpha),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(R.string.backup_auto_backup_period_title),
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            modifier = Modifier.alpha(0.5f),
            text = getBackupPeriodTitle(period),
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        BackupPeriod(
            period = BackupPeriod.TreeDays,
            isEnabled = true,
            onClick = {},
        )
    }
}
