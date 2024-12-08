package com.furianrt.search.api.entities

import java.time.LocalDate

class QueryData(
    val query: String,
    val tags: Set<String>,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
)