package com.furianrt.notelistui.composables

import android.net.Uri
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.furianrt.notelistui.entities.UiNoteContent
import com.furianrt.notelistui.entities.UiNoteTag
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.time.ZonedDateTime

private val cardRippleAlpha = RippleAlpha(
    draggedAlpha = 0.05f,
    focusedAlpha = 0.05f,
    hoveredAlpha = 0.05f,
    pressedAlpha = 0.05f,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListItem(
    content: ImmutableList<UiNoteContent>,
    tags: ImmutableList<UiNoteTag>,
    date: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    onTagClick: ((tag: UiNoteTag.Regular) -> Unit)? = null,
) {
    val rippleConfig = RippleConfiguration(MaterialTheme.colorScheme.onPrimary, cardRippleAlpha)
    CompositionLocalProvider(LocalRippleConfiguration provides rippleConfig) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick,
                ),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            content.forEachIndexed { index, item ->
                when (item) {
                    is UiNoteContent.Title -> Text(
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp),
                        text = item.state.text.toString(),
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium,
                    )

                    is UiNoteContent.MediaBlock -> NoteContentMedia(
                        modifier = Modifier.padding(top = if (index == 0) 0.dp else 12.dp),
                        block = item,
                        clickable = false,
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
            content = persistentListOf(
                UiNoteContent.Title(
                    id = "1",
                    state = TextFieldState(
                        initialText = "Kotlin is a modern programming language with a " +
                                "lot more syntactic sugar compared to Java, and as such " +
                                "there is equally more black magic",
                    ),
                ),
                UiNoteContent.MediaBlock(
                    id = "1",
                    media = persistentListOf(
                        UiNoteContent.MediaBlock.Image(
                            name = "",
                            addedDate = ZonedDateTime.now(),
                            ratio = 1.5f,
                            uri = Uri.EMPTY,
                        )
                    ),
                ),
            ),
        )
    }
}
