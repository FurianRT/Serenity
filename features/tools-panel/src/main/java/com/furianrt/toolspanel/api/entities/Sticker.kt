package com.furianrt.toolspanel.api.entities

import androidx.annotation.DrawableRes

data class Sticker(
    val id: String,
    @DrawableRes val icon: Int,
)