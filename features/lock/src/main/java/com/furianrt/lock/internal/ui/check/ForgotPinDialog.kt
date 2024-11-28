package com.furianrt.lock.internal.ui.check

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.furianrt.lock.R
import com.furianrt.uikit.R as uiR
import com.furianrt.uikit.components.ConfirmationDialog
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeState

@Composable
internal fun ForgotPinDialog(
    email: String,
    hazeState: HazeState,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    onConfirmClick: () -> Unit = {},
) {
    val fullText = stringResource(R.string.pin_recovery_dialog_title, email)
    val hint = remember {
        buildAnnotatedString {
            val emailStartIndex = fullText.indexOf(email)
            val emailEndIndex = emailStartIndex + email.length
            append(fullText.substring(0, emailStartIndex))
            withStyle(SpanStyle(fontWeight = FontWeight.ExtraBold)) {
                append(email)
            }
            append(fullText.substring(emailEndIndex, fullText.length))
        }
    }
    ConfirmationDialog(
        modifier = modifier,
        hint = hint,
        confirmText = stringResource(uiR.string.action_send),
        hazeState = hazeState,
        hideOnLock = false,
        onDismissRequest = onDismissRequest,
        onConfirmClick = onConfirmClick,
    )
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        ForgotPinDialog(
            email = "te***st@test.com",
            onDismissRequest = {},
            hazeState = HazeState(),
        )
    }
}
