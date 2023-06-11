package com.furianrt.uikit.utils

import com.furianrt.uikit.entities.UiNote

fun generatePreviewNotes() = buildList {
    val title = "Kotlin is a modern programming language with a " +
            "lot more syntactic sugar compared to Java, and as such " +
            "there is equally more black magic"
    for (i in 0..2) {
        add(
            UiNote(
                id = i.toString(),
                time = 0,
                title = title,
            ),
        )
    }
}