package com.furianrt.toolspanel.internal.ui.attachments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.furianrt.toolspanel.R
import com.furianrt.uikit.R as uiR
import com.furianrt.toolspanel.internal.ui.common.ButtonClose
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground

@Composable
internal fun AttachmentsPanel(
    modifier: Modifier = Modifier,
    onSelectMediaClick: () -> Unit = {},
    onTakePictureClick: () -> Unit = {},
    onRecordVoiceClick: () -> Unit = {},
    onCloseClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .padding(horizontal = 4.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            IconButton(
                onClick = onSelectMediaClick,
            ) {
                Icon(
                    painter = painterResource(uiR.drawable.ic_gallery),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
            IconButton(
                onClick = onTakePictureClick,
            ) {
                Icon(
                    painter = painterResource(uiR.drawable.ic_camera),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
            IconButton(
                onClick = onRecordVoiceClick,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_panel_microphone),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
        ButtonClose(
            onClick = onCloseClick,
        )
    }
}

@PreviewWithBackground
@Composable
private fun RegularPanelPreview() {
    SerenityTheme {
        AttachmentsPanel()
    }
}
