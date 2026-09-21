package com.furianrt.billing.internal.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.furianrt.billing.R
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground
import com.furianrt.uikit.R as uiR

@Composable
internal fun Logo(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            modifier = Modifier
                .padding(end = 2.dp)
                .alpha(0.85f)
                .size(120.dp),
            painter = painterResource(uiR.drawable.app_logo_big),
            tint = MaterialTheme.colorScheme.primaryContainer,
            contentDescription = null,
        )
        Spacer(Modifier.size(24.dp))
        Text(
            text = stringResource(uiR.string.title_serenity_plus),
            style = MaterialTheme.typography.titleLarge,
            fontSize = 22.sp,
        )
        Spacer(Modifier.size(14.dp))
        Text(
            modifier = Modifier.alpha(0.7f),
            text = stringResource(R.string.billing_subscriptions_description),
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
        )
    }

}

@PreviewWithBackground
@Composable
private fun Preview() {
    SerenityTheme {
        Logo()
    }
}
