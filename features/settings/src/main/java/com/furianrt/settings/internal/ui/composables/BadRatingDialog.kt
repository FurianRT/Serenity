package com.furianrt.settings.internal.ui.composables

import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.furianrt.settings.R
import com.furianrt.uikit.R as uiR
import com.furianrt.uikit.components.ConfirmationDialog
import com.furianrt.uikit.components.ConfirmationDialogButton
import com.furianrt.uikit.theme.SerenityTheme
import dev.chrisbanes.haze.HazeState

@Composable
internal fun BadRatingDialog(
    hazeState: HazeState,
    onSendClick: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.anim_bad_rating_morty),
    )
    val lottieState = animateLottieCompositionAsState(
        composition = composition,
        reverseOnRepeat = true,
        iterations = LottieConstants.IterateForever,
        speed = 1.5f,
    )
    ConfirmationDialog(
        modifier = modifier,
        cancelButton = {
            ConfirmationDialogButton(
                title = stringResource(uiR.string.action_cancel),
                textColor = MaterialTheme.colorScheme.primary,
                onClick = onDismissRequest,
            )
        },
        confirmButton = {
            ConfirmationDialogButton(
                title = stringResource(uiR.string.action_contact_us),
                textColor = MaterialTheme.colorScheme.primaryContainer,
                onClick = {
                    onSendClick()
                    onDismissRequest()
                },
            )
        },
        icon = {
            LottieAnimation(
                modifier = Modifier.size(100.dp),
                composition = composition,
                progress = { lottieState.progress },
            )
        },
        title = AnnotatedString(stringResource(R.string.settings_bad_feedback_title)),
        hint = AnnotatedString(stringResource(R.string.settings_bad_feedback_body)),
        hazeState = hazeState,
        onDismissRequest = onDismissRequest,
    )
}

@Composable
@Preview
private fun Preview() {
    SerenityTheme {
        BadRatingDialog(
            hazeState = HazeState(),
            onSendClick = {},
            onDismissRequest = {},
        )
    }
}
