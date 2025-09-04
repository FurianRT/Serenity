package com.furianrt.mediasorting.internal.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import com.furianrt.mediasorting.R
import com.furianrt.uikit.R as uiR
import com.furianrt.uikit.components.MenuItem
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.LocalAuth
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import kotlinx.coroutines.launch

@Composable
internal fun AddMediaButton(
    hazeState: HazeState,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDropDown by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .padding(start = 8.dp, end = 12.dp, top = 12.dp, bottom = 12.dp)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.tertiaryContainer,
                shape = RoundedCornerShape(24.dp),
            )
            .clip(RoundedCornerShape(24.dp))
            .clickable { showDropDown = true }
            .aspectRatio(1f),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_add_media_big),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiaryContainer,
        )
        PopUpMenu(
            expanded = showDropDown,
            hazeState = hazeState,
            onGalleryClick = onGalleryClick,
            onCameraClick = onCameraClick,
            onDismissRequest = { showDropDown = false },
        )
    }
}

@Composable
private fun PopUpMenu(
    expanded: Boolean,
    hazeState: HazeState,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val auth = LocalAuth.current

    LifecycleStartEffect(Unit) {
        scope.launch {
            if (!auth.isAuthorized()) {
                onDismissRequest()
            }
        }
        onStopOrDispose {}
    }
    DropdownMenu(
        modifier = Modifier
            .hazeEffect(
                state = hazeState,
                style = HazeDefaults.style(
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    blurRadius = 12.dp,
                )
            )
            .background(MaterialTheme.colorScheme.background),
        containerColor = Color.Transparent,
        offset = DpOffset(x = 8.dp, y = -(8).dp),
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 8.dp,
        expanded = expanded,
        onDismissRequest = onDismissRequest,
    ) {
        MenuItem(
            icon = painterResource(uiR.drawable.ic_gallery),
            text = stringResource(uiR.string.action_gallery),
            onClick = {
                onGalleryClick()
                onDismissRequest()
            },
        )
        MenuItem(
            icon = painterResource(uiR.drawable.ic_camera),
            text = stringResource(uiR.string.action_camera),
            onClick = {
                onCameraClick()
                onDismissRequest()
            },
        )
    }
}

@Composable
@PreviewWithBackground
private fun PopUpMenuPreview() {
    SerenityTheme {
        PopUpMenu(
            expanded = true,
            hazeState = HazeState(),
            onGalleryClick = {},
            onCameraClick = {},
            onDismissRequest = {}
        )
    }
}

@Composable
@PreviewWithBackground
private fun Preview() {
    SerenityTheme {
        AddMediaButton(
            hazeState = HazeState(),
            onGalleryClick = {},
            onCameraClick = {},
        )
    }
}
