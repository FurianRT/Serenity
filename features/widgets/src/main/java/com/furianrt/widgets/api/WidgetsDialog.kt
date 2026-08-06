package com.furianrt.widgets.api

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import com.furianrt.uikit.entities.UiThemeColor
import com.furianrt.uikit.entities.colorScheme
import com.furianrt.uikit.extensions.clickableNoRipple
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.LocalAuth
import com.furianrt.widgets.R
import com.furianrt.widgets.internal.widgets.AllActionsWidgetReceiver
import kotlinx.coroutines.launch
import com.furianrt.uikit.R as uiR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetsDialog(
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
        Content(onDismissRequest)
    }
}

@Composable
private fun Content(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val theme = UiThemeColor.STORM_IN_THE_NIGHT_BLUE_LIGHT
    SerenityTheme(
        colorScheme = theme.colorScheme,
        isLightTheme = theme.isLight,
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp)
                .clickableNoRipple {
                    val provider = ComponentName(context, AllActionsWidgetReceiver::class.java)
                    val appWidgetManager = AppWidgetManager.getInstance(context)
                    appWidgetManager.requestPinAppWidget(provider, null, null)
                    onDismissRequest()
                },
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AllActionsWidget()
            Text(
                text = stringResource(R.string.widgets_add_widget_title),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun AllActionsWidget(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 12.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            modifier = Modifier
                .padding(start = 8.dp, end = 12.dp)
                .size(30.dp),
            painter = painterResource(uiR.drawable.app_logo_small),
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = null,
        )
        Icon(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape,
                )
                .padding(12.dp),
            painter = painterResource(uiR.drawable.ic_add),
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = null,
        )
        Icon(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.tertiary,
                    shape = RoundedCornerShape(16.dp),
                )
                .padding(12.dp),
            painter = painterResource(uiR.drawable.ic_camera),
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = null,
        )
        Icon(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.tertiary,
                    shape = RoundedCornerShape(16.dp),
                )
                .padding(12.dp),
            painter = painterResource(uiR.drawable.ic_video),
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = null,
        )
        Icon(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.tertiary,
                    shape = RoundedCornerShape(16.dp),
                )
                .padding(12.dp),
            painter = painterResource(uiR.drawable.ic_microphone),
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = null,
        )
    }
}

@Preview
@Composable
private fun Preview() {
    Content(
        onDismissRequest = {},
    )
}
