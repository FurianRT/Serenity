package com.furianrt.domain.entities

sealed interface AppTheme {
    data class Regular(val id: String?) : AppTheme
    data class Custom(val background: AppCustomBackground) : AppTheme
}