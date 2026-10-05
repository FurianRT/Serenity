package com.furianrt.statistics.internal.ui.components

import android.icu.text.DecimalFormat
import android.icu.text.DecimalFormatSymbols
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.furianrt.common.truncateToMaxDigits
import com.furianrt.statistics.R
import com.furianrt.uikit.R as uiR
import com.furianrt.statistics.internal.ui.StatsState
import com.furianrt.uikit.theme.Colors
import com.furianrt.uikit.theme.LocalIsLightTheme
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import com.furianrt.uikit.utils.brighterBy
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.glass.LocalGlassStyle
import dev.chrisbanes.haze.glass.hazeGlass
import dev.chrisbanes.haze.rememberHazeState

private val symbols = DecimalFormatSymbols()

private val df = DecimalFormat("#.#", symbols)

@Composable
internal fun GeneralStatsBlock(
    stats: StatsState.GeneralStats,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        GeneralStatCard(
            modifier = Modifier.weight(1f),
            title = stringResource(R.string.stats_notes_title),
            icon = painterResource(uiR.drawable.ic_notes),
            value = stats.notesCount.toFloat(),
            valueDigits = 0,
            change = stats.notesChange,
            hazeState = hazeState,
        )
        GeneralStatCard(
            modifier = Modifier.weight(1f),
            title = stringResource(R.string.stats_media_title),
            icon = painterResource(uiR.drawable.ic_photos),
            value = stats.mediaCount.toFloat(),
            valueDigits = 0,
            change = stats.mediaChange,
            hazeState = hazeState,
        )
        GeneralStatCard(
            modifier = Modifier.weight(1f),
            title = stringResource(R.string.stats_mood_title),
            icon = painterResource(uiR.drawable.ic_smile),
            value = stats.averageMood,
            valueDigits = 1,
            change = stats.moodChange,
            changePostfix = "",
            forMood = true,
            hazeState = hazeState,
        )
    }
}

@OptIn(ExperimentalHazeApi::class)
@Composable
internal fun GeneralStatCard(
    title: String,
    value: Float,
    valueDigits: Int,
    change: Float,
    icon: Painter,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
    forMood: Boolean = false,
    changePostfix: String = "%",
    shape: RoundedCornerShape = RoundedCornerShape(12.dp),
) {
    val glassStyle = LocalGlassStyle.current

    Column(
        modifier = modifier
            .hazeGlass(
                input = HazeInput.Sources(hazeState),
                style = remember(glassStyle) {
                    glassStyle.then {
                        tint(Color.Transparent)
                        shape(shape)
                        whitePoint(0.06f)
                    }
                },
            )
            .padding(horizontal = 8.dp, vertical = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = icon,
                tint = if (LocalIsLightTheme.current) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.primaryContainer.brighterBy(0.12f)
                },
                contentDescription = null,
            )
            if (change != 0f) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    ValueChange(
                        value = change,
                        digits = valueDigits,
                        postfix = changePostfix,
                    )
                }
            }
        }
        Spacer(Modifier.size(16.dp))
        Title(
            text = title,
        )
        Spacer(Modifier.size(8.dp))
        ValueText(
            value = value,
            valueDigits = valueDigits,
            forMood = forMood,
        )
    }
}

@Composable
private fun Title(
    text: String,
    modifier: Modifier = Modifier,
) {
    val style = MaterialTheme.typography.labelSmall
    BasicText(
        modifier = modifier,
        text = text,
        style = style,
        color = MaterialTheme.colorScheme::onSurface,
        maxLines = 1,
        autoSize = TextAutoSize.StepBased(
            minFontSize = style.fontSize / 2,
            maxFontSize = style.fontSize,
        ),
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun ValueText(
    value: Float,
    valueDigits: Int,
    forMood: Boolean,
    modifier: Modifier = Modifier,
) {
    val valueAnim by animateFloatAsState(
        targetValue = value,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
    )

    val resultValue by remember {
        derivedStateOf { valueAnim.truncateToMaxDigits(valueDigits) }
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        Text(
            modifier = Modifier,
            text = when {
                resultValue == 0f -> "--"
                valueDigits == 0 -> resultValue.toInt().toString()
                else -> resultValue.toString()
            },
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
        )
        if (forMood && resultValue != 0f) {
            Text(
                modifier = Modifier
                    .alpha(0.6f)
                    .padding(bottom = 1.dp),
                text = "/5",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleSmall,
            )
        }
    }
}

@Composable
private fun ValueChange(
    value: Float,
    digits: Int,
    modifier: Modifier = Modifier,
    postfix: String,
) {
    val valueAnim by animateFloatAsState(
        targetValue = value,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
    )

    val resultValue by remember { derivedStateOf { valueAnim.truncateToMaxDigits(digits) } }

    val style = MaterialTheme.typography.bodySmall
    
    Text(
        modifier = modifier,
        text = if (resultValue > 0f) {
            "+${df.format(resultValue)}$postfix"
        } else {
            "${df.format(resultValue)}$postfix"
        },
        color = if (resultValue > 0f) {
            if (LocalIsLightTheme.current) {
                Colors.Common.SuccessLight
            } else {
                Colors.Common.SuccessDark
            }
        } else {
            MaterialTheme.colorScheme.error
        },
        style = style,
        fontSize = style.fontSize * 0.9f,
        letterSpacing = style.letterSpacing * 0.8f,
    )
}

@PreviewWithBackground
@Composable
private fun Preview() {
    SerenityTheme {
        GeneralStatsBlock(
            stats = StatsState.GeneralStats(
                notesCount = 142,
                notesChange = 12f,
                mediaCount = 251,
                mediaChange = -17.2f,
                averageMood = 4.8f,
                moodChange = -0.2f,
            ),
            hazeState = rememberHazeState(),
        )
    }
}
