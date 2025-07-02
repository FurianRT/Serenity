package com.furianrt.domain.entities

class MediaSortingResult(
    val noteId: String,
    val mediaBlockId: String,
    val media: List<LocalNote.Content.Media>,
)