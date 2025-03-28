package com.furianrt.uikit.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp

val Typography = Typography(
    labelSmall = TextStyle(
        color = Color.White,
        fontFamily = NoteFont.QuickSand.light,
        fontSize = 13.sp,
        lineHeight = 30.sp,
        letterSpacing = 0.7.sp,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.Both,
            mode = LineHeightStyle.Mode.Fixed,
        ),
    ),
    labelMedium = TextStyle(
        color = Color.White,
        fontFamily = NoteFont.QuickSand.regular,
        fontSize = 14.sp,
        lineHeight = 30.sp,
        letterSpacing = 0.7.sp,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.Both,
            mode = LineHeightStyle.Mode.Fixed,
        ),
    ),
    labelLarge = TextStyle(
        color = Color.White,
        fontFamily = NoteFont.QuickSand.regular,
        fontSize = 17.sp,
        lineHeight = 31.sp,
        letterSpacing = 0.7.sp,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.Both,
            mode = LineHeightStyle.Mode.Fixed,
        ),
    ),
    bodySmall = TextStyle(
        color = Color.White,
        fontFamily = NoteFont.QuickSand.regular,
        fontSize = 13.sp,
        lineHeight = 30.sp,
        letterSpacing = 0.7.sp,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.Both,
            mode = LineHeightStyle.Mode.Fixed,
        ),
    ),
    bodyMedium = TextStyle(
        color = Color.White,
        fontFamily = NoteFont.QuickSand.regular,
        fontSize = 15.sp,
        lineHeight = 30.sp,
        letterSpacing = 0.7.sp,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.Both,
            mode = LineHeightStyle.Mode.Fixed,
        ),
    ),
    bodyLarge = TextStyle(
        color = Color.White,
        fontFamily = NoteFont.QuickSand.regular,
        fontSize = 17.sp,
        lineHeight = 31.sp,
        letterSpacing = 0.7.sp,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.Both,
            mode = LineHeightStyle.Mode.Fixed,
        ),
    ),
    titleSmall = TextStyle(
        color = Color.White,
        fontFamily = NoteFont.QuickSand.bold,
        fontSize = 15.sp,
        lineHeight = 30.sp,
        letterSpacing = 0.7.sp,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.Both,
            mode = LineHeightStyle.Mode.Fixed,
        ),
    ),
    titleMedium = TextStyle(
        color = Color.White,
        fontFamily = NoteFont.QuickSand.bold,
        fontSize = 17.sp,
        lineHeight = 31.sp,
        letterSpacing = 0.7.sp,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.Both,
            mode = LineHeightStyle.Mode.Fixed,
        ),
    ),
    titleLarge = TextStyle(
        color = Color.White,
        fontFamily = NoteFont.QuickSand.bold,
        fontSize = 19.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.7.sp,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.Both,
            mode = LineHeightStyle.Mode.Fixed,
        ),
    ),
)
