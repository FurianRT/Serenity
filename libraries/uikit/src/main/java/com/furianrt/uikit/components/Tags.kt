package com.furianrt.uikit.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.furianrt.uikit.R
import com.furianrt.uikit.extensions.applyIf
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground

private const val ANIM_EDIT_MODE_DURATION = 250

@Composable
fun TagItem(
    title: String,
    isRemovable: Boolean,
    modifier: Modifier = Modifier,
    background: Color = MaterialTheme.colorScheme.secondaryContainer,
    textStyle: TextStyle = MaterialTheme.typography.labelSmall,
    textColor: Color = Color.Unspecified,
    horizontalPadding: Dp = 10.dp,
    onClick: (() -> Unit)? = null,
    onRemoveClick: () -> Unit = {},
    icon: (@Composable () -> Unit)? = null,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopStart,
    ) {
        Row(
            modifier = Modifier
                .padding(all = 4.dp)
                .clip(RoundedCornerShape(size = 16.dp))
                .background(background)
                .applyIf(onClick != null) { Modifier.clickable { onClick?.invoke() } }
                .padding(horizontal = horizontalPadding, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = textStyle,
                color = textColor,
            )
            if (icon != null) {
                icon()
            }
        }

        AnimatedVisibility(
            visible = isRemovable,
            enter = fadeIn(animationSpec = tween(durationMillis = ANIM_EDIT_MODE_DURATION)),
            exit = fadeOut(animationSpec = tween(durationMillis = ANIM_EDIT_MODE_DURATION)),
        ) {
            DeleteTagButton(onClick = { onRemoveClick() })
        }
    }
}

@Composable
private fun DeleteTagButton(
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(18.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.errorContainer)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            modifier = Modifier.size(16.dp),
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_close_small),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onErrorContainer,
        )
    }
}

@PreviewWithBackground
@Composable
private fun NoteTagsPreview() {
    SerenityTheme {
        TagItem(
            title = "Tag title",
            isRemovable = true,
        )
    }
}
