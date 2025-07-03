package com.furianrt.domain.entities

enum class AppLocale(val tag: String, val text: String) {
    ENGLISH(tag ="en", text = "English"),
    RUSSIAN(tag ="ru", text = "Русский");

    companion object {
        fun fromTag(tag: String?): AppLocale = when (tag) {
            ENGLISH.tag -> ENGLISH
            RUSSIAN.tag -> RUSSIAN
            else -> ENGLISH
        }
    }
}