package com.furianrt.mood.api.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import com.furianrt.mood.R
import com.furianrt.mood.internal.MoodHolder
import com.furianrt.mood.internal.entites.Mood
import com.furianrt.uikit.extensions.clickableUnbounded
import com.furianrt.uikit.extensions.drawBottomShadow
import com.furianrt.uikit.extensions.drawTopInnerShadow
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.LocalAuth
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodDialog(
    moodId: String?,
    defaultMoodId: String?,
    hazeState: HazeState,
    onMoodSelected: (mood: Mood?) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false,
) {
    val scope = rememberCoroutineScope()
    val auth = LocalAuth.current
    var isExpandedState by remember(isExpanded) { mutableStateOf(isExpanded) }

    LifecycleStartEffect(Unit) {
        scope.launch {
            if (!auth.isAuthorized()) {
                onDismissRequest()
            }
        }
        onStopOrDispose {}
    }

    val moodPacks = remember(moodId, defaultMoodId, isExpandedState) {
        val packs = MoodHolder.getMoodPacks()
        val topMoodId = moodId ?: defaultMoodId ?: packs.first().moods.first().id
        val sortedPacks = packs.sortedBy { pack -> pack.moods.none { it.id == topMoodId } }
        if (isExpandedState) {
            sortedPacks
        } else {
            sortedPacks.take(1)
        }
    }

    val listState = rememberLazyListState()
    val shadowColor = MaterialTheme.colorScheme.surfaceDim

    BasicAlertDialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .hazeEffect(
                    state = hazeState,
                    style = HazeDefaults.style(
                        backgroundColor = MaterialTheme.colorScheme.surface,
                        blurRadius = 20.dp,
                    )
                )
                .background(MaterialTheme.colorScheme.surfaceTint)
                .padding(top = 16.dp, bottom = 8.dp),
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        if (listState.canScrollBackward) {
                            drawBottomShadow(color = shadowColor, elevation = 2.dp)
                        }
                    }
                    .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                    .align(Alignment.CenterHorizontally),
                text = stringResource(R.string.mood_dialog_title),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
            )

            LazyColumn(
                modifier = Modifier.heightIn(max = 450.dp),
                state = listState,
                contentPadding = PaddingValues(vertical = 24.dp),
            ) {
                itemsIndexed(items = moodPacks) { index, pack ->
                    MoodBlock(
                        modifier = Modifier.padding(horizontal = 32.dp),
                        moods = pack.moods,
                        onClick = { mood ->
                            onMoodSelected(mood)
                            onDismissRequest()
                        },
                    )
                    if (index != moodPacks.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(top = 24.dp, bottom = 24.dp, start = 32.dp),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.tertiary,
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        if (listState.canScrollForward) {
                            drawTopInnerShadow(color = shadowColor, elevation = 2.dp)
                        }
                    }
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(
                    onClick = {
                        onMoodSelected(null)
                        onDismissRequest()
                    },
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(
                        text = stringResource(R.string.mood_dialog_clear),
                        style = MaterialTheme.typography.titleSmall,
                    )
                }
                if (!isExpandedState) {
                    Spacer(Modifier.width(8.dp))
                    TextButton(
                        onClick = { isExpandedState = true },
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.mood_dialog_more_emoji),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primaryContainer,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MoodBlock(
    moods: List<Mood>,
    onClick: (mood: Mood) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(3) { index ->
                MoodSell(
                    mood = moods[index],
                    onClick = onClick,
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(3) { index ->
                MoodSell(
                    mood = moods[index + 3],
                    onClick = onClick,
                )
            }
        }
    }
}

@Composable
private fun MoodSell(
    mood: Mood,
    onClick: (mood: Mood) -> Unit,
    modifier: Modifier = Modifier,
) {
    Icon(
        modifier = modifier
            .size(56.dp)
            .clickableUnbounded { onClick(mood) },
        painter = painterResource(mood.icon),
        tint = Color.Unspecified,
        contentDescription = null,
    )
}

@Composable
@PreviewWithBackground
private fun CollapsedPreview() {
    SerenityTheme {
        MoodDialog(
            moodId = null,
            defaultMoodId = null,
            isExpanded = false,
            hazeState = HazeState(),
            onMoodSelected = {},
            onDismissRequest = {},
        )
    }
}

@Composable
@PreviewWithBackground
private fun ExpandedPreview() {
    SerenityTheme {
        MoodDialog(
            moodId = null,
            defaultMoodId = null,
            isExpanded = true,
            hazeState = HazeState(),
            onMoodSelected = {},
            onDismissRequest = {},
        )
    }
}
