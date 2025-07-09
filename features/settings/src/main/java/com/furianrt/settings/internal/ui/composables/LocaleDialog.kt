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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import com.furianrt.domain.entities.AppLocale
import com.furianrt.uikit.components.RadioButtonWithText
import com.furianrt.uikit.utils.LocalAuth
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LocaleDialog(
    locales: List<AppLocale>,
    selectedLocale: AppLocale,
    hazeState: HazeState,
    onLocaleSelected: (locale: AppLocale) -> Unit,
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

    BasicAlertDialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .heightIn(max = 616.dp)
                .clip(RoundedCornerShape(16.dp))
                .hazeEffect(
                    state = hazeState,
                    style = HazeDefaults.style(
                        backgroundColor = MaterialTheme.colorScheme.surface,
                        blurRadius = 20.dp,
                    )
                )
                .background(MaterialTheme.colorScheme.surfaceTint)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            locales.forEach { locale ->
                RadioButtonWithText(
                    modifier = Modifier.fillMaxWidth(),
                    title = locale.text,
                    isSelected = locale == selectedLocale,
                    onClick = {
                        onLocaleSelected(locale)
                        scope.launch {
                            delay(150)
                            onDismissRequest()
                        }
                    },
                )
            }
        }
    }
}
