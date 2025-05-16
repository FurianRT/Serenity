package com.furianrt.uikit.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp

internal fun getTypography(font: NoteFont) = Typography(
    labelSmall = TextStyle(
        color = Color.White,
        fontFamily = font.light,
        fontSize = 13.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.8.sp,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.Both,
            mode = LineHeightStyle.Mode.Fixed,
        ),
    ),
    labelMedium = TextStyle(
        color = Color.White,
        fontFamily = font.regular,
        fontSize = 14.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.8.sp,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.Both,
            mode = LineHeightStyle.Mode.Fixed,
        ),
    ),
    labelLarge = TextStyle(
        color = Color.White,
        fontFamily = font.regular,
        fontSize = 17.sp,
        lineHeight = 29.sp,
        letterSpacing = 0.8.sp,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.Both,
            mode = LineHeightStyle.Mode.Fixed,
        ),
    ),
    bodySmall = TextStyle(
        color = Color.White,
        fontFamily = font.regular,
        fontSize = 13.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.8.sp,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.Both,
            mode = LineHeightStyle.Mode.Fixed,
        ),
    ),
    bodyMedium = TextStyle(
        color = Color.White,
        fontFamily = font.regular,
        fontSize = 15.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.8.sp,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.Both,
            mode = LineHeightStyle.Mode.Fixed,
        ),
    ),
    bodyLarge = TextStyle(
        color = Color.White,
        fontFamily = font.regular,
        fontSize = 17.sp,
        lineHeight = 29.sp,
        letterSpacing = 0.8.sp,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.Both,
            mode = LineHeightStyle.Mode.Fixed,
        ),
    ),
    titleSmall = TextStyle(
        color = Color.White,
        fontFamily = font.regular,
        fontSize = 15.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.8.sp,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.Both,
            mode = LineHeightStyle.Mode.Fixed,
        ),
    ),
    titleMedium = TextStyle(
        color = Color.White,
        fontFamily = font.regular,
        fontSize = 17.sp,
        lineHeight = 29.sp,
        letterSpacing = 0.8.sp,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.Both,
            mode = LineHeightStyle.Mode.Fixed,
        ),
    ),
    titleLarge = TextStyle(
        color = Color.White,
        fontFamily = font.regular,
        fontSize = 19.sp,
        lineHeight = 29.sp,
        letterSpacing = 0.8.sp,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.Both,
            mode = LineHeightStyle.Mode.Fixed,
        ),
    ),
)
