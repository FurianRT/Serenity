package com.furianrt.serenity.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.R as uiR

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.05f))
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    color = MaterialTheme.colorScheme.onPrimary,
                ),

            ),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            modifier = Modifier
                .alpha(0.6f)
                .padding(horizontal = 16.dp),
            text = stringResource(id = uiR.string.search_bar_title),
            style = MaterialTheme.typography.bodySmall,
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
