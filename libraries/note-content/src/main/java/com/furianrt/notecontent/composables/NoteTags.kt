package com.furianrt.notecontent.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.furianrt.core.buildImmutableList
import com.furianrt.notecontent.R
import com.furianrt.notecontent.entities.UiNoteTag
import com.furianrt.uikit.extensions.times
import com.furianrt.uikit.theme.OnTertiaryRippleTheme
import com.furianrt.uikit.theme.SerenityRippleTheme
import com.furianrt.uikit.theme.SerenityTheme
import kotlinx.collections.immutable.ImmutableList

private const val ANIM_EDIT_MODE_DURATION = 250

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NoteTags(
    tags: ImmutableList<UiNoteTag>,
    modifier: Modifier = Modifier,
    date: String? = null,
    isEditable: Boolean = false,
    onTagClick: (tag: UiNoteTag) -> Unit = {},
    onTagRemoveClick: (tag: UiNoteTag) -> Unit = {},
) {
    FlowRow(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        tags.forEach { tag ->
            when (tag) {
                is UiNoteTag.Regular -> RegularNoteTagItem(
                    tag = tag,
                    isRemovable = isEditable,
                    onClick = { onTagClick(tag) },
                    onRemoveClick = onTagRemoveClick,
                )

                is UiNoteTag.Template -> TemplateNoteTagItem(
                    tag = tag,
                    onClick = { onTagClick(tag) },
                )
            }
        }

        if (date != null) {
            NoteDateItem(
                modifier = Modifier.weight(1f),
                text = date,
                topPadding = if (tags.isEmpty()) 12.dp else 4.dp,
            )
        }
    }
}

@Composable
private fun RegularNoteTagItem(
    tag: UiNoteTag.Regular,
    onClick: () -> Unit,
    onRemoveClick: (tag: UiNoteTag) -> Unit,
    isRemovable: Boolean,
    modifier: Modifier = Modifier,
) {
    CompositionLocalProvider(LocalRippleTheme provides OnTertiaryRippleTheme) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.TopStart,
        ) {
            Box(
                modifier = Modifier
                    .padding(all = 4.dp)
                    .clip(RoundedCornerShape(size = 8.dp))
                    .background(MaterialTheme.colorScheme.tertiary)
                    .clickable(onClick = onClick),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    text = tag.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelSmall,
                )
            }

            AnimatedVisibility(
                visible = isRemovable,
                enter = fadeIn(animationSpec = tween(durationMillis = ANIM_EDIT_MODE_DURATION)),
                exit = fadeOut(animationSpec = tween(durationMillis = ANIM_EDIT_MODE_DURATION)),
            ) {
                DeleteTagButton(onClick = { onRemoveClick(tag) })
            }
        }
    }
}

@Composable
private fun TemplateNoteTagItem(
    tag: UiNoteTag.Template,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    CompositionLocalProvider(LocalRippleTheme provides OnTertiaryRippleTheme) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(size = 8.dp))
                .background(MaterialTheme.colorScheme.tertiary)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                text = tag.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Composable
private fun DeleteTagButton(
    onClick: () -> Unit,
) {
    CompositionLocalProvider(LocalRippleTheme provides RemoveTagRippleTheme) {
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
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer,
            )
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
private fun NoteDateItem(
    text: String,
    topPadding: Dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterEnd,
    ) {
        val textMeasurer = rememberTextMeasurer()
        textMeasurer.measure(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
        )
        Text(
            modifier = Modifier
                .padding(end = 8.dp, bottom = 4.dp, top = topPadding)
                .widthIn(
                    min = LocalDensity.current.run {
                        textMeasurer.measure(
                            text = text,
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                        ).size.width.toDp()
                    },
                )
                .alpha(0.6f),
            text = text,
            textAlign = TextAlign.End,
            maxLines = 1,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

private object RemoveTagRippleTheme : SerenityRippleTheme() {
    @Composable
    override fun defaultColor(): Color = MaterialTheme.colorScheme.onPrimary

    @Composable
    override fun rippleAlpha(): RippleAlpha = RippleTheme.defaultRippleAlpha(
        MaterialTheme.colorScheme.errorContainer,
        lightTheme = !isSystemInDarkTheme(),
    ) * 3f
}

@Preview
@Composable
private fun NoteTagsPreview() {
    SerenityTheme {
        NoteTags(
            tags = generatePreviewTags(),
            date = "Sat 9:12 PM",
            onTagRemoveClick = { },
            onTagClick = {},
        )
    }
}

private fun generatePreviewTags() = buildImmutableList {
    add(UiNoteTag.Regular(id = "-1", title = "Programming", isRemovable = false))
    add(UiNoteTag.Regular(id = "0", title = "Programming", isRemovable = false))
    add(UiNoteTag.Regular(id = "1", title = "Android", isRemovable = true))
    add(UiNoteTag.Regular(id = "2", title = "Android", isRemovable = true))
    add(UiNoteTag.Regular(id = "3", title = "Android", isRemovable = true))
    add(UiNoteTag.Template(id = "4", title = "Kotlin"))
}
