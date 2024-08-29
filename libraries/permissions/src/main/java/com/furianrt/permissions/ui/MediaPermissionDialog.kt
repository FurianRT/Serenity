package com.furianrt.permissions.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.furianrt.permissions.R
import com.furianrt.uikit.theme.SerenityTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaPermissionDialog(
    onDismissRequest: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    BasicAlertDialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            tonalElevation = AlertDialogDefaults.TonalElevation,
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Header()
                Title(modifier = Modifier.padding(16.dp))
                Row(
                    modifier = Modifier.padding(end = 16.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ActionButton(
                        title = stringResource(id = R.string.media_permission_not_now_title),
                        onClick = onDismissRequest,
                    )
                    ActionButton(
                        title = stringResource(id = R.string.media_permission_setting_title),
                        onClick = {
                            onSettingsClick()
                            onDismissRequest()
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun Header(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_folder),
            tint = Color.Unspecified,
            contentDescription = null,
        )
    }
}

@Composable
private fun Title(modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = buildAnnotatedString {
            val title = stringResource(R.string.media_permission_message)
            val boldPartOne = stringResource(R.string.media_permission_message_bold_part_1)
            val boldPartTwo = stringResource(R.string.media_permission_message_bold_part_2)
            val boldPartOneIndex = title.indexOf(boldPartOne)
            val boldPartTwoIndex = title.indexOf(boldPartTwo)
            append(title)
            addStyle(
                style = SpanStyle(fontWeight = FontWeight.ExtraBold),
                start = boldPartOneIndex,
                end = boldPartOneIndex + boldPartOne.length,
            )
            addStyle(
                style = SpanStyle(fontWeight = FontWeight.ExtraBold),
                start = boldPartTwoIndex,
                end = boldPartTwoIndex + boldPartTwo.length,
            )
        },
        style = MaterialTheme.typography.bodyMedium,
    )
}

@Composable
private fun ActionButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TextButton(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
        )
    }
}

@Preview
@Composable
private fun MediaPermissionDialogPreview() {
    SerenityTheme {
        MediaPermissionDialog(
            onDismissRequest = {},
            onSettingsClick = {},
        )
    }
}
