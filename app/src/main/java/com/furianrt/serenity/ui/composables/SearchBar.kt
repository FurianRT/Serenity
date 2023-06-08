package com.furianrt.serenity.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.furianrt.uikit.R as uiR

@Composable
fun SearchBar(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.05f),
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            modifier = Modifier.alpha(0.6f).padding(horizontal = 16.dp),
            text = stringResource(id = uiR.string.search_bar_title),
            style = MaterialTheme.typography.bodySmall,
        )
    }
}
