package com.furianrt.uikit.utils

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

object EmailSender {
    fun send(
        context: Context,
        email: String,
        subject: String? = null,
        text: String? = null,
    ): Result<Unit> = runCatching {
        val intent = Intent(Intent.ACTION_SENDTO, "mailto:".toUri()).apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            subject?.let { putExtra(Intent.EXTRA_SUBJECT, it) }
            text?.let { putExtra(Intent.EXTRA_TEXT, it) }
        }
        context.startActivity(Intent.createChooser(intent, null))
    }
}