package com.furianrt.notelistui.composables

import android.net.Uri
import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.furianrt.notelistui.composables.title.NoteTitleState
import com.furianrt.notelistui.entities.UiNoteContent
import com.furianrt.notelistui.entities.UiNoteFontColor
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.notelistui.entities.UiNoteTag
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.time.ZonedDateTime

private const val SELECTED_SCALE = 0.98f
private const val SELECTED_SCALE_DURATION = 350
private const val MAX_LINES_WITH_MEDIA = 4
private const val MAX_LINES_WITHOUT_MEDIA = 6

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListItem(
    content: ImmutableList<UiNoteContent>,
    tags: ImmutableList<UiNoteTag>,
    fontColor: UiNoteFontColor?,
    fontFamily: UiNoteFontFamily?,
    fontSize: TextUnit,
    date: String,
    modifier: Modifier = Modifier,
    isPinned: Boolean = false,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    onTagClick: ((tag: UiNoteTag.Regular) -> Unit)? = null,
) {
    val interpolator = remember { OvershootInterpolator(5.0f) }
    val scale by animateFloatAsState(
        targetValue = if (isSelected) SELECTED_SCALE else 1f,
        animationSpec = tween(
            durationMillis = SELECTED_SCALE_DURATION,
            easing = if (isSelected) {
                Easing { interpolator.getInterpolation(it) }
            } else {
                FastOutSlowInEasing
            }
        ),
    )
    val onlyText = remember(content) { content.all { it is UiNoteContent.Title } }

    val rippleColor = MaterialTheme.colorScheme.surfaceContainer
    val rippleConfig = remember(rippleColor) {
        RippleConfiguration(
            color = rippleColor,
            rippleAlpha = RippleAlpha(
                draggedAlpha = 0.05f,
                focusedAlpha = 0.05f,
                hoveredAlpha = 0.05f,
                pressedAlpha = 0.05f,
            ),
        )
    }
    CompositionLocalProvider(LocalRippleConfiguration provides rippleConfig) {
        OutlinedCard(
            modifier = modifier
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .clip(RoundedCornerShape(8.dp))
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick,
                ),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isSelected) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.background
                },
            ),
            border = if (isPinned) {
                BorderStroke(0.5.dp, MaterialTheme.colorScheme.primaryContainer)
            } else {
                BorderStroke(0.5.dp, MaterialTheme.colorScheme.inverseSurface)
            }
        ) {
            content.forEachIndexed { index, item ->
                when (item) {
                    is UiNoteContent.Title -> Text(
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp),
                        text = item.state.annotatedString,
                        maxLines = if (onlyText) MAX_LINES_WITHOUT_MEDIA else MAX_LINES_WITH_MEDIA,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = fontFamily?.regular
                                ?: MaterialTheme.typography.bodyMedium.fontFamily,
                            fontSize = fontSize,
                            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight *
                                    (fontSize.value /
                                            MaterialTheme.typography.bodyMedium.fontSize.value),
                        ),
                        color = fontColor?.value ?: Color.Unspecified,
                    )

                    is UiNoteContent.MediaBlock -> NoteContentMedia(
                        modifier = Modifier.padding(top = if (index == 0) 0.dp else 12.dp),
                        block = item,
                        clickable = false,
                    )

                    is UiNoteContent.Voice -> NoteContentVoice(
                        modifier = Modifier.padding(
                            start = 8.dp,
                            top = if (index == 0) 4.dp else 12.dp,
                        ),
                        voice = item,
                        isPayable = false,
                        isPlaying = false,
                        isRemovable = false,
                    )
                }
            }
            NoteTags(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 4.dp,
                        end = 4.dp,
                        top = if (tags.isEmpty()) 0.dp else 16.dp,
                        bottom = 10.dp,
                    ),
                tags = tags,
                date = date,
                onTagClick = onTagClick,
            )
        }
    }
}

@PreviewWithBackground
@Composable
private fun NoteItemPreview() {
    SerenityTheme {
        NoteListItem(
            date = "19.06.2023",
            tags = persistentListOf(
                UiNoteTag.Regular(title = "Programming", isRemovable = false),
                UiNoteTag.Regular(title = "Android", isRemovable = false),
                UiNoteTag.Template(id = "2"),
            ),
            fontColor = UiNoteFontColor.WHITE,
            fontFamily = UiNoteFontFamily.QuickSand,
            fontSize = 16.sp,
            content = generatePreviewContent(),
        )
    }
}

@PreviewWithBackground
@Composable
private fun PinnedNoteItemPreview() {
    SerenityTheme {
        NoteListItem(
            date = "19.06.2023",
            tags = persistentListOf(
                UiNoteTag.Regular(title = "Programming", isRemovable = false),
                UiNoteTag.Regular(title = "Android", isRemovable = false),
                UiNoteTag.Template(id = "2"),
            ),
            fontColor = UiNoteFontColor.WHITE,
            fontFamily = UiNoteFontFamily.QuickSand,
            fontSize = 16.sp,
            isPinned = true,
            content = generatePreviewContent(),
        )
    }
}

@PreviewWithBackground
@Composable
private fun SelectedNoteItemPreview() {
    SerenityTheme {
        NoteListItem(
            date = "19.06.2023",
            tags = persistentListOf(
                UiNoteTag.Regular(title = "Programming", isRemovable = false),
                UiNoteTag.Regular(title = "Android", isRemovable = false),
                UiNoteTag.Template(id = "2"),
            ),
            fontColor = UiNoteFontColor.WHITE,
            fontFamily = UiNoteFontFamily.QuickSand,
            fontSize = 16.sp,
            isPinned = true,
            isSelected = true,
            content = generatePreviewContent(),
        )
    }
}

private fun generatePreviewContent(): ImmutableList<UiNoteContent> = persistentListOf(
    UiNoteContent.Title(
        id = "1",
        state = NoteTitleState(
            fontFamily = UiNoteFontFamily.QuickSand,
            initialText = AnnotatedString(
                text = "Kotlin is a modern programming language with a " +
                        "lot more syntactic sugar compared to Java, and as such " +
                        "there is equally more black magic",
            ),
        ),
    ),
    UiNoteContent.MediaBlock(
        id = "1",
        media = persistentListOf(
            UiNoteContent.MediaBlock.Image(
                id = "",
                name = "",
                addedDate = ZonedDateTime.now(),
                ratio = 1.5f,
                uri = Uri.EMPTY,
            )
        ),
    ),
)
