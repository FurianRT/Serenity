package com.furianrt.settings.internal.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LifecycleStartEffect
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.uikit.components.RadioButtonWithText
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.LocalAuth
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.HazeColorEffect
import dev.chrisbanes.haze.blur.hazeBlur
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AppFontDialog(
    fonts: List<UiNoteFontFamily>,
    selectedFont: UiNoteFontFamily,
    hazeState: HazeState,
    onFontSelected: (font: UiNoteFontFamily) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val auth = LocalAuth.current

    LifecycleStartEffect(Unit) {
        scope.launch {
            if (!auth.isAuthorized()) {
                onDismissRequest()
            }
        }
        onStopOrDispose {}
    }

    BasicAlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest
    ) {
        Content(
            fonts = fonts,
            selectedFont = selectedFont,
            hazeState = hazeState,
            onFontSelected = onFontSelected,
            onDismissRequest = onDismissRequest,
        )
    }
}

@Composable
private fun Content(
    fonts: List<UiNoteFontFamily>,
    selectedFont: UiNoteFontFamily,
    hazeState: HazeState,
    onFontSelected: (font: UiNoteFontFamily) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val colorScheme = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 564.dp)
            .clip(RoundedCornerShape(16.dp))
            .hazeBlur(
                input = HazeInput.Sources(hazeState),
                style = HazeBlurStyle {
                    blurRadius(20.dp)
                    colorEffects(
                        listOf(HazeColorEffect.tint(colorScheme.surface.copy(alpha = 0.7f))),
                    )
                },
            )
            .background(MaterialTheme.colorScheme.surfaceTint)
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        fonts.forEach { font ->
            RadioButtonWithText(
                modifier = Modifier.fillMaxWidth(),
                title = font.name,
                fontFamily = font.regular,
                textStyle = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 17.sp * font.sizeMultiplier,
                ),
                isSelected = font == selectedFont,
                onClick = {
                    onFontSelected(font)
                    scope.launch {
                        delay(150.milliseconds)
                        onDismissRequest()
                    }
                },
            )
        }
    }
}

@Composable
@Preview
private fun Preview() {
    SerenityTheme {
        Content(
            fonts = listOf(
                UiNoteFontFamily.NotoSans,
                UiNoteFontFamily.Doto,
                UiNoteFontFamily.Tektur,
                UiNoteFontFamily.CormorantGaramond
            ),
            selectedFont = UiNoteFontFamily.Doto,
            hazeState = HazeState(),
            onDismissRequest = {},
            onFontSelected = {},
        )
    }
}
