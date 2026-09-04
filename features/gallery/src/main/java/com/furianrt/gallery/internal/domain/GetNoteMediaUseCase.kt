package com.furianrt.gallery.internal.domain

import com.furianrt.core.DispatchersProvider
import com.furianrt.core.deepFilter
import com.furianrt.domain.entities.NoteMedia
import com.furianrt.domain.repositories.MediaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import java.time.LocalDate
import javax.inject.Inject

internal class GetNoteMediaUseCase @Inject constructor(
    private val dispatchers: DispatchersProvider,
    private val mediaRepository: MediaRepository,
) {
    operator fun invoke(
        startDate: LocalDate?,
        endDate: LocalDate?,
    ): Flow<List<NoteMedia>> = if (startDate != null) {
        mediaRepository.getNotesMedia()
            .deepFilter { media ->
                val noteDate = media.noteDate.toLocalDate()
                if (endDate != null) {
                    noteDate in startDate..endDate
                } else {
                    noteDate == startDate
                }
            }
            .flowOn(dispatchers.default)
    } else {
        mediaRepository.getNotesMedia()
    }
}