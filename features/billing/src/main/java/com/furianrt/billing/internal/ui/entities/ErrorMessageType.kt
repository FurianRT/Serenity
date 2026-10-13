package com.furianrt.billing.internal.ui.entities

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.furianrt.billing.R
import com.furianrt.uikit.R as uiR

internal enum class ErrorMessageType {
    GENERAL,
    BILLING_UNAVAILABLE;

    @Composable
    fun getTitle(): String = when (this) {
        GENERAL -> stringResource(uiR.string.general_error)
        BILLING_UNAVAILABLE -> stringResource(R.string.billing_unavailable_title)
    }

    @Composable
    fun getSubtitle(): String? = when (this) {
        GENERAL -> null
        BILLING_UNAVAILABLE -> stringResource(R.string.billing_unavailable_message)
    }
}