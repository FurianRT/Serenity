package com.furianrt.notepage.internal.ui.page.composables

import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieDynamicProperty
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.model.KeyPath
import com.furianrt.notepage.R
import com.furianrt.uikit.R as uiR
import com.furianrt.permissions.R as permissionsR
import com.furianrt.uikit.components.ConfirmationDialog
import com.furianrt.uikit.components.ConfirmationDialogButton
import com.furianrt.uikit.components.SkipFirstEffect
import com.furianrt.uikit.extensions.clickableWithScaleAnim
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeState

@Composable
internal fun DetectLocationDialog(
    hazeState: HazeState,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    onConfirmClick: () -> Unit = {},
) {
    ConfirmationDialog(
        modifier = modifier,
        cancelButton = {
            ConfirmationDialogButton(
                title = stringResource(uiR.string.action_no),
                textColor = MaterialTheme.colorScheme.primary,
                onClick = onDismissRequest,
            )
        },
        confirmButton = {
            ConfirmationDialogButton(
                title = stringResource(uiR.string.action_yes),
                textColor = MaterialTheme.colorScheme.primaryContainer,
                onClick = {
                    onConfirmClick()
                    onDismissRequest()
                },
            )
        },
        icon = { DialogIcon() },
        hint = AnnotatedString(stringResource(R.string.note_auto_detect_location_title)),
        hazeState = hazeState,
        onDismissRequest = onDismissRequest,
    )
}

@Composable
private fun DialogIcon(
    modifier: Modifier = Modifier,
) {
    var isPlaying by remember { mutableStateOf(true) }
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(permissionsR.raw.anim_location_access),
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
    )
    SkipFirstEffect(lottieState.isPlaying) {
        isPlaying = lottieState.isPlaying
    }

    LottieAnimation(
        modifier = modifier
            .size(62.dp)
            .graphicsLayer { translationY = -8.dp.toPx() }
            .clickableWithScaleAnim { isPlaying = true },
        composition = composition,
        progress = { lottieState.progress },
        dynamicProperties = dynamicProperties,
    )
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        DetectLocationDialog(
            hazeState = HazeState(),
            onDismissRequest = {},
        )
    }
}