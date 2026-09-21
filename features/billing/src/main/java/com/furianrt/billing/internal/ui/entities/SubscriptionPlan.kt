package com.furianrt.billing.internal.ui.entities

import androidx.compose.ui.text.AnnotatedString

data class SubscriptionPlan(
    val id: String,
    val title: String,
    val hasTrial: Boolean,
    val mainPrice: String,
    val secondPrice: AnnotatedString?,
    val discount: String?,
)
