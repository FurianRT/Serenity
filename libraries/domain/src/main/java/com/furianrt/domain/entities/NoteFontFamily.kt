package com.furianrt.domain.entities

enum class NoteFontFamily {
    QUICK_SAND,
    SHANTELL_SANS,
    PIXELIFY_SANS,
    ADVENT_PRO,
    CORMORANT_UNICASE,
    MONSERRAT_ALTERNATES,
    TEKTUR,
    DOTO,
    PLAY_WRITE_MODERN,
    TILLANA,
    LIFE_SEVERS,
    TEXTURINA;

    companion object {
        fun fromString(value: String?): NoteFontFamily? {
            return NoteFontFamily.entries.find { it.name == value }
        }
    }
}