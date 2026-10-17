package com.furianrt.toolspanel.internal.ui.common

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.furianrt.toolspanel.R
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.theme.SerenityTheme

@Composable
internal fun CustomColorItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Icon(
        modifier = modifier.clickableNoRipple(onClick = onClick),
        painter = painterResource(R.drawable.ic_color_palette),
        tint = null,
        contentDescription = null,
    )
}

@Preview(widthDp = 40, heightDp = 40)
@Composable
private fun Preview() {
    SerenityTheme {
        CustomColorItem(
            onClick = {},
        )
    }
}
