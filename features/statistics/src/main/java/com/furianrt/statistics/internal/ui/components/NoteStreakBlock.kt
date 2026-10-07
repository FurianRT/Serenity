package com.furianrt.statistics.internal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.furianrt.statistics.R
import com.furianrt.uikit.R as uiR
import com.furianrt.statistics.internal.ui.entities.StreakDay
import com.furianrt.statistics.internal.ui.entities.StreakStats
import com.furianrt.uikit.extensions.pxToDp
import com.furianrt.uikit.theme.LocalIsLightTheme
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import com.furianrt.uikit.utils.brighterBy
import com.furianrt.uikit.utils.darkerBy
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.HazeColorEffect
import dev.chrisbanes.haze.blur.hazeBlur
import dev.chrisbanes.haze.glass.LocalGlassStyle
import dev.chrisbanes.haze.glass.hazeGlass
import dev.chrisbanes.haze.rememberHazeState
import java.time.LocalDate
import java.time.format.TextStyle

@OptIn(ExperimentalHazeApi::class)
@Composable
internal fun NoteStreakBlock(
    stats: StreakStats,
    hazeState: HazeState,
    onDayClick: (item: StreakDay) -> Unit,
    modifier: Modifier = Modifier,
) {
    val glassStyle = LocalGlassStyle.current

    var streakHeight by remember { mutableIntStateOf(0) }

    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.anim_streak_fire),
    )

    val lottieState = animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        speed = 0.6f,
    )

    Column(
        modifier = modifier.hazeGlass(
            input = HazeInput.Sources(hazeState),
            style = remember(glassStyle) {
                glassStyle.then {
                    tint(Color.Transparent)
                    shape(RoundedCornerShape(16.dp))
                    whitePoint(0.06f)
                }
            },
        ),
    ) {
        Row(
            modifier = Modifier.onSizeChanged { streakHeight = it.height },
        ) {
            StreakCard(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.stats_current_streak_title),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    LottieAnimation(
                        modifier = Modifier
                            .offset(y = (-4).dp)
                            .size(32.dp),
                        composition = composition,
                        progress = { lottieState.progress * 0.99f },
                    )
                    Text(
                        text = stats.currentStreak.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            }
            Box(
                modifier = Modifier
                    .height(streakHeight.pxToDp())
                    .width(2.dp)
                    .hazeBlur(
                        input = HazeInput.Sources(hazeState),
                        style = HazeBlurStyle {
                            blurRadius(0.dp)
                            noiseFactor(0f)
                            colorEffects(
                                listOf(HazeColorEffect.tint(Color.Transparent)),
                            )
                        },
                    )
            )
            StreakCard(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.stats_longest_streak_title),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Text(
                        text = stats.longestStreak.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text(
                        modifier = Modifier.padding(bottom = 1.dp),
                        text = pluralStringResource(
                            R.plurals.stats_days_count,
                            stats.longestStreak,
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .height(2.dp)
                .fillMaxWidth()
                .hazeBlur(
                    input = HazeInput.Sources(hazeState),
                    style = HazeBlurStyle {
                        blurRadius(0.dp)
                        noiseFactor(0f)
                        colorEffects(
                            listOf(HazeColorEffect.tint(Color.Transparent)),
                        )
                    },
                )
        )
        StreakDays(
            modifier = Modifier
                .padding(start = 11.dp, end = 11.dp, top = 11.dp, bottom = 15.dp)
                .fillMaxWidth(),
            days = stats.streakDays,
            onClick = onDayClick,
        )
    }
}

@Composable
private fun StreakCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val titleStyle = MaterialTheme.typography.labelSmall

    Column(
        modifier = modifier.padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        BasicText(
            modifier = Modifier,
            text = title,
            style = titleStyle,
            color = MaterialTheme.colorScheme::onSurface,
            maxLines = 1,
            autoSize = TextAutoSize.StepBased(
                minFontSize = titleStyle.fontSize / 2,
                maxFontSize = titleStyle.fontSize,
            ),
            overflow = TextOverflow.Ellipsis,
        )
        content()
    }
}

@Composable
private fun StreakDays(
    days: List<StreakDay>,
    onClick: (item: StreakDay) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        days.forEach { day ->
            StreakDayItem(
                modifier = Modifier.weight(1f),
                item = day,
                onClick = onClick,
            )
        }
    }
}

@Composable
private fun StreakDayItem(
    item: StreakDay,
    onClick: (item: StreakDay) -> Unit,
    modifier: Modifier = Modifier,
) {
    val locale = LocalLocale.current.platformLocale
    val dayShape = RoundedCornerShape(8.dp)
    val dateTextStyle = MaterialTheme.typography.titleSmall
    val isLightTheme = LocalIsLightTheme.current

    val currentDate = remember(item.date) {
        LocalDate.now()
    }

    Column(
        modifier = modifier
            .clip(dayShape)
            .clickable { onClick(item) }
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = item.date.dayOfWeek.getDisplayName(TextStyle.SHORT_STANDALONE, locale),
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            color = if (currentDate == item.date) {
                if (isLightTheme) {
                    MaterialTheme.colorScheme.primaryContainer.darkerBy(0.12f)
                } else {
                    MaterialTheme.colorScheme.primaryContainer.brighterBy(0.12f)
                }
            } else {
                MaterialTheme.colorScheme.onSurface
            },
        )
        Spacer(Modifier.size(2.dp))
        Text(
            modifier = Modifier.padding(2.dp),
            text = item.date.dayOfMonth.toString(),
            style = dateTextStyle,
            maxLines = 1,
            color = if (currentDate == item.date) {
                if (isLightTheme) {
                    MaterialTheme.colorScheme.primaryContainer.darkerBy(0.12f)
                } else {
                    MaterialTheme.colorScheme.primaryContainer.brighterBy(0.12f)
                }
            } else {
                MaterialTheme.colorScheme.onSurface
            },
        )
        Spacer(Modifier.size(6.dp))
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .fillMaxWidth()
                .then(
                    if (item.hasNotes) {
                        Modifier.background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = dayShape,
                        )
                    } else {
                        Modifier.border(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            width = 1.dp,
                            shape = dayShape,
                        )
                    }
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (item.hasNotes) {
                Icon(
                    painter = painterResource(uiR.drawable.ic_action_done),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    contentDescription = null,
                )
            } else {
                Icon(
                    modifier = Modifier.padding(6.dp),
                    painter = painterResource(uiR.drawable.ic_add),
                    tint = MaterialTheme.colorScheme.primaryContainer,
                    contentDescription = null,
                )
            }
        }
    }
}

@PreviewWithBackground
@Composable
private fun Preview() {
    SerenityTheme {
        NoteStreakBlock(
            stats = StreakStats(
                currentStreak = 23,
                longestStreak = 32,
                streakDays = buildList {
                    repeat(7) { index ->
                        add(
                            StreakDay(
                                hasNotes = index % 2 == 0,
                                date = LocalDate.now(),
                            ),
                        )
                    }
                },
            ),
            hazeState = rememberHazeState(),
            onDayClick = {},
        )
    }
}
