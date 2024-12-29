package com.furianrt.domain.entities

enum class ThemeColor {
    GREEN,
    BLACK,
    PURPLE,
    PURPLE_DARK;

    companion object {
        fun fromString(value: String?) = ThemeColor.entries.find { it.name == value } ?: GREEN
    }
}