package com.furianrt.uikit.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp

@Composable
fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }

@Composable
fun Int.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }

@Composable
fun Float.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }

fun Dp.dpToPx(density: Density) = with(density) { this@dpToPx.toPx() }

fun Int.pxToDp(density: Density) = with(density) { this@pxToDp.toDp() }

fun Float.pxToDp(density: Density) = with(density) { this@pxToDp.toDp() }