package com.furianrt.domain.entities

enum class AppLocale(val tag: String, val text: String) {
    ENGLISH(tag ="en", text = "English"),
    RUSSIAN(tag ="ru", text = "Русский"),
    HINDI(tag ="hi", text = "हिंदी"),
    INDONESIAN(tag ="id", text = "Indonesia"),
    SPAIN(tag ="es", text = "Español");

    companion object {
        fun fromTag(tag: String?): AppLocale = when (tag) {
            ENGLISH.tag -> ENGLISH
            RUSSIAN.tag -> RUSSIAN
            HINDI.tag -> HINDI
            INDONESIAN.tag -> INDONESIAN
            SPAIN.tag -> SPAIN
            else -> ENGLISH
        }
    }
}