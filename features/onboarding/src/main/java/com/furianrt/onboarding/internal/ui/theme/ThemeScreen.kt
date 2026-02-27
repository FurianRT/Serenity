package com.furianrt.onboarding.internal.ui.theme

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.furianrt.onboarding.R
import com.furianrt.onboarding.internal.ui.container.LocalHazeState
import com.furianrt.onboarding.internal.ui.theme.elements.ThemePreviewPage
import com.furianrt.uikit.entities.UiThemeColor
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import kotlin.math.absoluteValue

@Composable
internal fun ThemeScreen(
    state: ThemeScreenState,
    modifier: Modifier = Modifier,
) {
    val viewModel: ThemeViewModel = hiltViewModel()
    val uiState = viewModel.state.collectAsStateWithLifecycle().value

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        when (uiState.content) {
            is ThemeState.Content.Loading -> Box(
                modifier = Modifier.fillMaxSize(),
            )

            is ThemeState.Content.Success -> {
                val pagerState = rememberPagerState(
                    initialPage = getCenterPageIndex(uiState.content.themes.size) +
                            uiState.content.initialPageIndex,
                    pageCount = Int::MAX_VALUE,
                )
                LaunchedEffect(Unit) {
                    snapshotFlow { pagerState.currentPage }
                        .collect { currentPage ->
                            val realIndex = currentPage % uiState.content.themes.count()
                            state.selectedTheme = uiState.content.themes[realIndex]
                        }
                }
                Content(
                    pagerState = pagerState,
                    uiState = uiState.content,
                )
            }
        }
    }
}

@Composable
private fun Content(
    uiState: ThemeState.Content.Success,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(top = 40.dp, bottom = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .hazeEffect(
                    state = LocalHazeState.current,
                    style = HazeDefaults.style(
                        backgroundColor = MaterialTheme.colorScheme.surface,
                        blurRadius = 16.dp,
                        noiseFactor = 0f,
                        tint = HazeTint(Color.Transparent),
                    ),
                )
                .padding(vertical = 2.dp, horizontal = 4.dp),
            text = stringResource(R.string.onboarding_theme_page_title),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )
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
            uiState = ThemeState.Content.Success(
                initialPageIndex = 1,
                themes = UiThemeColor.getDarkThemesList() + UiThemeColor.getLightThemesList(),
            ),
        )
    }
}
