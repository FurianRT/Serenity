package com.furianrt.domain.entities

import kotlinx.serialization.Serializable

@Serializable
class NoteLocation(
    val id: String,
    val title: String,
    val latitude: Double,
    val longitude: Double,
)