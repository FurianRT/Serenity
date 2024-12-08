package com.furianrt.search.internal.ui.composables

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.pluralStringResource
import com.furianrt.notesearch.R

@Composable
internal fun NotesCountItem(
    count: Int,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier.alpha(0.5f),
        text = pluralStringResource(R.plurals.note_search_notes_count_title, count, count),
        style = MaterialTheme.typography.labelSmall,
    )
}