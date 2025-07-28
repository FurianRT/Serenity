package com.furianrt.notelistui.composables

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieDynamicProperty
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.model.KeyPath
import com.furianrt.uikit.R
import com.furianrt.uikit.components.ConfirmationDialog
import com.furianrt.uikit.components.SkipFirstEffect
import com.furianrt.uikit.extensions.clickableWithScaleAnim
import com.furianrt.uikit.theme.SerenityTheme
import dev.chrisbanes.haze.HazeState

@Composable
fun ConfirmNotesDeleteDialog(
    notesCount: Int,
    hazeState: HazeState,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    onConfirmClick: () -> Unit = {},
) {
    ConfirmationDialog(
        modifier = modifier,
        title = stringResource(R.string.delete_notes_warning_title),
        hint = if (notesCount > 1) {
            stringResource(R.string.delete_notes_warning_body)
        } else {
            stringResource(R.string.delete_note_warning_body)
        },
        icon = { DialogIcon() },
        confirmText = stringResource(R.string.action_delete),
        hazeState = hazeState,
        onConfirmClick = onConfirmClick,
        onDismissRequest = onDismissRequest,
    )
}

@Composable
private fun DialogIcon(
    modifier: Modifier = Modifier,
) {
    var isPlaying by remember { mutableStateOf(true) }
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.anim_delete_note),
    )
    val lottieState = animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isPlaying,
        speed = 1.3f,
    )
    val dynamicProperties = rememberLottieDynamicProperties(
        LottieDynamicProperty(
            property = LottieProperty.COLOR,
            value = MaterialTheme.colorScheme.onSurface.toArgb(),
            keyPath = KeyPath("**"),
        ),
        LottieDynamicProperty(
            property = LottieProperty.STROKE_COLOR,
            value = MaterialTheme.colorScheme.onSurface.toArgb(),
            keyPath = KeyPath("**"),
        ),
    )
    SkipFirstEffect(lottieState.isPlaying) {
        isPlaying = lottieState.isPlaying
    }

    LottieAnimation(
        modifier = modifier
            .size(70.dp)
            .scale(2.8f)
            .offset(y = 2.dp)
            .clickableWithScaleAnim { isPlaying = true },
        composition = composition,
        progress = { (lottieState.progress + 0.2f).coerceAtMost(1f) },
        dynamicProperties = dynamicProperties,
    )
}

@Composable
@Preview
private fun Preview(modifier: Modifier = Modifier) {
    SerenityTheme {
        ConfirmNotesDeleteDialog(
            notesCount = 3,
            hazeState = HazeState(),
            onDismissRequest = {},
        )
    }
}