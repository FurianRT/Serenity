package com.furianrt.serenity.ui.composables

import android.net.Uri
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.furianrt.notecontent.composables.NoteContentMedia
import com.furianrt.notecontent.composables.NoteContentTitle
import com.furianrt.notecontent.composables.NoteTags
import com.furianrt.notecontent.entities.UiNoteContent
import com.furianrt.notecontent.entities.UiNoteTag
import com.furianrt.serenity.ui.entities.MainScreenNote
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import kotlinx.collections.immutable.persistentListOf

private val cardRippleAlpha = RippleAlpha(
    draggedAlpha = 0.05f,
    focusedAlpha = 0.05f,
    hoveredAlpha = 0.05f,
    pressedAlpha = 0.05f,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NoteListItem(
    note: MainScreenNote,
    onClick: (note: MainScreenNote) -> Unit,
    onTagClick: (tag: UiNoteTag) -> Unit,
    modifier: Modifier = Modifier,
) {
    val rippleConfig = RippleConfiguration(MaterialTheme.colorScheme.onPrimary, cardRippleAlpha)
    CompositionLocalProvider(LocalRippleConfiguration provides rippleConfig) {
        Card(
            modifier = modifier.fillMaxWidth(),
            onClick = { onClick(note) },
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            note.content.forEachIndexed { index, item ->
                when (item) {
                    is UiNoteContent.Title -> {
                        NoteContentTitle(
                            modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp),
                            title = item,
                        )
                    }

                    is UiNoteContent.MediaBlock -> {
                        NoteContentMedia(
                            modifier = Modifier.padding(top = if (index == 0) 0.dp else 12.dp),
                            block = item,
                            clickable = false,
                        )
                    }
                }
            }
            NoteTags(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 4.dp,
                        end = 4.dp,
                        top = if (note.tags.isEmpty()) 0.dp else 16.dp,
                        bottom = 10.dp,
                    ),
                tags = note.tags,
                onTagClick = onTagClick,
                date = note.date,
            )
        }
    }
}

@PreviewWithBackground
@Composable
private fun NoteItemPreview() {
    SerenityTheme {
        NoteListItem(
            note = MainScreenNote(
                id = "1",
                date = "19.06.2023",
                tags = persistentListOf(
                    UiNoteTag.Regular(id = "0", title = "Programming", isRemovable = false),
                    UiNoteTag.Regular(id = "1", title = "Android", isRemovable = false),
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
                                id = "femkfemkf",
                                addedTime = 0,
                                ratio = 1.5f,
                                uri = Uri.EMPTY,
                            )
                        ),
                    ),
                ),
            ),
            onClick = {},
            onTagClick = {},
        )
    }
}
