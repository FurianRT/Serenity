package com.furianrt.billing.internal.ui.entities

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.furianrt.billing.R
import com.furianrt.uikit.R as uiR

internal enum class BenefitItem {
    CUSTOM_STICKERS,
    CUSTOM_BACKGROUNDS,
    EXPORT_PDF,
    MORE_FEATURES;

    @Composable
    fun getTitle(): String = when (this) {
        CUSTOM_STICKERS -> stringResource(R.string.billing_benefit_custom_stickers_title)
        CUSTOM_BACKGROUNDS -> stringResource(R.string.billing_benefit_custom_backgrounds_title)
        EXPORT_PDF -> stringResource(R.string.billing_benefit_export_pdf_title)
        MORE_FEATURES -> stringResource(R.string.billing_benefit_future_features_title)
    }

    @Composable
    fun getSubtitle(): String = when (this) {
        CUSTOM_STICKERS -> stringResource(R.string.billing_benefit_custom_stickers_body)
        CUSTOM_BACKGROUNDS -> stringResource(R.string.billing_benefit_custom_backgrounds_body)
        EXPORT_PDF -> stringResource(R.string.billing_benefit_export_pdf_body)
        MORE_FEATURES -> stringResource(R.string.billing_benefit_future_features_body)
    }

    @Composable
    fun getIcon(): Painter = when (this) {
        CUSTOM_STICKERS -> painterResource(uiR.drawable.ic_stickers)
        CUSTOM_BACKGROUNDS -> painterResource(uiR.drawable.ic_theme)
        EXPORT_PDF -> painterResource(uiR.drawable.ic_export_file)
        MORE_FEATURES -> painterResource(uiR.drawable.ic_leaf)
    }
}