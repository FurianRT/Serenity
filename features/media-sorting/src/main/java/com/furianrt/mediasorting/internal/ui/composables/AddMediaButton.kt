package com.furianrt.mediasorting.internal.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.furianrt.uikit.components.ActionButton
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import com.furianrt.uikit.R as uiR

@Composable
internal fun AddMediaButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ActionButton(
        modifier = modifier,
        icon = painterResource(uiR.drawable.ic_add),
        onClick = onClick,
    )
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        AddMediaButton(
            onClick = {},
        )
    }
}
