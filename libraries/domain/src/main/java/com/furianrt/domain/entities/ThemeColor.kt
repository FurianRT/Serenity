package com.furianrt.domain.entities

enum class ThemeColor {
    GREEN,
    BLACK,
    PURPLE,
    PURPLE_DARK;

    companion object {
        fun fromString(value: String?): ThemeColor? = when(value) {
            GREEN.name -> GREEN
            BLACK.name -> BLACK
            PURPLE.name -> PURPLE
            PURPLE_DARK.name -> PURPLE_DARK
            else -> null
        }
    }
}