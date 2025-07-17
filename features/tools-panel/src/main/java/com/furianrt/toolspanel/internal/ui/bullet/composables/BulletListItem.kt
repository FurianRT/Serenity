package com.furianrt.toolspanel.internal.ui.bullet.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.furianrt.notelistui.composables.title.NoteTitleState
import com.furianrt.uikit.extensions.applyIf
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground

@Composable
internal fun BulletListItem(
    bullet: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .applyIf(isSelected) {
                Modifier.border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface,
                    shape = RoundedCornerShape(8.dp),
                )
            }
            .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(8.dp))
            .clickableNoRipple(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        repeat(3) { index ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = bullet,
                )
                Box(
                    modifier = Modifier
                        .height(6.dp)
                        .fillMaxWidth()
                        .padding(
                            end = when (index) {
                                1 -> 16.dp
                                2 -> 8.dp
                                else -> 0.dp
                            }
                        )
                        .background(MaterialTheme.colorScheme.onTertiaryContainer, CircleShape)
                )
            }
        }
    }
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        BulletListItem(
            bullet = NoteTitleState.BulletListType.Dots.bullet,
            isSelected = true,
            onClick = {},
        )
    }
}
