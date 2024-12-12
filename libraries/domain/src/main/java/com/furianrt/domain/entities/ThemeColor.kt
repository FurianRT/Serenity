package com.furianrt.domain.entities

enum class ThemeColor {
    GREEN,
    BLACK,
    BLUE;

    companion object {
        fun fromString(value: String?): ThemeColor? = when(value) {
            GREEN.name -> GREEN
            BLACK.name -> BLACK
            BLUE.name -> BLUE
            else -> null
        }
    }
}