package com.furianrt.uikit.extensions

import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

fun Modifier.debounceClickable(
    debounceInterval: Long = 400,
    indication: Indication? = null,
    onClick: () -> Unit,
): Modifier = then(
    Modifier.composed {
        var lastClickTime by remember { mutableLongStateOf(0L) }
        clickable(
            onClick = {
                val currentTime = System.currentTimeMillis()
                if ((currentTime - lastClickTime) >= debounceInterval) {
                    lastClickTime = currentTime
                    onClick()
                }
            },
            indication = indication,
            interactionSource = remember { MutableInteractionSource() },
        )
    }
)
