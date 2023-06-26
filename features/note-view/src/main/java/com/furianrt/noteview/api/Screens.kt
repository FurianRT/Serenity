package com.furianrt.noteview.api

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.furianrt.noteview.internal.ui.container.ContainerScreen

@Composable
fun NoteViewScreen(navHostController: NavHostController) {
    ContainerScreen(navHostController)
}
