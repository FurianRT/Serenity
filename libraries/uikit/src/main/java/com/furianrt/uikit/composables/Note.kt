package com.furianrt.uikit.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.furianrt.uikit.entities.UiNote
import com.furianrt.uikit.theme.Colors
import com.furianrt.uikit.theme.SerenityTheme

@Composable
fun NoteItem(note: UiNote) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 200.dp)
            .background(color = Colors.WhiteAlpha5, shape = RoundedCornerShape(8.dp)),
    ) {
       /* for (item in note.content) {
            item.title?.let { NoteItemContentTitle(it) }
            NoteItemContentPhotos(item.photos)
        }
        NoteItemFooter(note)*/
        NoteItemContentTitle(note.title)
    }
}

@Composable
private fun NoteItemContentTitle(textContent: String) {
    Text(
        modifier = Modifier.padding(all = 8.dp),
        text = textContent,
        style = MaterialTheme.typography.bodyMedium,
    )
}

/*
@Composable
private fun NoteItemContentPhotos(photos: List<NoteContent.Photo>) {
}
*/

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun NoteItemFooter(note: UiNote) {
    /*Column {
        FlowRow(
            modifier = Modifier.padding(all = 8.dp),
            mainAxisSpacing = 8.dp,
            crossAxisSpacing = 8.dp
        ) {
            for (tag in note.tags) {
                Text(
                    modifier = Modifier
                        .background(color = Colors.WhiteAlpha5, shape = RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {  }
                        .padding(all = 8.dp),
                    text = tag.name,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }*/
}

@Preview
@Composable
private fun MainSuccessPreview() {
    SerenityTheme {
        NoteItem(
            note = UiNote(
                id = "1",
                time = 0L,
                title = "Kotlin is a modern programming language with a " +
                    "lot more syntactic sugar compared to Java, and as such " +
                    "there is equally more black magic",
            ),
        )
    }
}