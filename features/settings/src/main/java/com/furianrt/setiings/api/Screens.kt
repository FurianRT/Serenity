package com.furianrt.setiings.api

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.furianrt.setiings.internal.ui.SettingsScreenInternal

@Composable
fun SettingsScreen(navHostController: NavHostController) {
    SettingsScreenInternal(navHostController)
}
