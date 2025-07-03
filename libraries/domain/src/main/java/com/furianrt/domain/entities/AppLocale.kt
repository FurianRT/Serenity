package com.furianrt.domain.entities

enum class AppLocale(val tag: String, val text: String) {
    ENGLISH(tag ="en", text = "English"),
    RUSSIAN(tag ="ru", text = "Русский"),
    HINDI(tag ="hi", text = "हिंदी"),
    INDONESIAN(tag ="id", text = "Indonesia"),
    SPANISH(tag ="es", text = "Español"),
    PORTUGUESE(tag ="pt", text = "Português");

    companion object {
        fun fromTag(tag: String?): AppLocale = when (tag) {
            ENGLISH.tag -> ENGLISH
            RUSSIAN.tag -> RUSSIAN
            HINDI.tag -> HINDI
            INDONESIAN.tag -> INDONESIAN
            SPANISH.tag -> SPANISH
            PORTUGUESE.tag -> PORTUGUESE
            else -> ENGLISH
        }
    }
}