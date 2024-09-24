package com.furianrt.mediaview.api

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.furianrt.mediaview.internal.ui.MediaViewScreenInternal

@Composable
fun MediaViewScreen(
    navHostController: NavHostController,
) {
    MediaViewScreenInternal(navHostController)
}