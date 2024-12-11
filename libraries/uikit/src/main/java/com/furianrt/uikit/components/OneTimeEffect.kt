package com.furianrt.uikit.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope

@Composable
fun OneTimeEffect(key: Any? = Unit, block: suspend CoroutineScope.() -> Unit) {
    var executed by rememberSaveable(key) { mutableStateOf(false) }
    LaunchedEffect(key) {
        if (!executed) {
            block()
            executed = true
        }
    }
}