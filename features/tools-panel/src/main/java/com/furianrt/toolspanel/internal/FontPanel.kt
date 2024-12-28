package com.furianrt.toolspanel.internal

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imeAnimationSource
import androidx.compose.foundation.layout.imeAnimationTarget
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.furianrt.uikit.extensions.clickableNoRipple
import kotlin.math.max
import com.furianrt.uikit.R as uiR

private var cachedImeHeight = 300.dp

@Composable
internal fun FontTitleBar(
    onDoneClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickableNoRipple {},
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Font",
            style = MaterialTheme.typography.titleMedium,
        )
        IconButton(
            modifier = Modifier
                .padding(end = 16.dp)
                .align(Alignment.CenterEnd),
            onClick = onDoneClick,
        ) {
            Icon(
                painter = painterResource(id = uiR.drawable.ic_action_done),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ColumnScope.FontContent(
    visible: Boolean,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current

    val isImeVisible = WindowInsets.isImeVisible
    val imeTarget = WindowInsets.imeAnimationTarget.getBottom(LocalDensity.current)
    val imeSource = WindowInsets.imeAnimationSource.getBottom(LocalDensity.current)
    val navigationBarsHeight = WindowInsets.navigationBars.getBottom(LocalDensity.current)

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
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(contentHeight)
                .clickableNoRipple {},
        )
    } else {
        AnimatedVisibility(
            visible = visible,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(contentHeight)
                    .clickableNoRipple {},
            )
        }
    }
}

/*@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        FontPanel()
    }
}*/
