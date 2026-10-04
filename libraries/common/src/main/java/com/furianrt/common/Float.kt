package com.furianrt.common

import kotlin.math.pow

fun Float.truncateToMaxDigits(maxDigits: Int): Float {
    if (maxDigits <= 0) return this.toInt().toFloat()
    val factor = 10f.pow(maxDigits)
    return (this * factor).toInt() / factor
}