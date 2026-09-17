package com.furianrt.billing.api

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.furianrt.billing.internal.ui.BillingScreen
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
    composable<BillingRoute> {
        BillingScreen(
            onCloseRequest = onCloseRequest,
        )
    }
}