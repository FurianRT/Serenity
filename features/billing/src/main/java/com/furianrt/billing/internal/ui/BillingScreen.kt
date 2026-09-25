package com.furianrt.billing.internal.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.furianrt.billing.internal.ui.components.Benefit
import com.furianrt.billing.internal.ui.components.ButtonClose
import com.furianrt.billing.internal.ui.components.ButtonSubscribe
import com.furianrt.billing.internal.ui.components.Logo
import com.furianrt.billing.internal.ui.components.SubscriptionOption
import com.furianrt.billing.internal.ui.components.SubscriptionOptionSkeleton
import com.furianrt.billing.internal.ui.components.TermsWarning
import com.furianrt.common.ErrorTracker
import com.furianrt.uikit.components.AppBackgroundWithStars
import com.furianrt.uikit.extensions.fadingBottomEdge
import com.furianrt.uikit.extensions.pxToDp
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.HazeColorEffect
import dev.chrisbanes.haze.blur.hazeBlur
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.collectLatest
import com.furianrt.uikit.R as uiR

private const val KEY_LOGO = "logo"
private const val KEY_PLAN_SKELETON = "plan_skeleton"
private const val KEY_TERMS = "terms"
private const val SKELETONS_COUNT = 3

@Composable
internal fun BillingScreen(
    onCloseRequest: () -> Unit,
) {
    val viewModel: BillingViewModel = hiltViewModel()
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val hapticFeedback = LocalHapticFeedback.current

    val onCloseRequestState by rememberUpdatedState(onCloseRequest)

    val appNotFoundMessage = stringResource(uiR.string.app_not_found_error)
    val generalErrorMessage = stringResource(uiR.string.general_error)

    LaunchedEffect(Unit) {
        viewModel.effect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collectLatest { effect ->
                when (effect) {
                    is BillingEffect.CloseScreen -> onCloseRequestState()
                    is BillingEffect.OpenLink -> try {
                        val intent = Intent(Intent.ACTION_VIEW, effect.url.toUri())
                        context.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        ErrorTracker.printStackTrace(e)
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.Reject)
                        Toast.makeText(context, appNotFoundMessage, Toast.LENGTH_SHORT).show()
                    }

                    is BillingEffect.ShowGeneralErrorMessage -> {
                        Toast.makeText(context, generalErrorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    BackHandler(
        enabled = uiState.showButtonProgress,
        onBack = {},
    )

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

    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Box(modifier = modifier.fillMaxSize()) {
        AppBackgroundWithStars(
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(hazeState, zIndex = 0f),
            theme = uiState.theme,
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(hazeState, zIndex = 1f),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = statusBarPadding + 32.dp,
                bottom = subscribeButtonHeight.pxToDp() + navBarPadding + 40.dp,
            ),
        ) {
            item(
                key = KEY_LOGO,
                contentType = KEY_LOGO,
            ) {
                Logo(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                )
            }

            when (uiState.content) {
                is BillingState.Content.Loading -> repeat(SKELETONS_COUNT) { index ->
                    item(
                        key = KEY_PLAN_SKELETON + index,
                        contentType = KEY_PLAN_SKELETON,
                    ) {
                        SubscriptionOptionSkeleton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    bottom = if (index == SKELETONS_COUNT - 1) 24.dp else 10.dp,
                                ),
                            hazeState = hazeState,
                            isSelected = index == 1,
                        )
                    }
                }

                is BillingState.Content.Success -> itemsIndexed(
                    items = uiState.content.plans,
                    key = { _, plan -> plan.id },
                    contentType = { _, plan -> plan::class.simpleName },
                ) { index, plan ->
                    SubscriptionOption(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                bottom = if (index == uiState.content.plans.lastIndex) {
                                    24.dp
                                } else {
                                    10.dp
                                }
                            ),
                        plan = plan,
                        isSelected = plan.id == uiState.content.selectedPlanId,
                        hazeState = hazeState,
                        onSelected = { onEvent(BillingEvent.OnPlanSelected(plan.id)) },
                    )
                }
            }

            itemsIndexed(
                items = uiState.benefits,
                key = { _, benefit -> benefit.name },
                contentType = { _, benefit -> benefit::class.simpleName },
            ) { index, benefit ->
                Benefit(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = if (index != uiState.benefits.lastIndex) 10.dp else 0.dp),
                    benefit = benefit,
                    hazeState = hazeState,
                )
            }

            item(
                key = KEY_TERMS,
                contentType = KEY_TERMS,
            ) {
                TermsWarning(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 32.dp),
                    onTermsClick = { onEvent(BillingEvent.OnTermsClick) },
                    onPrivacyPolicyClick = { onEvent(BillingEvent.OnPrivacyPolicyClick) },
                )
            }
        }
        ButtonClose(
            modifier = Modifier
                .padding(start = 12.dp, top = 12.dp)
                .statusBarsPadding()
                .align(Alignment.TopStart),
            hazeState = hazeState,
            onClick = { onEvent(BillingEvent.OnButtonCloseClick) },
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .fadingBottomEdge()
                .hazeBlur(
                    input = HazeInput.Sources(hazeState),
                    style = HazeBlurStyle {
                        blurRadius(4.dp)
                        colorEffects(
                            listOf(HazeColorEffect.tint(Color.Transparent))
                        )
                    },
                )
                .padding(4.dp)
                .windowInsetsTopHeight(WindowInsets.statusBars),
        )
        ButtonSubscribe(
            modifier = Modifier
                .padding(start = 24.dp, end = 24.dp, bottom = 8.dp)
                .navigationBarsPadding()
                .fillMaxWidth()
                .onSizeChanged { subscribeButtonHeight = it.height }
                .align(Alignment.BottomCenter),
            text = when (uiState.content) {
                is BillingState.Content.Loading -> ""
                is BillingState.Content.Success -> uiState.content.buttonTitle
            },
            showProgress = uiState.showButtonProgress,
            onClick = { onEvent(BillingEvent.OnSubscribeClick) },
        )
    }
}
