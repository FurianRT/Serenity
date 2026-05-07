package com.furianrt.reminders.internal.ui.details.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.darkokoa.datetimewheelpicker.WheelTimePicker
import dev.darkokoa.datetimewheelpicker.core.WheelPickerDefaults
import dev.darkokoa.datetimewheelpicker.core.format.TimeFormat
import dev.darkokoa.datetimewheelpicker.core.format.timeFormatter
import kotlinx.datetime.toJavaLocalTime
import kotlinx.datetime.toKotlinLocalTime
import java.time.LocalTime

@Composable
internal fun TimePicker(
    startTime: LocalTime,
    onSnappedTimeChanged: (snappedTime: LocalTime) -> Unit,
    onSnappedTime: (snappedTime: LocalTime) -> Unit,
    modifier: Modifier = Modifier,
) {
    val timeFormatter = timeFormatter(Locale.current)
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        WheelTimePicker(
            startTime = startTime.toKotlinLocalTime(),
            size = if (timeFormatter.timeFormat == TimeFormat.HOUR_24) {
                DpSize(190.dp, 200.dp)
            } else {
                DpSize(280.dp, 200.dp)
            },
            textStyle = MaterialTheme.typography.titleLarge.copy(
                fontSize = 38.sp,
                fontWeight = FontWeight.SemiBold,
            ),
            timeFormatter = timeFormatter,
            onSnappedTimeChanged = { time -> onSnappedTimeChanged(time.toJavaLocalTime()) },
            onSnappedTime = { time -> onSnappedTime(time.toJavaLocalTime()) },
            selectorProperties = WheelPickerDefaults.selectorProperties(
                color = MaterialTheme.colorScheme.tertiary,
                border = null,
                shape = RoundedCornerShape(32.dp),
            )
        )
    }
}

@PreviewWithBackground
@Composable
private fun Preview() {
    SerenityTheme {
        TimePicker(
            startTime = LocalTime.now(),
            onSnappedTime = {},
            onSnappedTimeChanged = {},
        )
    }
}
