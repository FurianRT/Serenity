package com.furianrt.backup.internal.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import com.furianrt.backup.R
import com.furianrt.backup.internal.domain.entities.BackupPeriod
import com.furianrt.uikit.components.RadioButtonWithText
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.LocalAuth
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeChild
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val BACKUP_PERIODS: ImmutableList<BackupPeriod> = persistentListOf(
    BackupPeriod.OneDay,
    BackupPeriod.TreeDays,
    BackupPeriod.OneWeek,
    BackupPeriod.TwoWeeks,
    BackupPeriod.OneMonth,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BackupPeriodDialog(
    selectedPeriod: BackupPeriod,
    hazeState: HazeState,
    onPeriodSelected: (period: BackupPeriod) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val auth = LocalAuth.current

    LifecycleStartEffect(Unit) {
        scope.launch {
            if (!auth.isAuthorized()) {
                onDismissRequest()
            }
        }
        onStopOrDispose {}
    }

    BasicAlertDialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .hazeChild(
                    state = hazeState,
                    style = HazeDefaults.style(
                        backgroundColor = MaterialTheme.colorScheme.surface,
                        blurRadius = 20.dp,
                    ),
                )
                .background(MaterialTheme.colorScheme.surfaceTint)
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            BACKUP_PERIODS.forEach { period ->
                RadioButtonWithText(
                    modifier = Modifier.fillMaxWidth(),
                    title = getBackupPeriodTitle(period),
                    isSelected = period == selectedPeriod,
                    onClick = {
                        onPeriodSelected(period)
                        scope.launch {
                            delay(150)
                            onDismissRequest()
                        }
                    },
                )
            }
        }
    }
}

@Composable
@ReadOnlyComposable
internal fun getBackupPeriodTitle(period: BackupPeriod): String = when (period) {
    is BackupPeriod.OneDay -> {
        stringResource(R.string.backup_period_one_day_title)
    }

    is BackupPeriod.TreeDays -> {
        stringResource(R.string.backup_period_three_days_title)
    }

    is BackupPeriod.OneWeek -> {
        stringResource(R.string.backup_period_one_week_title)
    }

    is BackupPeriod.TwoWeeks -> {
        stringResource(R.string.backup_period_two_weeks_title)
    }

    is BackupPeriod.OneMonth -> {
        stringResource(R.string.backup_period_one_month_title)
    }
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        BackupPeriodDialog(
            selectedPeriod = BackupPeriod.TreeDays,
            hazeState = HazeState(),
            onPeriodSelected = {},
            onDismissRequest = {},
        )
    }
}
