package com.furianrt.reminders.internal.ui.list.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.furianrt.reminders.internal.ui.entities.DayItem
import com.furianrt.reminders.internal.ui.list.entities.ReminderItem
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import com.kizitonwose.calendar.core.daysOfWeek
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.rememberHazeState
import java.time.format.TextStyle
import com.furianrt.uikit.R as uiR

@Composable
internal fun ReminderListItem(
    item: ReminderItem,
    hazeState: HazeState,
    onClick: (item: ReminderItem) -> Unit,
    onDeleteClick: (item: ReminderItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hasSelectedDays = remember(item.daysOfWeek) { item.daysOfWeek.any(DayItem::isSelected) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 82.dp)
            .clip(RoundedCornerShape(12.dp))
            .hazeEffect(
                state = hazeState,
                style = HazeDefaults.style(
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    blurRadius = 12.dp,
                    tint = HazeTint(MaterialTheme.colorScheme.background),
                )
            )
            .clickable { onClick(item) }
            .padding(
                top = 8.dp,
                bottom = if (item.title != null) 16.dp else 8.dp,
                start = 12.dp,
                end = 6.dp
            )
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(
            space = 4.dp,
            alignment = if (item.title != null) Alignment.Top else Alignment.CenterVertically,
        )
    ) {
        if (item.title != null) {
            Title(
                text = item.title,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Time(
                modifier = Modifier.weight(1f),
                text = item.time,
            )
            Spacer(Modifier.width(8.dp))
            if (hasSelectedDays) {
                Days(
                    days = item.daysOfWeek,
                )
                Spacer(Modifier.width(4.dp))
            }
            ButtonDelete(
                onClick = { onDeleteClick(item) },
            )
        }
    }
}

@Composable
private fun Title(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.labelSmall,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun Time(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.titleLarge,
        fontSize = 24.sp,
    )
}

@Composable
private fun Days(
    days: List<DayItem>,
    modifier: Modifier = Modifier,
) {
    val locale = LocalLocale.current.platformLocale
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        days.forEach { dayItem ->
            Text(
                modifier = Modifier.alpha(if (dayItem.isSelected) 1f else 0.5f),
                text = dayItem.day.getDisplayName(TextStyle.NARROW, locale),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = MaterialTheme.typography.bodySmall.fontSize * 0.8f,
                    fontWeight = FontWeight.Bold
                ),
            )
        }
    }
}

@Composable
private fun ButtonDelete(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    IconButton(
        modifier = modifier,
        onClick = onClick,
    ) {
        Icon(
            painter = painterResource(uiR.drawable.ic_delete),
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = null,
        )
    }
}

@PreviewWithBackground
@Composable
private fun PreviewWithTitle() {
    SerenityTheme {
        ReminderListItem(
            item = ReminderItem(
                id = "",
                title = "How was your day?",
                time = "6:00 PM",
                daysOfWeek = daysOfWeek().mapIndexed { index, day ->
                    DayItem(
                        day = day,
                        isSelected = index % 2 == 0,
                    )
                },
            ),
            hazeState = rememberHazeState(),
            onClick = {},
            onDeleteClick = {},
        )
    }
}

@PreviewWithBackground
@Composable
private fun PreviewWithoutTitle() {
    SerenityTheme {
        ReminderListItem(
            item = ReminderItem(
                id = "",
                title = null,
                time = "6:00 PM",
                daysOfWeek = daysOfWeek().mapIndexed { index, day ->
                    DayItem(
                        day = day,
                        isSelected = index % 2 == 0,
                    )
                },
            ),
            hazeState = rememberHazeState(),
            onClick = {},
            onDeleteClick = {},
        )
    }
}
