package com.furianrt.domain.entities

enum class NoteFontColor {
    WHITE,
    ORANGE,
    GREEN,
    BLUE_LIGHT,
    BLUE,
    BLUE_DARK,
    PURPLE,
    PURPLE_DARK;

    companion object {
        fun fromString(value: String?) = NoteFontColor.entries.find { it.name == value } ?: WHITE
    }
}