package com.furianrt.toolspanel.api.entities

import androidx.annotation.DrawableRes

data class Sticker(
    val id: String,
    val icon: Icon,
    val ratio: Float? = null,
) {
    val iconData: Any
        get() = when (icon) {
            is Icon.Res -> icon.res
            is Icon.Uri -> icon.uri
        }

    sealed interface Icon {
        data class Res(@param:DrawableRes val res: Int) : Icon
        data class Uri(val uri: android.net.Uri) : Icon
    }
}