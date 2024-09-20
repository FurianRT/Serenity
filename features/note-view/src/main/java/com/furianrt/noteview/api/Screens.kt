package com.furianrt.noteview.api

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.furianrt.noteview.internal.ui.NoteViewScreenInternal

@Composable
fun NoteViewScreen(navHostController: NavHostController) {
    NoteViewScreenInternal(navHostController)
}
