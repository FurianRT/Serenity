package com.furianrt.permissions.ui

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
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
import com.furianrt.permissions.R
import com.furianrt.uikit.components.ConfirmationDialog
import com.furianrt.uikit.components.SkipFirstEffect
import com.furianrt.uikit.extensions.clickableWithScaleAnim
import com.furianrt.uikit.theme.SerenityTheme
import dev.chrisbanes.haze.HazeState
import com.furianrt.uikit.R as uiR

@Composable
fun MediaPermissionDialog(
    hazeState: HazeState,
    onDismissRequest: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    var isPlaying by remember { mutableStateOf(true) }
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.anim_media_list),
    )
    val lottieState = animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isPlaying,
        speed = 1.2f,
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

    ConfirmationDialog(
        hint = buildAnnotatedString {
            val title = stringResource(R.string.media_permission_message)
            val boldPartOne = stringResource(R.string.media_permission_message_bold_part_1)
            val boldPartTwo = stringResource(R.string.media_permission_message_bold_part_2)
            val boldPartOneIndex = title.indexOf(boldPartOne)
            val boldPartTwoIndex = title.indexOf(boldPartTwo)
            append(title)
            if (boldPartOneIndex != -1) {
                addStyle(
                    style = SpanStyle(fontWeight = FontWeight.ExtraBold),
                    start = boldPartOneIndex,
                    end = boldPartOneIndex + boldPartOne.length,
                )
            }
            if (boldPartTwoIndex != -1) {
                addStyle(
                    style = SpanStyle(fontWeight = FontWeight.ExtraBold),
                    start = boldPartTwoIndex,
                    end = boldPartTwoIndex + boldPartTwo.length,
                )
            }
        },
        cancelButton = {
            ActionButton(
                title = stringResource(id = uiR.string.action_not_now),
                onClick = onDismissRequest,
            )
        },
        confirmButton = {
            ActionButton(
                title = stringResource(id = uiR.string.settings_title),
                textColor = MaterialTheme.colorScheme.primaryContainer,
                onClick = {
                    onSettingsClick()
                    onDismissRequest()
                },
            )
        },
        icon = {
            LottieAnimation(
                modifier = Modifier
                    .size(80.dp)
                    .clickableWithScaleAnim { isPlaying = true },
                composition = composition,
                progress = { lottieState.progress },
                dynamicProperties = dynamicProperties,
            )
        },
        hazeState = hazeState,
        onDismissRequest = onDismissRequest,
    )
}

@Composable
fun AudioRecordPermissionDialog(
    hazeState: HazeState,
    onDismissRequest: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    var isPlaying by remember { mutableStateOf(true) }
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.anim_audio_record),
    )
    val lottieState = animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isPlaying,
    )
    val dynamicProperties = rememberLottieDynamicProperties(
        LottieDynamicProperty(
            property = LottieProperty.STROKE_COLOR,
            value = MaterialTheme.colorScheme.onSurface.toArgb(),
            keyPath = KeyPath("**"),
        ),
    )
    SkipFirstEffect(lottieState.isPlaying) {
        isPlaying = lottieState.isPlaying
    }
    ConfirmationDialog(
        hint = buildAnnotatedString {
            val title = stringResource(R.string.audio_record_permission_message)
            val boldPartOne = stringResource(R.string.audio_record_permission_message_bold_part_1)
            val boldPartTwo = stringResource(R.string.audio_record_permission_message_bold_part_2)
            val boldPartOneIndex = title.indexOf(boldPartOne)
            val boldPartTwoIndex = title.indexOf(boldPartTwo)
            append(title)
            if (boldPartOneIndex != -1) {
                addStyle(
                    style = SpanStyle(fontWeight = FontWeight.ExtraBold),
                    start = boldPartOneIndex,
                    end = boldPartOneIndex + boldPartOne.length,
                )
            }
            if (boldPartTwoIndex != -1) {
                addStyle(
                    style = SpanStyle(fontWeight = FontWeight.ExtraBold),
                    start = boldPartTwoIndex,
                    end = boldPartTwoIndex + boldPartTwo.length,
                )
            }
        },
        cancelButton = {
            ActionButton(
                title = stringResource(id = uiR.string.action_not_now),
                onClick = onDismissRequest,
            )
        },
        confirmButton = {
            ActionButton(
                title = stringResource(id = uiR.string.settings_title),
                textColor = MaterialTheme.colorScheme.primaryContainer,
                onClick = {
                    onSettingsClick()
                    onDismissRequest()
                },
            )
        },
        icon = {
            LottieAnimation(
                modifier = Modifier
                    .size(72.dp)
                    .clickableWithScaleAnim { isPlaying = true },
                composition = composition,
                progress = { lottieState.progress },
                dynamicProperties = dynamicProperties,
            )
        },
        hazeState = hazeState,
        onDismissRequest = onDismissRequest,
    )
}

@Composable
fun CameraPermissionDialog(
    hazeState: HazeState,
    onDismissRequest: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    var isPlaying by remember { mutableStateOf(true) }
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.anim_camera_access),
    )
    val lottieState = animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isPlaying,
    )
    val dynamicProperties = rememberLottieDynamicProperties(
        LottieDynamicProperty(
            property = LottieProperty.STROKE_COLOR,
            value = MaterialTheme.colorScheme.onSurface.toArgb(),
            keyPath = KeyPath("**"),
        ),
    )
    SkipFirstEffect(lottieState.isPlaying) {
        isPlaying = lottieState.isPlaying
    }
    ConfirmationDialog(
        hint = buildAnnotatedString {
            val title = stringResource(R.string.camera_permission_message)
            val boldPartOne = stringResource(R.string.camera_permission_message_bold_part_1)
            val boldPartTwo = stringResource(R.string.camera_permission_message_bold_part_2)
            val boldPartOneIndex = title.indexOf(boldPartOne)
            val boldPartTwoIndex = title.indexOf(boldPartTwo)
            append(title)
            if (boldPartOneIndex != -1) {
                addStyle(
                    style = SpanStyle(fontWeight = FontWeight.ExtraBold),
                    start = boldPartOneIndex,
                    end = boldPartOneIndex + boldPartOne.length,
                )
            }
            if (boldPartTwoIndex != -1) {
                addStyle(
                    style = SpanStyle(fontWeight = FontWeight.ExtraBold),
                    start = boldPartTwoIndex,
                    end = boldPartTwoIndex + boldPartTwo.length,
                )
            }
        },
        cancelButton = {
            ActionButton(
                title = stringResource(id = uiR.string.action_not_now),
                onClick = onDismissRequest,
            )
        },
        confirmButton = {
            ActionButton(
                title = stringResource(id = uiR.string.settings_title),
                textColor = MaterialTheme.colorScheme.primaryContainer,
                onClick = {
                    onSettingsClick()
                    onDismissRequest()
                },
            )
        },
        icon = {
            LottieAnimation(
                modifier = Modifier
                    .size(72.dp)
                    .scale(1.5f)
                    .clickableWithScaleAnim { isPlaying = true },
                composition = composition,
                progress = { lottieState.progress },
                dynamicProperties = dynamicProperties,
            )
        },
        hazeState = hazeState,
        onDismissRequest = onDismissRequest,
    )
}

@Composable
private fun ActionButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Unspecified,
) {
    TextButton(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = textColor,
        )
    }
}

@Preview
@Composable
private fun MediaPermissionDialogPreview() {
    SerenityTheme {
        MediaPermissionDialog(
            hazeState = HazeState(),
            onDismissRequest = {},
            onSettingsClick = {},
        )
    }
}

@Preview
@Composable
private fun AudioRecordPermissionDialogPreview() {
    SerenityTheme {
        AudioRecordPermissionDialog(
            hazeState = HazeState(),
            onDismissRequest = {},
            onSettingsClick = {},
        )
    }
}

@Preview
@Composable
private fun CameraPermissionDialogPreview() {
    SerenityTheme {
        CameraPermissionDialog(
            hazeState = HazeState(),
            onDismissRequest = {},
            onSettingsClick = {},
        )
    }
}
