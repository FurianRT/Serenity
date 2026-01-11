package com.furianrt.notepage.internal.domain

import com.furianrt.domain.repositories.TagsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

private const val MAX_RESULTS_COUNT = 3

internal class SearchTagsUseCase @Inject constructor(
    private val tagsRepository: TagsRepository,
) {
    suspend operator fun invoke(
        query: String,
        visibleTags: Set<String>,
    ): List<String> = tagsRepository.searchTags(query)
        .first()
        .filter { it != query && !visibleTags.contains(it) }
        .take(MAX_RESULTS_COUNT)
}