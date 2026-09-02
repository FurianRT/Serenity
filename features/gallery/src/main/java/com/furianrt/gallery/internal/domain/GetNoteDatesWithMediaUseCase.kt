package com.furianrt.gallery.internal.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

internal class GetNoteDatesWithMediaUseCase @Inject constructor(
    private val getNoteMediaUseCase: GetNoteMediaUseCase,
) {
    operator fun invoke(): Flow<Set<LocalDate>> = getNoteMediaUseCase(
        startDate = null,
        endDate = null,
    ).map { media ->
        media
            .map { it.noteDate.toLocalDate() }
            .distinct()
            .toSet()
    }
}