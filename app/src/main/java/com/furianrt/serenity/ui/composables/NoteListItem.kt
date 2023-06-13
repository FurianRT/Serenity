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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.furianrt.notecontent.composables.NoteContentTitle
import com.furianrt.notecontent.composables.NoteTags
import com.furianrt.notecontent.entities.UiNoteContent
import com.furianrt.serenity.ui.entities.MainScreenNote
import com.furianrt.uikit.theme.OnTertiaryRippleTheme
import com.furianrt.uikit.theme.SerenityTheme
import kotlinx.collections.immutable.persistentSetOf

@Composable
internal fun NoteListItem(
    note: MainScreenNote,
    modifier: Modifier = Modifier,
) {
    CompositionLocalProvider(LocalRippleTheme provides OnTertiaryRippleTheme) {
        Column(
            modifier = modifier
                .clip(shape = RoundedCornerShape(8.dp))
                .background(color = MaterialTheme.colorScheme.tertiary),
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
                    .padding(horizontal = 8.dp),
                tags = note.tags,
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
        )
    }
}
