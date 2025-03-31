package com.furianrt.backup.internal.extensions

import com.furianrt.backup.internal.domain.entities.PopularQuestion
import com.furianrt.backup.internal.ui.entities.Question

internal fun PopularQuestion.toQuestion(isExpanded: Boolean) = Question(
    id = id,
    title = title,
    answer = answer,
    isExpanded = isExpanded,
)