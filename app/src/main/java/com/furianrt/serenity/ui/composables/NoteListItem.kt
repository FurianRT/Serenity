package com.furianrt.serenity.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.furianrt.notecontent.composables.NoteContentMedia
import com.furianrt.notecontent.composables.NoteContentTitle
import com.furianrt.notecontent.composables.NoteTags
import com.furianrt.notecontent.entities.UiNoteContent
import com.furianrt.notecontent.entities.UiNoteTag
import com.furianrt.serenity.ui.entities.MainScreenNote
import com.furianrt.uikit.extensions.debounceClickable
import com.furianrt.uikit.theme.OnTertiaryRippleTheme
import com.furianrt.uikit.theme.SerenityTheme
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun NoteListItem(
    note: MainScreenNote,
    onClick: (note: MainScreenNote) -> Unit,
    onTagClick: (tag: UiNoteTag) -> Unit,
    modifier: Modifier = Modifier,
) {
    CompositionLocalProvider(LocalRippleTheme provides OnTertiaryRippleTheme) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(8.dp))
                .background(color = MaterialTheme.colorScheme.tertiary)
                .debounceClickable { onClick(note) },
        ) {
            note.content.forEachIndexed { index, item ->
                key(item.id) {
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
                            )
                        }
                    }
                }
            }

            NoteTags(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 8.dp,
                        end = 8.dp,
                        top = if (note.tags.isEmpty()) 0.dp else 12.dp,
                        bottom = 6.dp,
                    ),
                tags = note.tags,
                onTagClick = onTagClick,
                date = "19.06.2023",
            )
        }
    }
}

@Preview
@Composable
private fun NoteItemPreview() {
    SerenityTheme {
        NoteListItem(
            note = MainScreenNote(
                id = "1",
                timestamp = 0L,
                tags = persistentListOf(
                    UiNoteTag.Regular(id = "0", title = "Programming", isRemovable = false),
                    UiNoteTag.Regular(id = "1", title = "Android", isRemovable = false),
                    UiNoteTag.Template(id = "2", title = "Kotlin"),
                ),
                content = persistentListOf(
                    UiNoteContent.Title(
                        id = "1",
                        position = 0,
                        text = "Kotlin is a modern programming language with a " +
                            "lot more syntactic sugar compared to Java, and as such " +
                            "there is equally more black magic",
                    ),
                ),
            ),
            onClick = {},
            onTagClick = {},
        )
    }
}
