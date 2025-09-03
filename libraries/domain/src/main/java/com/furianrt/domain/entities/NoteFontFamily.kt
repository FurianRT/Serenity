package com.furianrt.domain.entities

enum class NoteFontFamily {
    NOTO_SANS,
    NOTO_SERIF,
    ROBOTO,
    SPACE_MONO,
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
    TEXTURINA,
    PARISIENNE;

    companion object {
        fun fromString(value: String?): NoteFontFamily? {
            return NoteFontFamily.entries.find { it.name == value }
        }
    }
}