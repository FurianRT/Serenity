package com.furianrt.search.internal.ui.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.unit.dp
import com.furianrt.notesearch.R
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.HazeColorEffect
import dev.chrisbanes.haze.blur.hazeBlur

@Composable
internal fun NotesCountItem(
    count: Int,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .hazeBlur(
                input = HazeInput.Sources(hazeState),
                style = HazeBlurStyle {
                    blurRadius(16.dp)
                    noiseFactor(0f)
                    colorEffects(
                        listOf(HazeColorEffect.tint(Color.Transparent))
                    )
                },
            )
            .padding(horizontal = 8.dp)
            .alpha(0.5f),
        text = pluralStringResource(R.plurals.note_search_notes_count_title, count, count),
        style = MaterialTheme.typography.labelSmall,
    )
}