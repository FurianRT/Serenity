package com.furianrt.mediaselector.internal.ui.selector

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground

private const val SKELETON_COUNT = 24

@Composable
internal fun LoadingContent(
    modifier: Modifier = Modifier,
) {
    val listSpanCount = 3
    LazyVerticalGrid(
        modifier = modifier.fillMaxSize(),
        userScrollEnabled = false,
        columns = GridCells.Fixed(listSpanCount),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(horizontal = 4.dp),
    ) {
        items(count = SKELETON_COUNT) { index ->
            SkeletonItem(
                modifier = Modifier.background(
                    color = MaterialTheme.colorScheme.tertiary,
                    shape = RoundedCornerShape(
                        topStart = if (index == 0) 8.dp else 0.dp,
                        topEnd = if (index == listSpanCount - 1) 8.dp else 0.dp,
                    ),
                ),
            )
        }
    }
}

@Composable
private fun SkeletonItem(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(2.dp)),
    )
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        LoadingContent(

        )
    }
}
