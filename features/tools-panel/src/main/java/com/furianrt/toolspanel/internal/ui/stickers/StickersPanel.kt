package com.furianrt.toolspanel.internal.ui.stickers

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imeAnimationSource
import androidx.compose.foundation.layout.imeAnimationTarget
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import com.furianrt.toolspanel.internal.ui.font.cachedImeHeight
import com.furianrt.uikit.extensions.clickableNoRipple
import kotlin.math.max

@Composable
internal fun StickersTitleBar(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickableNoRipple {},
        contentAlignment = Alignment.Center,
    ) {

    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun StickersContent(
    visible: Boolean,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val isImeVisible = WindowInsets.isImeVisible
    val imeTarget = WindowInsets.imeAnimationTarget.getBottom(density)
    val imeSource = WindowInsets.imeAnimationSource.getBottom(density)
    val navigationBarsHeight = WindowInsets.navigationBars.getBottom(density)

    var imeHeight by remember { mutableStateOf(cachedImeHeight) }
    val contentHeight = imeHeight - density.run { navigationBarsHeight.toDp() }

    LaunchedEffect(imeTarget, imeSource) {
        val imeMaxHeight = max(imeTarget, imeSource)
        if (imeMaxHeight > 0) {
            imeHeight = density.run { imeMaxHeight.toDp() }
            cachedImeHeight = imeHeight
        }
    }

    if (isImeVisible && visible) {
        Content(
            modifier = modifier.height(contentHeight),
        )
    } else {
        AnimatedVisibility(
            visible = visible,
            enter = expandVertically(expandFrom = Alignment.Top),
            exit = shrinkVertically(shrinkTowards = Alignment.Top),
        ) {
            Content(
                modifier = modifier.height(contentHeight),
            )
        }
    }
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickableNoRipple {},
        contentAlignment = Alignment.Center,
    ) {

    }
}
