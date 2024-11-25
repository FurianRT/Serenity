package com.furianrt.uikit.extensions

import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import kotlin.math.max

private const val MAX_VISIBLE_THRESHOLD = 100f

fun LazyListLayoutInfo.firstMostVisibleItemInfo() = visibleItemsInfo.maxBy { visibilityPercent(it) }

fun LazyListLayoutInfo.visibleItemsInfo(itemVisiblePercentThreshold: Float) = visibleItemsInfo
    .filter { visibilityPercent(it) >= itemVisiblePercentThreshold }

fun LazyListLayoutInfo.visibilityPercent(info: LazyListItemInfo): Float {
    val cutTop = max(0, viewportStartOffset - info.offset)
    val cutBottom = max(0, info.offset + info.size - viewportEndOffset)
    return max(0f, MAX_VISIBLE_THRESHOLD - (cutTop + cutBottom) * MAX_VISIBLE_THRESHOLD / info.size)
}