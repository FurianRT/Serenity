package com.furianrt.backup.internal.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import com.furianrt.uikit.R as uiR

@Composable
internal fun SuccessSyncSnackBar(
    title: String,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .widthIn(min = 200.dp)
            .clip(RoundedCornerShape(16.dp))
            .hazeEffect(
                state = hazeState,
                style = HazeDefaults.style(
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    tint = HazeTint(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                    blurRadius = 16.dp,
                )
            )
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            modifier = Modifier.size(64.dp),
            painter = painterResource(uiR.drawable.ic_action_done),
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = null,
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
        )
    }
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        SuccessSyncSnackBar(
            title = "Successful",
            hazeState = HazeState(),
        )
    }
}
