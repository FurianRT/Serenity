package com.furianrt.uikit.extensions

import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import kotlin.math.max

private const val MAX_VISIBLE_THRESHOLD = 100f

fun LazyListLayoutInfo.firstMostVisibleItemInfo(
    topOffset: Int = 0,
): LazyListItemInfo = visibleItemsInfo.maxBy { visibilityPercent(it, topOffset) }

fun LazyListLayoutInfo.visibleItemsInfo(
    itemVisiblePercentThreshold: Float,
    topOffset: Int = 0,
): List<LazyListItemInfo> = visibleItemsInfo
    .filter { visibilityPercent(it, topOffset) >= itemVisiblePercentThreshold }

fun LazyListLayoutInfo.visibilityPercent(info: LazyListItemInfo, topOffset: Int): Float {
    val cutTop = max(0, viewportStartOffset - info.offset + topOffset)
    val cutBottom = max(0, info.offset + topOffset + info.size - viewportEndOffset)
    return max(0f, MAX_VISIBLE_THRESHOLD - (cutTop + cutBottom) * MAX_VISIBLE_THRESHOLD / info.size)
}