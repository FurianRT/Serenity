package com.furianrt.domain.entities

enum class AppLocale(val tag: String, val text: String) {
    ENGLISH(tag ="en", text = "English"),
    RUSSIAN(tag ="ru", text = "Русский"),
    HINDI(tag ="hi", text = "हिंदी");

    companion object {
        fun fromTag(tag: String?): AppLocale = when (tag) {
            ENGLISH.tag -> ENGLISH
            RUSSIAN.tag -> RUSSIAN
            HINDI.tag -> HINDI
            else -> ENGLISH
        }
    }
}