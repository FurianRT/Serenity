package com.furianrt.mediaselector.internal.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground

// TODO Сделать индикатор
@Composable
internal fun LoadingContent(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize())
}

@Composable
@PreviewWithBackground
private fun Preivew() {
    SerenityTheme {
        LoadingContent(

        )
    }
}
