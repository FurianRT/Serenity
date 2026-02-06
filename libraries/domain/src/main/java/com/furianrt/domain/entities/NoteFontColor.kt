package com.furianrt.domain.entities

enum class NoteFontColor {
    BLACK,

    WHITE,

    GREY,

    PINK_LIGHT,
    PINK_DARK,

    YELLOW_LIGHT,
    YELLOW_DARK,

    GREEN_LIGHT,
    GREEN,
    GREEN_DARK,

    BLUE_LIGHT,
    BLUE,
    BLUE_DARK,

    PURPLE_LIGHT,
    PURPLE,
    PURPLE_DARK,

    RED,
    RED_DARK;

    companion object {
        fun fromString(value: String?) = NoteFontColor.entries.find { it.name == value }
    }
}