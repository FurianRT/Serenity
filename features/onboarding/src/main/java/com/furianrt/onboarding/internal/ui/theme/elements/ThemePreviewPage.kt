package com.furianrt.onboarding.internal.ui.theme.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.furianrt.uikit.entities.UiThemeColor
import com.furianrt.uikit.entities.colorScheme
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.R as uiR

@Composable
internal fun ThemePreviewPage(
    color: UiThemeColor,
    modifier: Modifier = Modifier,
) {
    SerenityTheme(
        colorScheme = color.colorScheme,
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp),
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(16.dp),
                )
                .padding(vertical = 24.dp),
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 5.dp),
                verticalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                Toolbar(
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
                Spacer(Modifier.height(4.dp))
                SkeletonItem1()
                SkeletonItem2()
                SkeletonItem1()
                SkeletonItem2()
            }
            ActionButton(
                modifier = Modifier
                    .padding(end = 18.dp)
                    .align(Alignment.BottomEnd),
            )
        }
    }
}

@Composable
private fun Toolbar(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(9.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .height(18.dp)
                .weight(1f)
                .background(MaterialTheme.colorScheme.background, RoundedCornerShape(16.dp)),
        )
        Icon(
            modifier = Modifier.size(14.dp),
            painter = painterResource(uiR.drawable.ic_settings),
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = null,
        )
    }
}

@Composable
private fun ActionButton(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.primaryContainer, shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            modifier = Modifier
                .padding(9.dp)
                .size(14.dp),
            painter = painterResource(uiR.drawable.ic_add),
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            contentDescription = null,
        )
    }
}

@Composable
private fun SkeletonItem1(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(6.dp))
            .padding(vertical = 6.dp, horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Box(
            modifier = Modifier
                .height(10.dp)
                .fillMaxWidth(fraction = 0.9f)
                .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(16.dp)),
        )
        Box(
            modifier = Modifier
                .height(10.dp)
                .fillMaxWidth(fraction = 0.6f)
                .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(16.dp)),
        )
        Box(
            modifier = Modifier
                .height(10.dp)
                .fillMaxWidth(fraction = 0.8f)
                .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(16.dp)),
        )
        Row(
            modifier = Modifier.padding(end = 64.dp, top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Box(
                modifier = Modifier
                    .height(14.dp)
                    .weight(0.2f)
                    .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(16.dp)),
            )
            Box(
                modifier = Modifier
                    .height(14.dp)
                    .weight(0.3f)
                    .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(16.dp)),
            )
            Box(
                modifier = Modifier
                    .height(14.dp)
                    .weight(0.5f)
                    .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(16.dp)),
            )
        }
    }
}

@Composable
private fun SkeletonItem2(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(6.dp))
            .padding(vertical = 6.dp, horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Box(
            modifier = Modifier
                .height(10.dp)
                .fillMaxWidth(fraction = 0.9f)
                .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(16.dp)),
        )
        Box(
            modifier = Modifier
                .height(10.dp)
                .fillMaxWidth(fraction = 0.5f)
                .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(16.dp)),
        )
        Box(
            modifier = Modifier
                .height(10.dp)
                .fillMaxWidth(fraction = 0.7f)
                .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(16.dp)),
        )
    }
}

@Preview
@Composable
private fun Preview() {
    ThemePreviewPage(
        modifier = Modifier.size(height = 420.dp, width = 200.dp),
        color = UiThemeColor.DISTANT_CASTLE_GREEN,
    )
}