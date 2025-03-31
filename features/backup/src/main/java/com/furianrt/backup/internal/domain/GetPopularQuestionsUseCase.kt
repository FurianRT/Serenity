package com.furianrt.backup.internal.domain

import com.furianrt.backup.R
import com.furianrt.backup.internal.domain.entities.PopularQuestion
import com.furianrt.domain.managers.ResourcesManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject

internal class GetPopularQuestionsUseCase @Inject constructor(
    private val resourcesManager: ResourcesManager,
) {
    operator fun invoke(): Flow<List<PopularQuestion>> = flow {
        emit(buildPopularQuestionsList())
    }

    private fun buildPopularQuestionsList() = listOf(
        PopularQuestion(
            id = UUID.randomUUID().toString(),
            title = resourcesManager.getString(R.string.backup_popular_question_1_title),
            answer = resourcesManager.getString(R.string.backup_popular_question_1_answer),
        ),
        PopularQuestion(
            id = UUID.randomUUID().toString(),
            title = resourcesManager.getString(R.string.backup_popular_question_2_title),
            answer = resourcesManager.getString(R.string.backup_popular_question_2_answer),
        ),
        PopularQuestion(
            id = UUID.randomUUID().toString(),
            title = resourcesManager.getString(R.string.backup_popular_question_3_title),
            answer = resourcesManager.getString(R.string.backup_popular_question_3_answer),
        ),
        PopularQuestion(
            id = UUID.randomUUID().toString(),
            title = resourcesManager.getString(R.string.backup_popular_question_4_title),
            answer = resourcesManager.getString(R.string.backup_popular_question_4_answer),
        ),
        PopularQuestion(
            id = UUID.randomUUID().toString(),
            title = resourcesManager.getString(R.string.backup_popular_question_5_title),
            answer = resourcesManager.getString(R.string.backup_popular_question_5_answer),
        ),
        PopularQuestion(
            id = UUID.randomUUID().toString(),
            title = resourcesManager.getString(R.string.backup_popular_question_6_title),
            answer = resourcesManager.getString(R.string.backup_popular_question_6_answer),
        ),
        PopularQuestion(
            id = UUID.randomUUID().toString(),
            title = resourcesManager.getString(R.string.backup_popular_question_7_title),
            answer = resourcesManager.getString(R.string.backup_popular_question_7_answer),
        ),
        PopularQuestion(
            id = UUID.randomUUID().toString(),
            title = resourcesManager.getString(R.string.backup_popular_question_8_title),
            answer = resourcesManager.getString(R.string.backup_popular_question_8_answer),
        ),
    )
}