package com.furianrt.widgets.internal.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.furianrt.uikit.components.DefaultToolbar
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import com.furianrt.uikit.R as uiR

@Composable
internal fun WidgetsScreen(
    onCloseRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        DefaultToolbar(
            modifier = Modifier.statusBarsPadding(),
            title = stringResource(uiR.string.title_widgets),
            onBackClick = onCloseRequest,
        )
        Spacer(Modifier.size(32.dp))
        AllActionsWidget(
            onClick = {

            },
        )
    }
}

@Composable
private fun AllActionsWidget(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.background)
            .clickable(onClick = onClick)
            .padding(vertical = 32.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .widthIn(max = 230.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                .clickable(onClick = onClick)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(
                modifier = Modifier.weight(0.2f),
            )
            Icon(
                painter = painterResource(uiR.drawable.ic_add),
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = null,
            )
            Separator(
                modifier = Modifier.weight(1f),
            )
            Icon(
                painter = painterResource(uiR.drawable.ic_camera),
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = null,
            )
            Separator(
                modifier = Modifier.weight(1f),
            )
            Icon(
                painter = painterResource(uiR.drawable.ic_video),
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = null,
            )
            Separator(
                modifier = Modifier.weight(1f),
            )
            Icon(
                painter = painterResource(uiR.drawable.ic_microphone),
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = null,
            )
            Spacer(
                modifier = Modifier.weight(0.2f),
            )
        }
    }
}

@Composable
private fun Separator(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Spacer(
            modifier = Modifier
                .width(1.dp)
                .height(32.dp)
                .background(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(24.dp),
                ),
        )
    }
}

@PreviewWithBackground
@Composable
private fun Preview() {
    SerenityTheme {
        AllActionsWidget(
            onClick = {},
        )
    }
}
