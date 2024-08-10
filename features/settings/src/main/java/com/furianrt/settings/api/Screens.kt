package com.furianrt.settings.api

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.furianrt.settings.internal.ui.SettingsScreenInternal

@Composable
fun SettingsScreen(navHostController: NavHostController) {
    SettingsScreenInternal(navHostController)
}
