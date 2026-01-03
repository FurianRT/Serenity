package com.furianrt.onboarding.internal.ui.theme.model

internal sealed class ThemeTab(
    open val title: String,
) {
    data class Dark(
        override val title: String,
    ) : ThemeTab(title)

    data class Light(
        override val title: String,
    ) : ThemeTab(title)
}