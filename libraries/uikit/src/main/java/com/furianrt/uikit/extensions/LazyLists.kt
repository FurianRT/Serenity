package com.furianrt.uikit.extensions

import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import kotlin.math.max

private const val MAX_VISIBLE_THRESHOLD = 100f

fun LazyListState.visibleItemsInfo(itemVisiblePercentThreshold: Float) = layoutInfo
    .visibleItemsInfo
    .filter { visibilityPercent(it) >= itemVisiblePercentThreshold }

fun LazyListState.visibilityPercent(info: LazyListItemInfo): Float {
    val cutTop = max(0, layoutInfo.viewportStartOffset - info.offset)
    val cutBottom = max(0, info.offset + info.size - layoutInfo.viewportEndOffset)
    return max(0f, MAX_VISIBLE_THRESHOLD - (cutTop + cutBottom) * MAX_VISIBLE_THRESHOLD / info.size)
}