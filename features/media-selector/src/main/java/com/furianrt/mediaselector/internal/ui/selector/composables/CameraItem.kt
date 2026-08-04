package com.furianrt.mediaselector.internal.ui.selector.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import com.furianrt.uikit.R as uiR

@Composable
internal fun CameraItem(
    allowVideo: Boolean,
    onPhotoClick: () -> Unit,
    onVideoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.aspectRatio(1f),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Black.copy(alpha = 0.6f))
                .clickable(onClick = onPhotoClick)
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier
                    .size(if (allowVideo) 24.dp else 32.dp)
                    .padding(3.dp),
                painter = painterResource(uiR.drawable.ic_camera),
                tint = Color.White,
                contentDescription = null,
            )
            if (allowVideo) {
                BasicText(
                    text = stringResource(uiR.string.action_photo),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color::White,
                    maxLines = 1,
                    autoSize = TextAutoSize.StepBased(
                        minFontSize = MaterialTheme.typography.bodyMedium.fontSize * 0.5f,
                        maxFontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    ),
                )
            }
        }
        if (allowVideo) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable(onClick = onVideoClick)
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(uiR.drawable.ic_video),
                    tint = Color.White,
                    contentDescription = null,
                )
                BasicText(
                    text = stringResource(uiR.string.action_video),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color::White,
                    autoSize = TextAutoSize.StepBased(
                        minFontSize = MaterialTheme.typography.bodyMedium.fontSize * 0.5f,
                        maxFontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    ),
                )
            }
        }
    }
}

@PreviewWithBackground
@Composable
private fun PreviewWithVideo() {
    SerenityTheme {
        CameraItem(
            modifier = Modifier.size(140.dp),
            allowVideo = true,
            onPhotoClick = {},
            onVideoClick = {},
        )
    }
}

@PreviewWithBackground
@Composable
private fun PreviewWithoutVideo() {
    SerenityTheme {
        CameraItem(
            modifier = Modifier.size(140.dp),
            allowVideo = false,
            onPhotoClick = {},
            onVideoClick = {},
        )
    }
}
