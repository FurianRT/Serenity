package com.furianrt.billing.api

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.furianrt.billing.internal.ui.BillingScreen
import com.furianrt.uikit.anim.defaultPopEnterTransition
import kotlinx.serialization.Serializable

@Serializable
data object BillingRoute

fun NavController.navigateToBilling(
    route: BillingRoute = BillingRoute,
    navOptions: NavOptions = NavOptions.Builder().setLaunchSingleTop(true).build(),
) = navigate(route = route, navOptions = navOptions)

fun NavGraphBuilder.billingScreen(
    onCloseRequest: () -> Unit,
) {
    composable<BillingRoute>(
        enterTransition = {
            fadeIn(
                tween(
                    durationMillis = 400,
                    easing = LinearEasing,
                )
            ) + slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Up,
                initialOffset = { (it * 0.5f).toInt() },
                animationSpec = tween(
                    durationMillis = 650,
                    easing = FastOutSlowInEasing,
                ),
            )
        },
        popEnterTransition = { defaultPopEnterTransition() },
    ) {
        BillingScreen(
            onCloseRequest = onCloseRequest,
        )
    }
}