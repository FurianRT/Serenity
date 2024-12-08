package com.furianrt.domain.usecase

import com.furianrt.domain.entities.LocalNote
import com.furianrt.domain.repositories.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class GetFilteredNotesUseCase @Inject constructor(
    private val notesRepository: NotesRepository,
) {
    operator fun invoke(
        query: String,
        tagsNames: Set<String>,
        startDate: LocalDate?,
        endDate: LocalDate?,
    ): Flow<List<LocalNote>> = notesRepository.getAllNotes(query).map { notes ->
        notes
            .filterByTags(tagsNames)
            .filterByDate(startDate, endDate)
    }


    private fun List<LocalNote>.filterByTags(
        tagsNames: Set<String>,
    ): List<LocalNote> = if (tagsNames.isEmpty()) {
        this
    } else {
        map { it to it.tags.map(LocalNote.Tag::title).intersect(tagsNames).count() }
            .filter { it.second > 0 }
            .sortedByDescending { it.second }
            .map { it.first }
    }

    private fun List<LocalNote>.filterByDate(
        start: LocalDate?,
        end: LocalDate?,
    ): List<LocalNote> = when {
        start != null && end != null -> filter { it.date.toLocalDate() in start..end }
        start != null -> filter { it.date.toLocalDate() == start }
        else -> this
    }
}