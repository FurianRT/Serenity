package com.furianrt.notecreate.api

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.furianrt.notecreate.internal.ui.NoteCreateScreenInternal

@Composable
fun NoteCreateScreen(navHostController: NavHostController) {
    NoteCreateScreenInternal(navHostController)
}