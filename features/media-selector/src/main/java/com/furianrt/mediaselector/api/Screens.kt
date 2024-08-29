package com.furianrt.mediaselector.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.furianrt.mediaselector.internal.ui.MediaSelectorBottomSheetInternal

@Composable
fun MediaSelectorBottomSheet(
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
) {
    MediaSelectorBottomSheetInternal(
        modifier = modifier,
        navHostController = navHostController,
    )
}
