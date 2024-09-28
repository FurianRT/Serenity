package com.furianrt.settings.internal.ui

import androidx.annotation.IntRange
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.furianrt.settings.R
import com.furianrt.settings.internal.ui.composables.Toolbar
import com.furianrt.uikit.extensions.applyIf
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.extensions.drawBottomShadow
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground

@Composable
internal fun SettingsScreen(
    onCloseRequest: () -> Unit,
) {
    val viewModel: SettingsViewModel = hiltViewModel()
    val uiState = viewModel.state.collectAsStateWithLifecycle().value

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SettingsEffect.CloseScreen -> onCloseRequest()
            }
        }
    }

    ScreenContent(uiState, viewModel::onEvent)
}

@Composable
private fun ScreenContent(
    uiState: SettingsUiState,
    onEvent: (event: SettingsEvent) -> Unit,
) {
    val scrollState = rememberScrollState()
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.systemBarsPadding()) {
            Toolbar(
                modifier = Modifier.drawBehind {
                    if (scrollState.canScrollBackward) {
                        drawBottomShadow(elevation = 8.dp)
                    }
                },
                onBackClick = { onEvent(SettingsEvent.OnButtonBackClick) },
            )
            when (uiState) {
                is SettingsUiState.Success -> SuccessScreen(uiState, scrollState, onEvent)
                is SettingsUiState.Loading -> LoadingScreen()
            }
        }
    }
}

@Composable
private fun SuccessScreen(
    uiState: SettingsUiState.Success,
    scrollState: ScrollState,
    onEvent: (event: SettingsEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        GeneralButton(
            title = stringResource(id = R.string.settings_security_title),
            iconPainter = painterResource(id = R.drawable.ic_lock),
            onClick = {},
        )
        GeneralButton(
            title = stringResource(id = R.string.settings_backup_title),
            iconPainter = painterResource(id = R.drawable.ic_cloud),
            onClick = {},
        )
        ThemeSelector(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            onSelected = {},
        )
        LanguageSelector(
            language = "English",
            onClick = {},
        )
        Rating(
            rating = 4,
            onSelected = {},
        )
        GeneralButton(
            modifier = Modifier.padding(top = 16.dp),
            title = stringResource(id = R.string.settings_donate_title),
            iconPainter = painterResource(id = R.drawable.ic_hart),
            onClick = {},
        )
        GeneralButton(
            title = stringResource(id = R.string.settings_feedback_title),
            iconPainter = painterResource(id = R.drawable.ic_mail),
            onClick = {},
        )
        GeneralButton(
            title = stringResource(id = R.string.settings_about_title),
            iconPainter = painterResource(id = R.drawable.ic_info),
            onClick = {},
        )
    }
}

@Composable
private fun GeneralButton(
    title: String,
    iconPainter: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            painter = iconPainter,
            contentDescription = title,
            tint = Color.Unspecified,
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun ThemeSelector(
    onSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(4.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_theme),
                contentDescription = stringResource(id = R.string.settings_theme_title),
                tint = Color.Unspecified,
            )
            Text(
                text = stringResource(id = R.string.settings_theme_title),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            val shape = RoundedCornerShape(16.dp)
            val colors = listOf(
                0xFF20454E,
                0xFF193727,
                0xFF0F2D5F,
                0xFF086375,
                0xFF000000,
            )
            repeat(5) { index ->
                Spacer(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(shape)
                        .background(Color(colors[index]))
                        .applyIf(index == 1) { Modifier.border(1.dp, Color.White, shape) }
                        .clickable(onClick = onSelected)
                )
            }
        }
    }
}

@Composable
private fun LanguageSelector(
    language: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_language),
            contentDescription = stringResource(id = R.string.settings_language_title),
            tint = Color.Unspecified,
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(id = R.string.settings_language_title),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                modifier = Modifier.alpha(0.5f),
                text = language,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }

}

@Composable
private fun Rating(
    @IntRange(0, 5) rating: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(
            text = stringResource(id = R.string.settings_rate_us_title),
            style = MaterialTheme.typography.bodyMedium,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            repeat(5) { index ->
                Icon(
                    modifier = Modifier.clickableNoRipple { onSelected(index + 1) },
                    painter = if (rating >= index + 1) {
                        painterResource(id = R.drawable.ic_star_filled)
                    } else {
                        painterResource(id = R.drawable.ic_star_outlined)
                    },
                    contentDescription = (index + 1).toString(),
                    tint = Color.Unspecified,
                )
            }
        }
    }
}

@Composable
private fun LoadingScreen(
) {
    Box(modifier = Modifier) {
    }
}

@Composable
@PreviewWithBackground
private fun ScreenContentPreview() {
    SerenityTheme {
        ScreenContent(
            uiState = SettingsUiState.Success,
            onEvent = {},
        )
    }
}
