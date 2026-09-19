package com.furianrt.billing.internal.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.furianrt.billing.R
import com.furianrt.uikit.theme.SerenityTheme
import com.furianrt.uikit.utils.PreviewWithBackground

@Composable
internal fun TermsWarning(
    onTermsClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val fulltext = stringResource(R.string.billing_terms_warning_text)
    val termsPart = stringResource(R.string.billing_terms_warning_clickable_part_terms_of_service)
    val privacyPolicyPart =
        stringResource(R.string.billing_terms_warning_clickable_part_privacy_policy)
    val textColor = MaterialTheme.colorScheme.onSurface

    val annotatedString = remember(
        textColor,
        fulltext,
        termsPart,
        privacyPolicyPart,
        onTermsClick,
        onPrivacyPolicyClick,
    ) {
        buildAnnotatedString {
            append(fulltext)
            val termsIndex = fulltext.indexOf(termsPart)
            val privacyPolicyIndex = fulltext.indexOf(privacyPolicyPart)
            val linkStyles = TextLinkStyles(
                style = SpanStyle(
                    color = textColor.copy(alpha = 0.8f),
                    fontWeight = FontWeight.ExtraBold,
                )
            )
            addLink(
                clickable = LinkAnnotation.Clickable(
                    tag = "TERMS",
                    styles = linkStyles,
                    linkInteractionListener = { onTermsClick() },
                ),
                start = termsIndex,
                end = termsIndex + termsPart.length,
            )
            addLink(
                clickable = LinkAnnotation.Clickable(
                    tag = "PRIVACY",
                    styles = linkStyles,
                    linkInteractionListener = { onPrivacyPolicyClick() },
                ),
                start = privacyPolicyIndex,
                end = privacyPolicyIndex + privacyPolicyPart.length,
            )
        }
    }
    Text(
        modifier = modifier,
        text = annotatedString,
        style = MaterialTheme.typography.labelSmall,
        color = textColor.copy(alpha = 0.5f),
        textAlign = TextAlign.Center,
        fontSize = MaterialTheme.typography.labelSmall.fontSize * 0.9f,
    )
}


@PreviewWithBackground
@Composable
private fun Preview() {
    SerenityTheme {
        TermsWarning(
            onTermsClick = {},
            onPrivacyPolicyClick = {},
        )
    }
}
