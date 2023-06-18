package com.furianrt.noteview.internal.ui.container.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.furianrt.assistant.api.AssistantLogo
import com.furianrt.uikit.extensions.clickableWithScaleAnim
import me.onebone.toolbar.CollapsingToolbarScope
import com.furianrt.uikit.R as uiR

private const val LABEL_BUTTON_EDIT = "button_edit"
private const val ANIM_BUTTON_EDIT_DURATION = 350

@Composable
internal fun CollapsingToolbarScope.Toolbar(
    isInEditMode: () -> Boolean,
    date: () -> String,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .pin()
            .height(64.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.fillMaxHeight(),
            horizontalArrangement = Arrangement.Start,
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
            Text(
                modifier = Modifier.padding(start = 24.dp),
                text = date(),
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Row(
            modifier = Modifier.fillMaxHeight(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ButtonEditAndDone(
                modifier = Modifier
                    .padding(end = 28.dp),
                isInEditMode = isInEditMode,
                onClick = onEditClick,
            )
            AssistantLogo(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(28.dp),
                onClick = {},
            )
            IconButton(
                onClick = {},
            ) {
                Icon(
                    painter = painterResource(id = uiR.drawable.ic_action_menu),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
private fun ButtonEditAndDone(
    onClick: () -> Unit,
    isInEditMode: () -> Boolean,
    modifier: Modifier = Modifier,
) {
    Icon(
        modifier = modifier.clickableWithScaleAnim(
            duration = ANIM_BUTTON_EDIT_DURATION,
            maxScale = 1.15f,
            indication = rememberRipple(bounded = false),
            onClick = onClick,
        ),
        painter = if (isInEditMode()) {
            painterResource(id = uiR.drawable.ic_action_done)
        } else {
            painterResource(id = uiR.drawable.ic_action_edit)
        },
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onPrimary,
    )
}
