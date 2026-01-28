package com.furianrt.mood.api.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.furianrt.mood.R
import com.furianrt.mood.internal.MoodHolder
import com.furianrt.mood.internal.entites.MoodPack
import com.furianrt.uikit.extensions.applyIf
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect

@Composable
fun MoodButton(
    moodId: String?,
    defaultMoodId: String?,
    modifier: Modifier = Modifier,
    hazeState: HazeState? = null,
    onClick: (() -> Unit)? = null,
) {
    val moodPacks = remember { MoodHolder.getMoodPacks() }
    val selectedMood = remember(moodId) {
        moodPacks.flatMap(MoodPack::moods).find { it.id == moodId }
    }

    val iconRes = remember(selectedMood, defaultMoodId) {
        if (selectedMood == null) {
            val pack = moodPacks.find { pack -> pack.moods.any { it.id == defaultMoodId } }
            pack?.icon ?: R.drawable.mood_raccoon_default
        } else {
            selectedMood.icon
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .then(
                if (hazeState != null) {
                    Modifier.hazeEffect(
                        state = hazeState,
                        style = HazeDefaults.style(
                            backgroundColor = MaterialTheme.colorScheme.surface,
                            blurRadius = 12.dp,
                            tint = HazeTint(MaterialTheme.colorScheme.secondaryContainer),
                        )
                    )
                } else {
                    Modifier.background(MaterialTheme.colorScheme.secondaryContainer)
                }
            )
            .applyIf(onClick != null) {
                Modifier.clickable { onClick?.invoke() }
            }
            .padding(5.dp),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            modifier = Modifier.alpha(if (selectedMood == null) 0.4f else 1f),
            painter = painterResource(iconRes),
            tint = Color.Unspecified,
            contentDescription = null,
        )
    }
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        MoodButton(
            moodId = null,
            defaultMoodId = null,
        )
    }
}
