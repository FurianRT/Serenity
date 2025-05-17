package com.furianrt.uikit.utils

import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri

object IntentCreator {
    enum class MediaType {
        IMAGE, VIDEO
    }

    fun emailIntent(
        email: String,
        subject: String? = null,
        text: String? = null,
    ): Result<Intent> = runCatching {
        val intent = Intent(Intent.ACTION_SENDTO, "mailto:".toUri()).apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            subject?.let { putExtra(Intent.EXTRA_SUBJECT, it) }
            text?.let { putExtra(Intent.EXTRA_TEXT, it) }
        }
        Intent.createChooser(intent, null)
    }

    fun mediaShareIntent(
        uri: Uri,
        mediaType: MediaType,
    ): Result<Intent> = runCatching {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = when (mediaType) {
                MediaType.IMAGE -> "image/*"
                MediaType.VIDEO -> "video/*"
            }
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            putExtra(Intent.EXTRA_STREAM, uri)
        }
        Intent.createChooser(intent, null)
    }
}