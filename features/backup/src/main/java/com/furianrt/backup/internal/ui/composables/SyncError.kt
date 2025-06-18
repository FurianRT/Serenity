package com.furianrt.backup.internal.ui.composables

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.furianrt.backup.R
import com.furianrt.uikit.theme.SerenityTheme

@Composable
internal fun SyncError(
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = stringResource(R.string.backup_error_message),
        style = MaterialTheme.typography.labelMedium,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.errorContainer,
    )
}

@Preview
@Composable
private fun Preview() {
    SerenityTheme {
        SyncError()
    }
}
