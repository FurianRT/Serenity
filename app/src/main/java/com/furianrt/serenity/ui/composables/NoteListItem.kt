package com.furianrt.serenity.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.furianrt.notecontent.composables.NoteContentTitle
import com.furianrt.notecontent.composables.NoteTags
import com.furianrt.notecontent.entities.UiNoteContent
import com.furianrt.notecontent.entities.UiNoteTag
import com.furianrt.serenity.ui.entities.MainScreenNote
import com.furianrt.uikit.theme.OnTertiaryRippleTheme
import com.furianrt.uikit.theme.SerenityTheme
import kotlinx.collections.immutable.persistentSetOf

@Composable
internal fun NoteListItem(
    note: MainScreenNote,
    modifier: Modifier = Modifier,
    onClick: (note: MainScreenNote) -> Unit,
    onTagClick: (tag: UiNoteTag) -> Unit,
) {
    CompositionLocalProvider(LocalRippleTheme provides OnTertiaryRippleTheme) {
        Column(
            modifier = modifier
                .clip(shape = RoundedCornerShape(8.dp))
                .background(color = MaterialTheme.colorScheme.tertiary)
                .clickable { onClick(note) },
        ) {
            for (item in note.content) {
                when (item) {
                    is UiNoteContent.Title -> {
                        NoteContentTitle(
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .padding(top = 8.dp),
                            title = item,
                        )
                    }

                    is UiNoteContent.Image -> Unit
                }
            }

            NoteTags(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = if (note.tags.isEmpty()) 0.dp else 16.dp)
                    .padding(horizontal = 8.dp),
                tags = note.tags,
                onTagClick = onTagClick,
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
                tags = persistentSetOf(),
                content = persistentSetOf(
                    UiNoteContent.Title(
                        id = "1",
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
