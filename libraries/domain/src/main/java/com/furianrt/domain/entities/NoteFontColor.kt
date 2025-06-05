package com.furianrt.domain.entities

enum class NoteFontColor {
    WHITE,

    GREY_LIGHT,
    GREY,
    GREY_DARK,

    PINK_LIGHT,
    PINK,
    PINK_DARK,

    YELLOW_LIGHT,
    YELLOW,
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

    RED_LIGHT,
    RED,
    RED_DARK;

    companion object {
        fun fromString(value: String?) = NoteFontColor.entries.find { it.name == value }
    }
}