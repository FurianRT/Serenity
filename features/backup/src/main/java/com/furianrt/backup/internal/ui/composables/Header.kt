package com.furianrt.backup.internal.ui.composables

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieDynamicProperty
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.model.KeyPath
import com.furianrt.backup.R
import com.furianrt.backup.internal.ui.BackupUiState
import com.furianrt.uikit.components.OptionButtonWrapper
import com.furianrt.uikit.components.SkipFirstEffect
import com.furianrt.uikit.extensions.clickableWithScaleAnim
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.rememberHazeState

@Composable
internal fun Header(
    authState: BackupUiState.Content.Success.AuthState,
    hazeState: HazeState,
    onSingInClick: () -> Unit,
    onSingOutClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isPlaying by remember { mutableStateOf(true) }
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.anim_backup_profile),
    )
    val lottieState = animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isPlaying,
    )
    val dynamicProperties = rememberLottieDynamicProperties(
        LottieDynamicProperty(
            property = LottieProperty.COLOR,
            value = MaterialTheme.colorScheme.surfaceContainer.toArgb(),
            keyPath = KeyPath("**"),
        ),
    )
    SkipFirstEffect(lottieState.isPlaying) {
        isPlaying = lottieState.isPlaying
    }

    OptionButtonWrapper(
        modifier = modifier.fillMaxWidth(),
        hazeState = hazeState,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    enabled = !authState.isLoading,
                    onClick = {
                        when (authState) {
                            is BackupUiState.Content.Success.AuthState.SignedOut -> onSingInClick()
                            is BackupUiState.Content.Success.AuthState.SignedIn -> onSingOutClick()
                        }
                    },
                )
                .padding(horizontal = 12.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Crossfade(
                modifier = Modifier.size(48.dp),
                targetState = authState.isLoading,
            ) { targetState ->
                if (targetState) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(34.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.tertiaryContainer,
                            strokeWidth = 4.dp,
                        )
                    }
                } else {
                    LottieAnimation(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickableWithScaleAnim { isPlaying = true },
                        composition = composition,
                        progress = { lottieState.progress },
                        dynamicProperties = dynamicProperties,
                    )
                }
            }
            Crossfade(
                targetState = authState,
            ) { targetState ->
                when (targetState) {
                    is BackupUiState.Content.Success.AuthState.SignedOut -> SignedOutHeader()
                    is BackupUiState.Content.Success.AuthState.SignedIn -> SignedInHeader(
                        email = targetState.email,
                    )
                }
            }
        }
    }
}

@Composable
private fun SignedInHeader(
    email: String,
    modifier: Modifier = Modifier,
) {
    val wholeText = stringResource(R.string.backup_tap_to_sing_out_title)
    val underlinePart = stringResource(R.string.backup_tap_to_sing_out_underline_part)
    val underlinePartIndex = wholeText.indexOf(underlinePart)
    val title = remember {
        buildAnnotatedString {
            append(wholeText)
            if (underlinePartIndex != -1) {
                addStyle(
                    style = SpanStyle(
                        textDecoration = TextDecoration.Underline,
                        fontWeight = FontWeight.ExtraBold,
                    ),
                    start = underlinePartIndex,
                    end = underlinePartIndex + underlinePart.length,
                )
            }
        }
    }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = email,
            style = MaterialTheme.typography.bodySmall,
        )
        Text(
            modifier = Modifier.alpha(0.5f),
            text = title,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun SignedOutHeader(
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 250,
                delayMillis = 1000,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
    )
    val wholeText = stringResource(R.string.backup_tap_to_sing_in_title)
    val underlinePart = stringResource(R.string.backup_tap_to_sing_in_underline_part)
    val underlinePartIndex = wholeText.indexOf(underlinePart)
    val title = remember {
        buildAnnotatedString {
            append(wholeText)
            if (underlinePartIndex != -1) {
                addStyle(
                    style = SpanStyle(
                        textDecoration = TextDecoration.Underline,
                        fontWeight = FontWeight.ExtraBold,
                    ),
                    start = underlinePartIndex,
                    end = underlinePartIndex + underlinePart.length,
                )
            }
        }
    }
    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
@PreviewWithBackground
private fun SignedInPreview() {
    SerenityTheme {
        Header(
            authState = BackupUiState.Content.Success.AuthState.SignedIn(
                email = "testtest@gmail.com",
                isLoading = false,
            ),
            hazeState = rememberHazeState(),
            onSingInClick = {},
            onSingOutClick = {},
        )
    }
}

@Composable
@PreviewWithBackground
private fun SignedOutPreview() {
    SerenityTheme {
        Header(
            authState = BackupUiState.Content.Success.AuthState.SignedOut(
                isLoading = false,
            ),
            hazeState = rememberHazeState(),
            onSingInClick = {},
            onSingOutClick = {},
        )
    }
}

@Composable
@PreviewWithBackground
private fun LoadingPreview() {
    SerenityTheme {
        Header(
            authState = BackupUiState.Content.Success.AuthState.SignedIn(
                email = "testtest@gmail.com",
                isLoading = true,
            ),
            hazeState = rememberHazeState(),
            onSingInClick = {},
            onSingOutClick = {},
        )
    }
}
