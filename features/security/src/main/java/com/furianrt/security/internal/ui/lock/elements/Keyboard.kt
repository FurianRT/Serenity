package com.furianrt.security.internal.ui.lock.elements

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.furianrt.security.R
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground

@Composable
internal fun Keyboard(
    modifier: Modifier = Modifier,
    showFingerprint: Boolean = true,
    onKeyClick: (key: Int) -> Unit = {},
    onClearKeyClick: () -> Unit = {},
    onFingerprintClick: () -> Unit = {},
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(40.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(3) { index ->
                Key(
                    key = index + 1,
                    onClick = onKeyClick,
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(40.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(3) { index ->
                Key(
                    key = index + 4,
                    onClick = onKeyClick,
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(40.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(3) { index ->
                Key(
                    key = index + 7,
                    onClick = onKeyClick,
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(40.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (showFingerprint) {
                IconKey(
                    painter = painterResource(R.drawable.ic_fingerprint),
                    onClick = onFingerprintClick
                )
            } else {
                Box(modifier = Modifier.size(56.dp))
            }
            Key(
                key = 0,
                onClick = onKeyClick,

                )
            IconKey(
                painter = painterResource(R.drawable.ic_backspace),
                onClick = onClearKeyClick,
            )
        }
    }
}

@Composable
private fun Key(
    key: Int,
    onClick: (key: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(56.dp)
            .clickable(
                onClick = { onClick(key) },
                interactionSource = null,
                indication = ripple(bounded = false, radius = 32.dp),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = key.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontSize = 27.sp,
        )
    }
}

@Composable
private fun IconKey(
    painter: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(56.dp)
            .clickable(
                onClick = onClick,
                interactionSource = null,
                indication = ripple(bounded = false, radius = 32.dp),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painter,
            tint = Color.Unspecified,
            contentDescription = null,
        )
    }
}

@PreviewWithBackground
@Composable
private fun Preview() {
    SerenityTheme {
        Keyboard()
    }
}
