package com.furianrt.notelistui.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.furianrt.notelistui.R
import com.furianrt.notelistui.entities.LocationState
import com.furianrt.uikit.R as uiR
import com.furianrt.uikit.anim.shimmer
import com.furianrt.uikit.extensions.applyIf
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground

@Composable
fun LocationCard(
    state: LocationState,
    modifier: Modifier = Modifier,
    isRemovable: Boolean = false,
    clickable: Boolean = true,
    fullAlpha: Boolean = false,
    onLocationClick: () -> Unit = {},
    onAddLocationClick: () -> Unit = {},
    onRemoveLocationClick: () -> Unit = {},
    onCancelClick: () -> Unit = {},
) {
    AnimatedContent(
        modifier = modifier.applyIf(clickable) {
            Modifier.clickableNoRipple(onClick = onLocationClick)
        },
        targetState = state,
        contentKey = { it !is LocationState.Empty },
        transitionSpec = {
            fadeIn(animationSpec = tween(220, delayMillis = 90))
                .togetherWith(fadeOut(animationSpec = tween(90)))
        }
    ) { targetState ->
        when (targetState) {
            is LocationState.Loading -> LoadingContent(
                onCancelClick = onCancelClick,
            )

            is LocationState.Empty -> EmptyContent(
                onClick = onAddLocationClick,
            )

            is LocationState.Success -> SuccessContent(
                locationTitle = targetState.title,
                isRemovable = isRemovable,
                fullAlpha = fullAlpha,
                onRemoveLocationClick = onRemoveLocationClick,
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun SuccessContent(
    locationTitle: String,
    isRemovable: Boolean,
    fullAlpha: Boolean,
    onRemoveLocationClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var lineCount by remember { mutableIntStateOf(2) }
    val textAlpha = if (fullAlpha) 1f else 0.6f
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = if (lineCount > 1) Alignment.Top else Alignment.CenterVertically,
    ) {
        AnimatedContent(
            targetState = isRemovable,
            transitionSpec = {
                fadeIn(animationSpec = tween(250)).togetherWith(ExitTransition.None)
            },
        ) { targetState ->
            if (targetState) {
                ButtonRemove(
                    onClick = onRemoveLocationClick,
                )
            } else {
                Icon(
                    modifier = Modifier.alpha(textAlpha),
                    painter = painterResource(uiR.drawable.ic_location),
                    contentDescription = null,
                )
            }
        }
        Text(
            modifier = Modifier.alpha(textAlpha),
            text = locationTitle,
            style = MaterialTheme.typography.labelSmall,
            onTextLayout = { lineCount = it.lineCount },
        )
    }
}

@Composable
private fun EmptyContent(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .alpha(0.5f)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(start = 4.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            painter = painterResource(uiR.drawable.ic_location),
            contentDescription = null,
        )
        Text(
            text = stringResource(R.string.note_add_current_location_title),
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@Composable
private fun LoadingContent(
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ButtonRemove(
            onClick = onCancelClick,
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 275.dp)
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .shimmer(color = MaterialTheme.colorScheme.secondaryContainer),
            )
            Box(
                modifier = Modifier
                    .widthIn(max = 175.dp)
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .shimmer(color = MaterialTheme.colorScheme.secondaryContainer),
            )
        }
    }
}

@Composable
private fun ButtonRemove(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .padding(2.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.errorContainer)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            modifier = Modifier
                .padding(2.dp)
                .size(16.dp),
            painter = painterResource(uiR.drawable.ic_close_small),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onErrorContainer,
        )
    }
}

@Composable
@PreviewWithBackground
private fun SuccessPreview() {
    SerenityTheme {
        LocationCard(
            state = LocationState.Success(
                id = "",
                title = "Komsomol'skiy Prospekt, 28А, Chelyabinsk, Chelyabinskaya oblast', 454138",
                latitude = 0.0,
                longitude = 0.0,
            ),
            isRemovable = false,
            onAddLocationClick = {},
            onRemoveLocationClick = {},
        )
    }
}

@Composable
@PreviewWithBackground
private fun SuccessOneLinePreview() {
    SerenityTheme {
        LocationCard(
            state = LocationState.Success(
                id = "",
                title = "Komsomol'skiy Prospekt",
                latitude = 0.0,
                longitude = 0.0,
            ),
            isRemovable = false,
            onAddLocationClick = {},
            onRemoveLocationClick = {},
        )
    }
}

@Composable
@PreviewWithBackground
private fun SuccessRemovablePreview() {
    SerenityTheme {
        LocationCard(
            state = LocationState.Success(
                id = "",
                title = "Komsomol'skiy Prospekt, 28А, Chelyabinsk, Chelyabinskaya oblast', 454138",
                latitude = 0.0,
                longitude = 0.0,
            ),
            isRemovable = true,
        )
    }
}

@Composable
@PreviewWithBackground
private fun EmptyPreview() {
    SerenityTheme {
        LocationCard(
            state = LocationState.Empty,
        )
    }
}

@Composable
@PreviewWithBackground
private fun LoadingPreview() {
    SerenityTheme {
        LocationCard(
            state = LocationState.Loading,
        )
    }
}
