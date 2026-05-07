package com.furianrt.reminders.internal.ui.details.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.furianrt.reminders.R
import com.furianrt.reminders.internal.ui.entities.DayItem
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import com.kizitonwose.calendar.core.daysOfWeek
import java.time.format.TextStyle

@Composable
internal fun DaysOfWeekPanel(
    daysOfWeek: List<DayItem>,
    onDayClick: (day: DayItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            text = stringResource(R.string.reminders_days_of_week_title),
            style = MaterialTheme.typography.bodyMedium,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            daysOfWeek.forEach { day ->
                DayOnWeekChip(
                    modifier = Modifier.weight(1f),
                    day = day,
                    onClick = onDayClick,
                )
            }
        }
    }
}

@Composable
private fun DayOnWeekChip(
    day: DayItem,
    onClick: (day: DayItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val locale = LocalLocale.current.platformLocale
    val haptic = LocalHapticFeedback.current
    Box(
        modifier = modifier
            .clip(CircleShape)
            .aspectRatio(1f)
            .background(
                if (day.isSelected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.background
                }
            )
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                onClick(day)
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.day.getDisplayName(TextStyle.NARROW, locale),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
    }
}

@PreviewWithBackground
@Composable
private fun Preview() {
    SerenityTheme {
        DaysOfWeekPanel(
            daysOfWeek = daysOfWeek().mapIndexed { index, day ->
                DayItem(
                    day = day,
                    isSelected = index % 2 == 0,
                )
            },
            onDayClick = {},
        )
    }
}
