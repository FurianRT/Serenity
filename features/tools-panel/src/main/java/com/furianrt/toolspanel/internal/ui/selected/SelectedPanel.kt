package com.furianrt.toolspanel.internal.ui.selected

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.furianrt.core.orFalse
import com.furianrt.notelistui.composables.title.NoteTitleState
import com.furianrt.notelistui.entities.UiNoteFontBackgroundColor
import com.furianrt.notelistui.entities.UiNoteFontColor
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.toolspanel.R
import com.furianrt.toolspanel.api.ToolsPanelConstants
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground

private enum class PanelState {
    DEFAULT,
    FONT_COLOR,
    FILL_COLOR,
    FONT_SIZE,
}

@Composable
internal fun SelectedPanel(
    titleState: NoteTitleState?,
    modifier: Modifier = Modifier,
) {
    var panelState by remember { mutableStateOf(PanelState.DEFAULT) }
    AnimatedContent(
        modifier = modifier.clickableNoRipple {},
        targetState = panelState,
        transitionSpec = {
            (fadeIn(animationSpec = tween(durationMillis = 220, delayMillis = 90)))
                .togetherWith(fadeOut(animationSpec = tween(durationMillis = 90)))
        },
    ) { targetState ->
        when (targetState) {
            PanelState.DEFAULT -> SelectedStateContent(
                titleState = titleState,
                onFontColorsClick = { panelState = PanelState.FONT_COLOR },
                onFillColorsClick = { panelState = PanelState.FILL_COLOR },
                onFontSizeClick = { panelState = PanelState.FONT_SIZE },
            )

            PanelState.FONT_COLOR -> FontColorsStateContent(
                titleState = titleState,
                onCloseClick = { panelState = PanelState.DEFAULT },
            )

            PanelState.FILL_COLOR -> FillColorsStateContent(
                titleState = titleState,
                onCloseClick = { panelState = PanelState.DEFAULT },
            )

            PanelState.FONT_SIZE -> SizeSelectorStateContent(
                titleState = titleState,
                onCloseClick = { panelState = PanelState.DEFAULT },
            )
        }
    }
}

@Composable
private fun SelectedStateContent(
    modifier: Modifier = Modifier,
    titleState: NoteTitleState?,
    onFontColorsClick: () -> Unit,
    onFillColorsClick: () -> Unit,
    onFontSizeClick: () -> Unit,
) {
    val hasBoldStyles = remember(titleState?.annotatedString, titleState?.selection) {
        titleState?.hasSpan(
            start = titleState.selection.min,
            end = titleState.selection.max,
            spanType = NoteTitleState.SpanType.Bold
        ).orFalse()
    }
    val hasItalicStyles = remember(titleState?.annotatedString, titleState?.selection) {
        titleState?.hasSpan(
            start = titleState.selection.min,
            end = titleState.selection.max,
            spanType = NoteTitleState.SpanType.Italic
        ).orFalse()
    }
    val hasUnderlineStyles = remember(titleState?.annotatedString, titleState?.selection) {
        titleState?.hasSpan(
            start = titleState.selection.min,
            end = titleState.selection.max,
            spanType = NoteTitleState.SpanType.Underline
        ).orFalse()
    }
    val hasStrikeThroughStyles = remember(titleState?.annotatedString, titleState?.selection) {
        titleState?.hasSpan(
            start = titleState.selection.min,
            end = titleState.selection.max,
            spanType = NoteTitleState.SpanType.Strikethrough
        ).orFalse()
    }
    val hasFontSizeStyles = remember(titleState?.annotatedString, titleState?.selection) {
        titleState?.hasSpan(
            start = titleState.selection.min,
            end = titleState.selection.max,
            spanType = NoteTitleState.SpanType.FontSize(),
        ).orFalse()
    }
    val hasFontColorStyles = remember(titleState?.annotatedString, titleState?.selection) {
        titleState?.hasAnyFontColorSpan(
            start = titleState.selection.min,
            end = titleState.selection.max,
        ).orFalse()
    }
    val hasFillColorStyles = remember(titleState?.annotatedString, titleState?.selection) {
        titleState?.hasAnyFillColorSpan(
            start = titleState.selection.min,
            end = titleState.selection.max,
        ).orFalse()
    }
    Row(
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = {
                if (hasBoldStyles) {
                    titleState?.removeSpan(
                        start = titleState.selection.min,
                        end = titleState.selection.max,
                        spanType = NoteTitleState.SpanType.Bold,
                    )
                } else {
                    titleState?.addSpan(
                        start = titleState.selection.min,
                        end = titleState.selection.max,
                        spanType = NoteTitleState.SpanType.Bold,
                    )
                }
            },
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_panel_bold),
                contentDescription = null,
                tint = if (hasBoldStyles) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            )
        }
        IconButton(
            onClick = {
                if (hasItalicStyles) {
                    titleState?.removeSpan(
                        start = titleState.selection.min,
                        end = titleState.selection.max,
                        spanType = NoteTitleState.SpanType.Italic,
                    )
                } else {
                    titleState?.addSpan(
                        start = titleState.selection.min,
                        end = titleState.selection.max,
                        spanType = NoteTitleState.SpanType.Italic,
                    )
                }
            },
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_panel_italic),
                contentDescription = null,
                tint = if (hasItalicStyles) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            )
        }
        IconButton(
            onClick = {
                if (hasUnderlineStyles) {
                    titleState?.removeSpan(
                        start = titleState.selection.min,
                        end = titleState.selection.max,
                        spanType = NoteTitleState.SpanType.Underline,
                    )
                } else {
                    titleState?.addSpan(
                        start = titleState.selection.min,
                        end = titleState.selection.max,
                        spanType = NoteTitleState.SpanType.Underline,
                    )
                }
            },
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_panel_underline),
                contentDescription = null,
                tint = if (hasUnderlineStyles) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            )
        }
        IconButton(
            onClick = {
                if (hasStrikeThroughStyles) {
                    titleState?.removeSpan(
                        start = titleState.selection.min,
                        end = titleState.selection.max,
                        spanType = NoteTitleState.SpanType.Strikethrough,
                    )
                } else {
                    titleState?.addSpan(
                        start = titleState.selection.min,
                        end = titleState.selection.max,
                        spanType = NoteTitleState.SpanType.Strikethrough,
                    )
                }
            },
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_panel_strikethrough),
                contentDescription = null,
                tint = if (hasStrikeThroughStyles) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            )
        }
        IconButton(
            onClick = {
                if (hasFontSizeStyles) {
                    titleState?.removeSpan(
                        start = titleState.selection.min,
                        end = titleState.selection.max,
                        spanType = NoteTitleState.SpanType.FontSize(),
                    )
                } else {
                    onFontSizeClick()
                }
            },
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_panel_font_size),
                contentDescription = null,
                tint = if (hasFontSizeStyles) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            )
        }
        IconButton(
            onClick = {
                if (hasFontColorStyles) {
                    titleState?.removeSpan(
                        start = titleState.selection.min,
                        end = titleState.selection.max,
                        spanType = NoteTitleState.SpanType.FontColor(),
                    )
                } else {
                    onFontColorsClick()
                }
            },
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_panel_font_color),
                contentDescription = null,
                tint = if (hasFontColorStyles) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            )
        }
        IconButton(
            onClick = {
                if (hasFillColorStyles) {
                    titleState?.removeSpan(
                        start = titleState.selection.min,
                        end = titleState.selection.max,
                        spanType = NoteTitleState.SpanType.FillColor(),
                    )
                } else {
                    onFillColorsClick()
                }
            },
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_panel_fill_color),
                contentDescription = null,
                tint = if (hasFillColorStyles) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            )
        }
    }
}

@Composable
private fun FontColorsStateContent(
    titleState: NoteTitleState?,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = remember { UiNoteFontColor.entries.map { it.value } }
    val selectedColor = remember(titleState?.annotatedString, titleState?.selection) {
        titleState
            ?.getSpans(
                start = titleState.selection.min,
                end = titleState.selection.max,
                spanType = NoteTitleState.SpanType.FontColor(),
            )
            ?.filterIsInstance<NoteTitleState.SpanType.FontColor>()
            ?.map { UiNoteFontColor.fromColor(it.color).value }
            ?.takeIf { it.count() == 1 }
            ?.firstOrNull()
    }
    ColorsPanel(
        modifier = modifier,
        colors = colors,
        selectedColor = selectedColor,
        onCloseClick = onCloseClick,
        onColorSelected = { color ->
            if (color == null) {
                titleState?.removeSpan(
                    start = titleState.selection.min,
                    end = titleState.selection.max,
                    spanType = NoteTitleState.SpanType.FontColor(),
                )
            } else {
                titleState?.addSpan(
                    start = titleState.selection.min,
                    end = titleState.selection.max,
                    spanType = NoteTitleState.SpanType.FontColor(color = color),
                )
            }
        },
    )
}

@Composable
private fun FillColorsStateContent(
    titleState: NoteTitleState?,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = remember { UiNoteFontBackgroundColor.entries.map { it.value } }
    val selectedColor = remember(titleState?.annotatedString, titleState?.selection) {
        titleState
            ?.getSpans(
                start = titleState.selection.min,
                end = titleState.selection.max,
                spanType = NoteTitleState.SpanType.FillColor(),
            )
            ?.filterIsInstance<NoteTitleState.SpanType.FillColor>()
            ?.map { UiNoteFontBackgroundColor.fromColor(it.color)?.value }
            ?.takeIf { it.count() == 1 }
            ?.firstOrNull()
    }
    ColorsPanel(
        modifier = modifier,
        colors = colors,
        selectedColor = selectedColor,
        onCloseClick = onCloseClick,
        onColorSelected = { color ->
            if (color == null) {
                titleState?.removeSpan(
                    start = titleState.selection.min,
                    end = titleState.selection.max,
                    spanType = NoteTitleState.SpanType.FillColor(),
                )
            } else {
                titleState?.addSpan(
                    start = titleState.selection.min,
                    end = titleState.selection.max,
                    spanType = NoteTitleState.SpanType.FillColor(color = color),
                )
            }
        },
    )
}

@Composable
fun SizeSelectorStateContent(
    titleState: NoteTitleState?,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SizeSliderPanel(
        modifier = modifier,
        onCloseClick = onCloseClick,
        onValueChange = { value ->
            if (value == 1f) {
                titleState?.removeSpan(
                    start = titleState.selection.min,
                    end = titleState.selection.max,
                    spanType = NoteTitleState.SpanType.FontSize(),
                )
            } else {
                titleState?.addSpan(
                    start = titleState.selection.min,
                    end = titleState.selection.max,
                    spanType = NoteTitleState.SpanType.FontSize(multiplier = value),
                )
            }
        }
    )
}

@PreviewWithBackground
@Composable
private fun SelectedPanelPreview() {
    SerenityTheme {
        SelectedPanel(
            modifier = Modifier.height(ToolsPanelConstants.PANEL_HEIGHT),
            titleState = NoteTitleState(
                fontFamily = UiNoteFontFamily.NotoSans,
                fontSize = 16.sp,
            ),
        )
    }
}
