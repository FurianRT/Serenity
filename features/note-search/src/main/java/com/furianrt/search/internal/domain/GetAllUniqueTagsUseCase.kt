package com.furianrt.search.internal.domain

import com.furianrt.domain.entities.LocalTag
import com.furianrt.domain.repositories.TagsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class GetAllUniqueTagsUseCase @Inject constructor(
    private val tagsRepository: TagsRepository,
) {
    operator fun invoke(): Flow<List<LocalTag>> = tagsRepository.getAllTags()
        .map { tags -> tags.sortedByDescending { it.noteIds.count() } }
}