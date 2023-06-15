package com.furianrt.serenity.ui.composables

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.furianrt.assistant.api.AssistantDefaults
import com.furianrt.assistant.api.AssistantLogo
import com.furianrt.serenity.R
import com.furianrt.serenity.ui.MainUiState
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.extensions.clickableWithScaleAnim
import com.furianrt.uikit.extensions.expand
import com.furianrt.uikit.extensions.isInMiddleState
import com.furianrt.uikit.extensions.performSnap
import com.furianrt.uikit.theme.SerenityTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.onebone.toolbar.CollapsingToolbarScaffoldState
import me.onebone.toolbar.CollapsingToolbarScope
import me.onebone.toolbar.ExperimentalToolbarApi
import com.furianrt.uikit.R as uiR

private const val PARALLAX_RATIO = 0.03f
private const val ANIM_BUTTON_SETTINGS_DURATION = 250
private const val ANIM_BUTTON_SETTINGS_ROTATION = 60f
private const val ANIM_HINT_VISIBILITY_DURATION = 400

@OptIn(ExperimentalToolbarApi::class)
@Composable
internal fun CollapsingToolbarScope.Toolbar(
    toolbarScaffoldState: CollapsingToolbarScaffoldState,
    listState: LazyListState,
    assistantHint: MainUiState.AssistantHint?,
    onSettingsClick: () -> Unit,
    onSearchClick: () -> Unit,
    onAssistantHintCLick: () -> Unit,
) {
    val needToSnapParallax by remember {
        derivedStateOf {
            val toolbarState = toolbarScaffoldState.toolbarState
            val isScrolling = toolbarState.isScrollInProgress || listState.isScrollInProgress
            !isScrolling && toolbarState.isInMiddleState && !toolbarState.isScrollInProgress
        }
    }

    val needToSnapPin by remember {
        derivedStateOf {
            !listState.isScrollInProgress &&
                toolbarScaffoldState.isInMiddleState &&
                !toolbarScaffoldState.toolbarState.isScrollInProgress
        }
    }

    LaunchedEffect(needToSnapParallax, needToSnapPin) {
        if (needToSnapParallax) {
            toolbarScaffoldState.toolbarState.performSnap()
        }

        if (needToSnapPin) {
            toolbarScaffoldState.performSnap()
        }
    }

    val toolbarHeightDp = remember { 64.dp }
    var botHintTop by remember { mutableStateOf(0f) }
    var searchBarTop by remember { mutableStateOf(0f) }

    var message by remember { mutableStateOf(assistantHint?.message) }

    LaunchedEffect(assistantHint) {
        if (assistantHint == null) {
            toolbarScaffoldState.toolbarState.collapse(ANIM_HINT_VISIBILITY_DURATION)
            message = ""
            return@LaunchedEffect
        }

        val prevMessage = message
        if (prevMessage == assistantHint.message) {
            return@LaunchedEffect
        }

        message = assistantHint.message

        delay(200)

        if (assistantHint.forceShow && listState.firstVisibleItemIndex == 0) {
            launch { toolbarScaffoldState.expand(ANIM_HINT_VISIBILITY_DURATION) }
            launch { toolbarScaffoldState.toolbarState.expand(ANIM_HINT_VISIBILITY_DURATION) }
        }
    }

    if (message.isNullOrBlank()) {
        Spacer(
            modifier = Modifier
                .parallax(PARALLAX_RATIO)
                .height(toolbarHeightDp),
        )
    } else {
        AssistantHint(
            modifier = Modifier
                .parallax(PARALLAX_RATIO)
                .padding(start = 16.dp, end = 16.dp, top = toolbarHeightDp, bottom = 20.dp)
                .onGloballyPositioned { botHintTop = it.boundsInRoot().top }
                .drawWithContent {
                    clipRect(
                        right = size.width,
                        top = -size.height,
                        bottom = if (botHintTop == 0f) -size.height else searchBarTop - botHintTop,
                    ) {
                        this@drawWithContent.drawContent()
                    }
                }
                .graphicsLayer {
                    val toolbarState = toolbarScaffoldState.toolbarState
                    val scale = 1f - PARALLAX_RATIO * (1f - toolbarState.progress)
                    scaleX = scale
                    scaleY = scale
                    alpha = toolbarState.progress
                    translationY = -toolbarHeightDp.toPx() + 16.dp.toPx()
                },
            message = message.orEmpty(),
            onClick = onAssistantHintCLick,
        )
    }

    Row(
        modifier = Modifier
            .pin()
            .height(toolbarHeightDp)
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .graphicsLayer {
                translationY = toolbarScaffoldState.toolbarState.height - size.height
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SearchBar(
            modifier = Modifier
                .height(36.dp)
                .padding(end = 16.dp)
                .weight(1f)
                .onGloballyPositioned { searchBarTop = it.boundsInRoot().top },
            onClick = onSearchClick,
        )
        SettingsButton(onClick = onSettingsClick)
    }
}

@Composable
private fun SettingsButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val rotation = remember { Animatable(0f) }

    Icon(
        modifier = modifier
            .graphicsLayer { rotationZ = rotation.value }
            .clickableWithScaleAnim(ANIM_BUTTON_SETTINGS_DURATION) {
                if (rotation.isRunning) {
                    return@clickableWithScaleAnim
                }
                scope.launch {
                    rotation.animateTo(
                        targetValue = rotation.value + ANIM_BUTTON_SETTINGS_ROTATION,
                        animationSpec = tween(ANIM_BUTTON_SETTINGS_DURATION),
                    )
                }
                onClick()
            },
        painter = painterResource(R.drawable.ic_settings),
        contentDescription = stringResource(id = uiR.string.settings_title),
        tint = MaterialTheme.colorScheme.onPrimary,
    )
}

@Composable
private fun AssistantHint(
    message: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val logoHeightDp = remember { 48.dp }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top,
    ) {
        val scope = rememberCoroutineScope()
        val scale = remember { Animatable(1f) }

        AssistantLogo(
            modifier = Modifier
                .size(logoHeightDp)
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                },
            onClick = onClick,
        )
        AssistantMessage(
            modifier = Modifier
                .padding(start = 10.dp)
                .graphicsLayer {
                    translationY = if (size.height > logoHeightDp.toPx()) {
                        0f
                    } else {
                        (logoHeightDp.toPx() - size.height) / 2f
                    }
                },
            text = message,
            onClick = {
                if (scale.isRunning) {
                    return@AssistantMessage
                }
                scope.launch {
                    scale.animateTo(
                        targetValue = 1.1f,
                        animationSpec = tween(AssistantDefaults.ANIM_LOGO_CLICK_SCALE_DURATION / 2),
                    )
                    scale.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(AssistantDefaults.ANIM_LOGO_CLICK_SCALE_DURATION / 2),
                    )
                }
            },
        )
    }
}

@Composable
private fun AssistantMessage(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier.clickableNoRipple(onClick),
        text = text,
        style = MaterialTheme.typography.labelMedium,
        fontStyle = FontStyle.Italic,
    )
}

@Preview
@Composable
private fun AssistantHintPreview() {
    SerenityTheme {
        AssistantHint(
            message = "Hi, i’m your personal AI powered assistant. I can do a lot of things. Let me show you!",
            onClick = {},
        )
    }
}
