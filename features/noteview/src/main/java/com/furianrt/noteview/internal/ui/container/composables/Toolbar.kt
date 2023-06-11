package com.furianrt.noteview.internal.ui.container.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.furianrt.assistant.api.AssistantLogo
import com.furianrt.noteview.internal.ui.container.ContainerEvent
import com.furianrt.uikit.extensions.isInMiddleState
import com.furianrt.uikit.extensions.performSnap
import me.onebone.toolbar.CollapsingToolbarScaffoldState
import me.onebone.toolbar.CollapsingToolbarScope
import com.furianrt.uikit.R as uiR

@Composable
internal fun CollapsingToolbarScope.Toolbar(
    toolbarScaffoldState: CollapsingToolbarScaffoldState,
    isScrollInProgress: Boolean,
    onEvent: (event: ContainerEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(isScrollInProgress) {
        if (!isScrollInProgress && toolbarScaffoldState.isInMiddleState) {
            toolbarScaffoldState.performSnap()
        }
    }

    Row(
        modifier = modifier
            .pin()
            .height(64.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            modifier = Modifier.padding(start = 4.dp),
            onClick = {},
        ) {
            Icon(
                painter = painterResource(id = uiR.drawable.ic_arrow_back),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }

        Row(
            modifier = Modifier.fillMaxHeight(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                modifier = Modifier.padding(end = 20.dp),
                onClick = {},
            ) {
                Icon(
                    painter = painterResource(id = uiR.drawable.ic_action_edit),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
            AssistantLogo(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(32.dp),
                onClick = {},
            )
            IconButton(
                onClick = {},
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(id = uiR.drawable.ic_action_menu),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}
