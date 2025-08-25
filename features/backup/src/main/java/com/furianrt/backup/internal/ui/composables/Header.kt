package com.furianrt.backup.internal.ui.composables

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
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
import com.furianrt.uikit.components.SkipFirstEffect
import com.furianrt.uikit.extensions.clickableWithScaleAnim
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground

@Composable
internal fun Header(
    authState: BackupUiState.Success.AuthState,
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

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Crossfade(
            modifier = Modifier.size(64.dp),
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
                is BackupUiState.Success.AuthState.SignedOut -> SignedOutHeader(
                    isEnabled = !targetState.isLoading,
                    onSingInClick = onSingInClick,
                )

                is BackupUiState.Success.AuthState.SignedIn -> SignedInHeader(
                    email = targetState.email,
                    isEnabled = !targetState.isLoading,
                    onSingOutClick = onSingOutClick,
                )
            }
        }
    }
}

@Composable
private fun SignedInHeader(
    email: String,
    isEnabled: Boolean,
    onSingOutClick: () -> Unit,
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
        modifier = modifier
            .alpha(0.5f)
            .clip(RoundedCornerShape(16.dp))
            .clickable(enabled = isEnabled, onClick = onSingOutClick)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = email,
            style = MaterialTheme.typography.bodySmall,
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun SignedOutHeader(
    isEnabled: Boolean,
    onSingInClick: () -> Unit,
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
    val alpha by animateFloatAsState(targetValue = if (isEnabled) 1f else 0.5f)
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
    Text(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(enabled = isEnabled, onClick = onSingInClick)
            .padding(horizontal = 8.dp, vertical = 16.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            },
        text = title,
        style = MaterialTheme.typography.bodyMedium,
    )
}

@Composable
@PreviewWithBackground
private fun SignedInPreview() {
    SerenityTheme {
        Header(
            authState = BackupUiState.Success.AuthState.SignedIn(
                email = "testtest@gmail.com",
                isLoading = false,
            ),
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
            authState = BackupUiState.Success.AuthState.SignedOut(
                isLoading = false,
            ),
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
            authState = BackupUiState.Success.AuthState.SignedIn(
                email = "testtest@gmail.com",
                isLoading = true,
            ),
            onSingInClick = {},
            onSingOutClick = {},
        )
    }
}
