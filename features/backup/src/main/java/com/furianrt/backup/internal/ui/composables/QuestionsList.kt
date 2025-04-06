package com.furianrt.backup.internal.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.furianrt.backup.R
import com.furianrt.backup.internal.ui.entities.Question
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.theme.SerenityTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun QuestionsList(
    questions: ImmutableList<Question>,
    isSignedIn: Boolean,
    onQuestionClick: (question: Question) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        questions.forEachIndexed { index, question ->
            QuestionItem(
                question = question,
                index = index,
                isSignedIn = isSignedIn,
                onClick = onQuestionClick,
            )
        }
    }
}

@Composable
private fun QuestionItem(
    question: Question,
    index: Int,
    isSignedIn: Boolean,
    onClick: (question: Question) -> Unit,
    modifier: Modifier = Modifier,
) {
    val alpha by animateFloatAsState(
        targetValue = if (question.isExpanded || !isSignedIn) 1f else 0.5f,
    )
    Column(
        modifier = modifier
            .animateContentSize()
            .clickableNoRipple { onClick(question) },
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            modifier = Modifier.alpha(alpha),
            text = "${index + 1}. ${question.title}",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.ExtraBold,
        )
        AnimatedVisibility(
            modifier = Modifier.alpha(alpha),
            visible = question.isExpanded,
            enter = fadeIn(),
            exit = ExitTransition.None,
        ) {
            Text(
                text = question.answer,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
@Preview
private fun Preview() {
    SerenityTheme {
        QuestionsList(
            questions = persistentListOf(
                Question(
                    id = "",
                    title = stringResource(R.string.backup_popular_question_1_title),
                    answer = stringResource(R.string.backup_popular_question_1_answer),
                    isExpanded = false,
                ),
                Question(
                    id = "",
                    title = stringResource(R.string.backup_popular_question_2_title),
                    answer = stringResource(R.string.backup_popular_question_2_answer),
                    isExpanded = true,
                ),
                Question(
                    id = "",
                    title = stringResource(R.string.backup_popular_question_3_title),
                    answer = stringResource(R.string.backup_popular_question_3_answer),
                    isExpanded = false,
                ),
            ),
            isSignedIn = true,
            onQuestionClick = {},
        )
    }
}
