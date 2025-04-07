package com.furianrt.security.internal.ui.lock.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.furianrt.security.R
import com.furianrt.security.internal.ui.lock.entities.PinConstants
import com.furianrt.security.internal.ui.lock.entities.PinCount
import com.furianrt.uikit.anim.ShakingState
import com.furianrt.uikit.anim.rememberShakingState
import com.furianrt.uikit.anim.shakable
import com.furianrt.uikit.extensions.applyIf
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground

@Composable
internal fun Pins(
    title: String,
    pinsCount: PinCount,
    modifier: Modifier = Modifier,
    shakingState: ShakingState = rememberShakingState(),
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
        )
        Row(
            modifier = Modifier.shakable(shakingState),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(PinConstants.MAX_PIN_LENGTH) { index ->
                Pin(isFilled = index < pinsCount.value)
            }
        }
    }
}

@Composable
private fun Pin(
    isFilled: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(16.dp)
            .applyIf(isFilled) {
                Modifier.background(MaterialTheme.colorScheme.primary, CircleShape)
            }
            .applyIf(!isFilled) {
                Modifier.border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape,
                )
            },
    )
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        Pins(
            title = stringResource(R.string.lock_enter_new_pin_title),
            pinsCount = PinCount.TWO,
        )
    }
}
