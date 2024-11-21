package com.furianrt.settings.internal.ui.security

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.unit.dp
import com.furianrt.settings.R
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeChild
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val PIN_DELAYS: ImmutableList<Int> = persistentListOf(
    5 * 1000,
    15 * 1000,
    30 * 1000,
    60 * 1000,
    5 * 60 * 1000,
    15 * 60 * 1000,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PinDelayDialog(
    selectedDelay: Int,
    hazeState: HazeState,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    onDelayClick: (delay: Int) -> Unit = {},
) {
    val scope = rememberCoroutineScope()
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
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.3f))
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            PIN_DELAYS.forEach { delay ->
                RadioButtonWithText(
                    modifier = Modifier.fillMaxWidth(),
                    title = if (delay < 60 * 1000) {
                        pluralStringResource(
                            R.plurals.settings_pin_delay_seconds_plural,
                            delay / 1000,
                            delay / 1000,
                        )
                    } else {
                        pluralStringResource(
                            R.plurals.settings_pin_delay_minutes_plural,
                            delay / 60 / 1000,
                            delay / 60 / 1000,
                        )
                    },
                    isSelected = delay == selectedDelay,
                    onClick = {
                        onDelayClick(delay)
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
private fun RadioButtonWithText(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primaryContainer,
                unselectedColor = MaterialTheme.colorScheme.onSurface,
            ),
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        PinDelayDialog(
            selectedDelay = 0,
            onDismissRequest = {},
            hazeState = HazeState(),
        )
    }
}
