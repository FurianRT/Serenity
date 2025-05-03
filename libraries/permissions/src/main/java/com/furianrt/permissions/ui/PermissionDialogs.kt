package com.furianrt.permissions.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.furianrt.permissions.R
import com.furianrt.uikit.components.SkipFirstEffect
import com.furianrt.uikit.extensions.clickableWithScaleAnim
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.LocalAuth
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeChild
import kotlinx.coroutines.launch
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
    )
    SkipFirstEffect(lottieState.isPlaying) {
        isPlaying = lottieState.isPlaying
    }

    PermissionDialog(
        title = buildAnnotatedString {
            val title = stringResource(R.string.media_permission_message)
            val boldPartOne = stringResource(R.string.media_permission_message_bold_part_1)
            val boldPartTwo = stringResource(R.string.media_permission_message_bold_part_2)
            val boldPartOneIndex = title.indexOf(boldPartOne)
            val boldPartTwoIndex = title.indexOf(boldPartTwo)
            append(title)
            addStyle(
                style = SpanStyle(fontWeight = FontWeight.ExtraBold),
                start = boldPartOneIndex,
                end = boldPartOneIndex + boldPartOne.length,
            )
            addStyle(
                style = SpanStyle(fontWeight = FontWeight.ExtraBold),
                start = boldPartTwoIndex,
                end = boldPartTwoIndex + boldPartTwo.length,
            )
        },
        logo = {
            LottieAnimation(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .size(80.dp)
                    .align(Alignment.CenterHorizontally)
                    .clickableWithScaleAnim { isPlaying = true },
                composition = composition,
                progress = { lottieState.progress },
            )
        },
        hazeState = hazeState,
        onDismissRequest = onDismissRequest,
        onSettingsClick = onSettingsClick,
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
    SkipFirstEffect(lottieState.isPlaying) {
        isPlaying = lottieState.isPlaying
    }

    PermissionDialog(
        title = buildAnnotatedString {
            val title = stringResource(R.string.audio_record_permission_message)
            val boldPartOne = stringResource(R.string.audio_record_permission_message_bold_part_1)
            val boldPartTwo = stringResource(R.string.audio_record_permission_message_bold_part_2)
            val boldPartOneIndex = title.indexOf(boldPartOne)
            val boldPartTwoIndex = title.indexOf(boldPartTwo)
            append(title)
            addStyle(
                style = SpanStyle(fontWeight = FontWeight.ExtraBold),
                start = boldPartOneIndex,
                end = boldPartOneIndex + boldPartOne.length,
            )
            addStyle(
                style = SpanStyle(fontWeight = FontWeight.ExtraBold),
                start = boldPartTwoIndex,
                end = boldPartTwoIndex + boldPartTwo.length,
            )
        },
        logo = {
            LottieAnimation(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .size(72.dp)
                    .align(Alignment.CenterHorizontally)
                    .clickableWithScaleAnim { isPlaying = true },
                composition = composition,
                progress = { lottieState.progress },
            )
        },
        hazeState = hazeState,
        onDismissRequest = onDismissRequest,
        onSettingsClick = onSettingsClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PermissionDialog(
    title: AnnotatedString,
    logo: @Composable ColumnScope.() -> Unit,
    hazeState: HazeState,
    onDismissRequest: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val auth = LocalAuth.current

    LifecycleStartEffect(Unit) {
        scope.launch {
            if (!auth.isAuthorized()) {
                onDismissRequest()
            }
        }
        onStopOrDispose {}
    }

    BasicAlertDialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .clip(RoundedCornerShape(16.dp))
                .hazeChild(
                    state = hazeState,
                    style = HazeDefaults.style(
                        backgroundColor = MaterialTheme.colorScheme.surface,
                        blurRadius = 20.dp,
                    ),
                )
                .background(MaterialTheme.colorScheme.surfaceTint)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.End,
        ) {
            logo()
            Title(title = title)
            Row(
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ActionButton(
                    title = stringResource(id = uiR.string.action_not_now),
                    onClick = onDismissRequest,
                )
                ActionButton(
                    title = stringResource(id = uiR.string.settings_title),
                    onClick = {
                        onSettingsClick()
                        onDismissRequest()
                    },
                )
            }
        }
    }
}

@Composable
private fun Header(
    icon: Painter,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            modifier = Modifier
                .size(64.dp)
                .clickableWithScaleAnim {},
            painter = icon,
            tint = Color.Unspecified,
            contentDescription = null,
        )
    }
}

@Composable
private fun Title(
    title: AnnotatedString,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = title,
        style = MaterialTheme.typography.bodyMedium,
    )
}

@Composable
private fun ActionButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TextButton(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
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
