package com.furianrt.domain.entities

enum class NoteFontColor {
    BLACK,

    WHITE,

    GREY,

    BLUE,
    BLUE_DARK,
    SKY,
    INDIGO,

    PURPLE_LIGHT,
    PURPLE,
    PURPLE_DARK,
    LAVENDER,

    PINK_LIGHT,
    PINK_DARK,

    STEEL,
    NAVI,

    GREEN_LIGHT,
    GREEN,
    GREEN_DARK,
    BLUE_LIGHT,

    CORAL,
    PEACH,
    CORAL_DARK,
    RED,
    RED_DARK,

    MINT,
    MINT_DARK,
    TEAL,

    YELLOW_LIGHT,
    YELLOW_DARK,
    ORANGE,
    AMBER,
    BROWN,
    CHOCOLATE,

    SAGE,
    OLIVE,
    KHAKI,
    SAND;

    companion object {
        fun fromString(value: String?) = NoteFontColor.entries.find { it.name == value }
    }
}