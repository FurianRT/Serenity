package com.furianrt.lock.internal.ui.entities

internal enum class PinCount(val value: Int) {
    ZERO(0), ONE(1), TWO(2), THREE(3), FOUR(4);

    companion object {
        fun fromPin(pin: String) = when (pin.length) {
            ZERO.value -> ZERO
            ONE.value -> ONE
            TWO.value -> TWO
            THREE.value -> THREE
            else -> FOUR
        }
    }
}