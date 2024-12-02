package com.furianrt.notelist.internal.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.furianrt.notelist.R
import com.furianrt.uikit.theme.SerenityTheme

@Composable
internal fun SearchBar(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.tertiary)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            modifier = Modifier
                .alpha(0.6f)
                .padding(horizontal = 16.dp),
            text = stringResource(R.string.notes_list_search_bar_title),
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Preview
@Composable
private fun SearchBarPreview() {
    SerenityTheme {
        SearchBar(
            onClick = {},
        )
    }
}
