package com.furianrt.common

import kotlin.random.Random

fun Random.nextFloat(
    from: Float,
    until: Float,
    multiplier: Int = 100_000,
): Float {
    require(multiplier > 0) { "Multiplier must be greater than 0" }

    val fromInt = (from * multiplier).toInt()
    val untilInt = (until * multiplier).toInt()

    require(untilInt > fromInt) {
        "The range [$from, $until) is too small for multiplier $multiplier (mapped to [$fromInt, $untilInt))"
    }

    return nextInt(fromInt, untilInt) / multiplier.toFloat()
}
