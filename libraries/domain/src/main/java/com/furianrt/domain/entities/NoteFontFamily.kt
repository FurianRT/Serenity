package com.furianrt.domain.entities

enum class NoteFontFamily {
    QUICK_SAND,
    TEST_FONT_1,
    TEST_FONT_2,
    TEST_FONT_3,
    TEST_FONT_4,
    TEST_FONT_5,
    TEST_FONT_6,
    TEST_FONT_7,
    TEST_FONT_8,
    TEST_FONT_9,
    TEST_FONT_10,
    TEST_FONT_11,
    TEST_FONT_12,
    TEST_FONT_13,
    TEST_FONT_14,
    TEST_FONT_15,
    TEST_FONT_16,
    TEST_FONT_17,
    TEST_FONT_18;

    companion object {
        fun fromString(value: String?): NoteFontFamily {
            return NoteFontFamily.entries.find { it.name == value } ?: QUICK_SAND
        }
    }
}