package com.furianrt.onboarding.internal.ui.theme

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.furianrt.onboarding.R
import com.furianrt.onboarding.internal.ui.theme.elements.ThemePreviewPage
import com.furianrt.onboarding.internal.ui.theme.model.ThemeTab
import com.furianrt.uikit.components.TabsSelector
import com.furianrt.uikit.entities.UiThemeColor
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.absoluteValue

@Composable
internal fun ThemeScreen(
    state: ThemeScreenState,
    modifier: Modifier = Modifier,
) {
    val viewModel: ThemeViewModel = hiltViewModel()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    when (val uiState = viewModel.state.collectAsStateWithLifecycle().value) {
        is ThemeState.Loading -> Box(
            modifier = Modifier.fillMaxSize(),
        )

        is ThemeState.Success -> {
            val pagerState = rememberPagerState(
                initialPage = getCenterPageIndex(uiState.themes.size) + uiState.initialPageIndex,
                pageCount = Int::MAX_VALUE,
            )
            LaunchedEffect(Unit) {
                snapshotFlow { pagerState.currentPage }
                    .collect { currentPage ->
                        val realIndex = currentPage % uiState.themes.count()
                        state.selectedTheme = uiState.themes[realIndex]
                        viewModel.onEvent(ThemeEvent.OnThemeChange(state.selectedTheme))
                    }
            }
            LaunchedEffect(Unit) {
                viewModel.effect
                    .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                    .collectLatest { effect ->
                        when (effect) {
                            is ThemeEffect.ScrollToTheme -> pagerState.scrollToPage(
                                getCenterPageIndex(uiState.themes.size) + effect.index
                            )
                        }
                    }
            }
            Content(
                modifier = modifier,
                pagerState = pagerState,
                uiState = uiState,
                onEvent = viewModel::onEvent,
            )
        }
    }
}

@Composable
private fun Content(
    uiState: ThemeState.Success,
    onEvent: (event: ThemeEvent) -> Unit,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
) {

    val tabs = remember { uiState.tabs.map { it.title } }
    Column(
        modifier = modifier.padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.onboarding_theme_page_title),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(32.dp))
        TabsSelector(
            tabs = tabs,
            selectedIndex = uiState.tabs.indexOf(uiState.selectedTab),
            onClick = { onEvent(ThemeEvent.OnThemeTabClick) },
        )
        Spacer(Modifier.height(24.dp))
        HorizontalPager(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            state = pagerState,
            pageSize = PageSize.Fixed(200.dp),
            pageSpacing = 16.dp,
            beyondViewportPageCount = 1,
            snapPosition = SnapPosition.Center,
        ) { page ->
            ThemePreviewPage(
                modifier = Modifier.graphicsLayer {
                    val distance = pagerState.getOffsetDistanceInPages(page)
                    val scale = lerp(1f, 0.85f, distance.absoluteValue.coerceAtMost(1f))
                    scaleX = scale
                    scaleY = scale
                },
                color = uiState.themes[page % uiState.themes.count()],
            )
        }
    }
}

private fun getCenterPageIndex(realCount: Int): Int {
    val middle = Int.MAX_VALUE / 2
    return middle - (middle % realCount)
}

@PreviewWithBackground
@Composable
private fun Preview() {
    SerenityTheme {
        Content(
            modifier = Modifier.height(670.dp),
            pagerState = rememberPagerState(
                initialPage = 6,
                pageCount = { 10 }
            ),
            uiState = ThemeState.Success(
                initialPageIndex = 1,
                themes = UiThemeColor.getDarkThemesList() + UiThemeColor.getLightThemesList(),
                tabs = listOf(ThemeTab.Dark("Dark"), ThemeTab.Light("Light")),
                selectedTab = ThemeTab.Dark("Dark"),
            ),
            onEvent = {},
        )
    }
}
