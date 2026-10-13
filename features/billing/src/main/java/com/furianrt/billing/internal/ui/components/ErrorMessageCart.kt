package com.furianrt.billing.internal.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.furianrt.billing.R
import com.furianrt.billing.internal.ui.entities.ErrorMessageType
import com.furianrt.uikit.components.OptionButtonWrapper
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.rememberHazeState

@Composable
internal fun ErrorMessageCart(
    errorMessageType: ErrorMessageType,
    hazeState: HazeState,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OptionButtonWrapper(
        modifier = modifier,
        hazeState = hazeState,
        borderColor = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = errorMessageType.getTitle(),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
            errorMessageType.getSubtitle()?.let { subtitle ->
                Spacer(Modifier.size(12.dp))
                Text(
                    modifier = Modifier.alpha(0.85f),
                    text = subtitle,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            Spacer(Modifier.size(24.dp))
            OutlinedButton(
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                onClick = onRetryClick,
            ) {
                Text(
                    text = stringResource(R.string.billing_retry_button),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@PreviewWithBackground
@Composable
private fun Preview() {
    SerenityTheme {
        ErrorMessageCart(
            errorMessageType = ErrorMessageType.BILLING_UNAVAILABLE,
            hazeState = rememberHazeState(),
            onRetryClick = {},
        )
    }
}
