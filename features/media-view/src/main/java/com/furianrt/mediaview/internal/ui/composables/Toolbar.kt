package com.furianrt.mediaview.internal.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import com.furianrt.mediaview.R
import com.furianrt.uikit.components.ButtonBack
import com.furianrt.uikit.components.ButtonMenu
import com.furianrt.uikit.components.MenuItem
import com.furianrt.uikit.constants.ToolbarConstants
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.theme.Colors
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.LocalAuth
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeChild
import kotlinx.coroutines.launch
import com.furianrt.uikit.R as uiR

@Composable
internal fun Toolbar(
    totalImages: Int,
    currentImageIndex: Int,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onSaveMediaClick: () -> Unit = {},
) {
    var showDropDownMenu by remember { mutableStateOf(false) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(ToolbarConstants.toolbarHeight)
            .clickableNoRipple {}
            .padding(horizontal = 4.dp)
            .systemGestureExclusion(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ButtonBack(
            onClick = onBackClick,
        )
        Counter(
            modifier = Modifier.weight(1f),
            total = totalImages,
            currentIndex = currentImageIndex,
        )
        Box {
            ButtonMenu(
                onClick = { showDropDownMenu = true },
            )
            Menu(
                expanded = showDropDownMenu,
                hazeState = hazeState,
                onDeleteClick = onDeleteClick,
                onSaveMediaClick = onSaveMediaClick,
                onDismissRequest = { showDropDownMenu = false },
            )
        }
    }
}

@Composable
private fun Counter(
    total: Int,
    currentIndex: Int,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        textAlign = TextAlign.Center,
        text = stringResource(uiR.string.media_counter_pattern, currentIndex + 1, total),
        style = MaterialTheme.typography.titleMedium,
    )
}

@Composable
private fun Menu(
    expanded: Boolean,
    hazeState: HazeState,
    onDeleteClick: () -> Unit,
    onSaveMediaClick: () -> Unit,
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
            .hazeChild(
                state = hazeState,
                style = HazeDefaults.style(
                    backgroundColor = Colors.Common.DarkGray,
                    tint = HazeTint(Colors.Common.DarkGray.copy(alpha = 0.5f)),
                    blurRadius = 12.dp,
                ),
            )
           ,
        offset = DpOffset(x = (-8).dp, y = 0.dp),
        containerColor = Color.Transparent,
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 0.dp,
        expanded = expanded,
        onDismissRequest = onDismissRequest,
    ) {
        MenuItem(
            icon = painterResource(R.drawable.ic_download),
            text = stringResource(R.string.media_view_save_to_gallery),
            onClick = {
                onSaveMediaClick()
                onDismissRequest()
            }
        )
        MenuItem(
            icon = painterResource(uiR.drawable.ic_delete),
            text = stringResource(uiR.string.action_delete),
            onClick = {
                onDeleteClick()
                onDismissRequest()
            }
        )
    }
}

@Preview
@Composable
private fun Preview() {
    SerenityTheme {
        Toolbar(
            totalImages = 50,
            currentImageIndex = 25,
            hazeState = HazeState(),
        )
    }
}
