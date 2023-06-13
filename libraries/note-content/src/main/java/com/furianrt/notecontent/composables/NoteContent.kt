package com.furianrt.notecontent.composables

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.furianrt.notecontent.entities.UiNoteContent
import com.furianrt.uikit.theme.SerenityTheme

@Composable
fun NoteContentTitle(
    title: UiNoteContent.Title,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = title.text,
        style = MaterialTheme.typography.bodyLarge,
    )
}

/*
@Composable
private fun NoteItemContentPhotos(photos: List<NoteContent.Photo>) {
}
*/

/*@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun NoteItemFooter(note: UiNote) {
    Column {
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
    }
}*/

@Preview
@Composable
private fun NoteContentTitlePreview() {
    SerenityTheme {
        NoteContentTitle(
            title = UiNoteContent.Title(
                id = "1",
                text = "Kotlin is a modern programming language with a " +
                    "lot more syntactic sugar compared to Java, and as such " +
                    "there is equally more black magic",
            ),
        )
    }
}
