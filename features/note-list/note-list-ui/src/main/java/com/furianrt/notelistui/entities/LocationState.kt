package com.furianrt.notelistui.entities

sealed interface LocationState {
    data object Loading : LocationState
    data object Empty : LocationState
    data class Success(
        val id: String,
        val title: String,
        val latitude: Double,
        val longitude: Double,
    ) : LocationState

    companion object {
        const val BLOCK_ID = "location_block"
    }
}