package com.furianrt.domain.entities

enum class AppLocale(val tag: String, val text: String) {
    ENGLISH(tag ="en", text = "English"),
    RUSSIAN(tag ="ru", text = "Русский"),
    HINDI(tag ="hi", text = "हिंदी"),
    INDONESIAN(tag ="id", text = "Indonesia"),
    SPANISH(tag ="es", text = "Español"),
    PORTUGUESE(tag ="pt", text = "Português"),
    GERMAN(tag ="de", text = "Deutsch"),
    TURKISH(tag ="tr", text = "Türkçe"),
    VIETNAMESE(tag ="vi", text = "Tiếng Việt"),
    FILIPINO(tag ="fil", text = "Filipino"),
    JAPANESE(tag ="ja", text = "日本語"),
    KOREAN(tag ="ko", text = "한국어"),
    FRENCH(tag ="fr", text = "Français"),
    ITALIAN(tag ="it", text = "Italiano"),
    UKRAINIAN(tag ="uk", text = "Українська"),
    POLISH(tag ="pl", text = "Polski"),
    NEDERLANDS(tag ="nl", text = "Nederlands"),
    ROMANIAN(tag ="ro", text = "Română");

    companion object {
        fun fromTag(tag: String?): AppLocale = when (tag) {
            ENGLISH.tag -> ENGLISH
            RUSSIAN.tag -> RUSSIAN
            HINDI.tag -> HINDI
            INDONESIAN.tag -> INDONESIAN
            SPANISH.tag -> SPANISH
            PORTUGUESE.tag -> PORTUGUESE
            GERMAN.tag -> GERMAN
            TURKISH.tag -> TURKISH
            VIETNAMESE.tag -> VIETNAMESE
            FILIPINO.tag -> FILIPINO
            JAPANESE.tag -> JAPANESE
            KOREAN.tag -> KOREAN
            FRENCH.tag -> FRENCH
            ITALIAN.tag -> ITALIAN
            UKRAINIAN.tag -> UKRAINIAN
            POLISH.tag -> POLISH
            NEDERLANDS.tag -> NEDERLANDS
            ROMANIAN.tag -> ROMANIAN
            else -> ENGLISH
        }
    }
}