package com.furianrt.billing.internal.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.furianrt.billing.R
import com.furianrt.billing.internal.ui.components.Benefit
import com.furianrt.billing.internal.ui.components.ButtonSubscribe
import com.furianrt.billing.internal.ui.components.Logo
import com.furianrt.billing.internal.ui.components.ShootingStarsLayout
import com.furianrt.billing.internal.ui.components.SubscriptionOption
import com.furianrt.billing.internal.ui.components.TermsWarning
import com.furianrt.uikit.components.AppBackground
import com.furianrt.uikit.components.OptionButtonWrapper
import com.furianrt.uikit.components.StarsLayout
import com.furianrt.uikit.entities.UiThemeColor
import com.furianrt.uikit.extensions.pxToDp
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.collectLatest
import com.furianrt.uikit.R as uiR

@Composable
internal fun BillingScreen(
    onCloseRequest: () -> Unit,
) {
    val viewModel: BillingViewModel = hiltViewModel()
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val onCloseRequestState by rememberUpdatedState(onCloseRequest)

    LaunchedEffect(Unit) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->
                when (effect) {
                    BillingEffect.CloseScreen -> onCloseRequestState()
                }
            }
    }

    Content(
        uiState = uiState,
        onEvent = viewModel::onEvent,
    )
}

@Composable
private fun Content(
    uiState: BillingState,
    onEvent: (event: BillingEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hazeState = rememberHazeState()
    var subscribeButtonHeight by remember { mutableIntStateOf(0) }

    Box(
        modifier = modifier
            .fillMaxSize(),
    ) {
        Background(
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(hazeState),
            theme = uiState.theme,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 40.dp,
                    bottom = subscribeButtonHeight.pxToDp() + 32.dp,
                )
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Logo()
            Spacer(Modifier.size(24.dp))
            Text(
                text = stringResource(uiR.string.title_serenity_plus),
                style = MaterialTheme.typography.titleLarge,
                fontSize = 22.sp,
            )
            Spacer(Modifier.size(14.dp))
            Text(
                modifier = Modifier.alpha(0.7f),
                text = stringResource(R.string.billing_subscriptions_description),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.size(24.dp))
            uiState.plans.forEachIndexed { index, plan ->
                SubscriptionOption(
                    modifier = Modifier.fillMaxWidth(),
                    plan = plan,
                    hazeState = hazeState,
                    onSelected = {},
                )
                if (index != uiState.plans.lastIndex) {
                    Spacer(Modifier.size(10.dp))
                }
            }
            Spacer(Modifier.size(24.dp))
            OptionButtonWrapper(
                hazeState = hazeState,
                borderColor = Color.Transparent,
            ) {
                Spacer(Modifier.size(20.dp))
                repeat(4) { index ->
                    Benefit(
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .fillMaxWidth()
                    )
                    if (index != 3) {
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 48.dp, top = 16.dp, bottom = 16.dp),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                        )
                    }
                }
                Spacer(Modifier.size(20.dp))
            }
            Spacer(Modifier.size(32.dp))
            TermsWarning(
                modifier = Modifier.padding(horizontal = 16.dp),
                onTermsClick = {},
                onPrivacyPolicyClick = {},
            )
        }
        ButtonSubscribe(
            modifier = Modifier
                .padding(start = 24.dp, end = 24.dp, bottom = 8.dp)
                .navigationBarsPadding()
                .fillMaxWidth()
                .onSizeChanged { subscribeButtonHeight = it.height }
                .align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun Background(
    theme: UiThemeColor,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition()
    val rotationAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 200000,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
    )

    Box(
        modifier = modifier,
    ) {
        AppBackground(
            theme = theme,
        )
        StarsLayout(
            modifier = Modifier
                .aspectRatio(1f)
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .graphicsLayer { rotationZ = rotationAnim },
            starCount = 75,
            starRotation = { rotationAnim },
        )
        ShootingStarsLayout(
            modifier = Modifier
                .aspectRatio(1.2f)
                .fillMaxWidth()
                .align(Alignment.TopCenter),
        )
        StarsLayout(
            modifier = Modifier.fillMaxSize(),
            starCount = 75,
        )
    }
}
