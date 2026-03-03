package com.furianrt.uikit.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp

@Composable
@ReadOnlyComposable
fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }

@Composable
@ReadOnlyComposable
fun Int.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }

@Composable
@ReadOnlyComposable
fun Float.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }

fun Dp.dpToPx(density: Density) = with(density) { this@dpToPx.toPx() }

fun Int.pxToDp(density: Density) = with(density) { this@pxToDp.toDp() }

fun Float.pxToDp(density: Density) = with(density) { this@pxToDp.toDp() }