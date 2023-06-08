package com.furianrt.serenity.ui.composables

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.furianrt.serenity.R
import com.furianrt.uikit.theme.SerenityTheme

private const val OFFSET_ANIM_DURATION = 350
private const val ALPHA_ANIM_DURATION = 250
private const val LABEL_OFFSET_ANIM = "BottomNavigationBar_offset_anim"

@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    needToHideNavigation: () -> Boolean,
    needToShowScrollUpButton: () -> Boolean,
    onScrollToTopClick: () -> Unit,
) {
    val offset by animateFloatAsState(
        targetValue = if (needToHideNavigation()) 1f else 0f,
        animationSpec = tween(durationMillis = OFFSET_ANIM_DURATION),
        label = LABEL_OFFSET_ANIM,
    )

    Column(
        modifier = modifier
            .graphicsLayer { translationY = (90.dp.toPx() + size.height) * offset },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ButtonScrollToTop(
            modifier = Modifier.padding(bottom = 16.dp),
            isVisible = needToShowScrollUpButton,
            onClick = onScrollToTopClick,
        )
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ButtonOptionMenu(
                iconRes = R.drawable.ic_search,
                onClick = {},
            )
            ButtonAddNote(
                modifier = Modifier.padding(horizontal = 40.dp),
                onClick = {},
            )
            ButtonOptionMenu(
                iconRes = R.drawable.ic_settings,
                onClick = {},
            )
        }
    }
}

@Composable
private fun ButtonAddNote(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(64.dp)
            .shadow(8.dp, CircleShape)
            .background(MaterialTheme.colorScheme.secondary, CircleShape)
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    color = MaterialTheme.colorScheme.onSecondary,
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_add),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondary,
        )
    }
}

@Composable
private fun ButtonOptionMenu(
    @DrawableRes iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .alpha(0.7f)
            .size(48.dp)
            .shadow(8.dp, CircleShape)
            .background(MaterialTheme.colorScheme.secondary, CircleShape)
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    color = MaterialTheme.colorScheme.onSecondary,
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondary,
        )
    }
}

@Composable
private fun ButtonScrollToTop(
    modifier: Modifier = Modifier,
    isVisible: () -> Boolean,
    onClick: () -> Unit,
) {
    AnimatedVisibility(
        visible = isVisible(),
        enter = fadeIn(animationSpec = tween(durationMillis = ALPHA_ANIM_DURATION)),
        exit = fadeOut(animationSpec = tween(durationMillis = ALPHA_ANIM_DURATION)),
    ) {
        Box(
            modifier = modifier
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp))
                .background(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(24.dp),
                )
                .clickable(
                    onClick = onClick,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(
                        color = MaterialTheme.colorScheme.onSecondary,
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                text = stringResource(id = R.string.button_scroll_to_top_title),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Preview
@Composable
private fun BottomNavigationBarPreview() {
    SerenityTheme {
        BottomNavigationBar(
            needToHideNavigation = { false },
            needToShowScrollUpButton = { true },
            onScrollToTopClick = {},
        )
    }
}
