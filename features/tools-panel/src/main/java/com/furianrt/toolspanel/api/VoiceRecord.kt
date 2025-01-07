package com.furianrt.toolspanel.api

import android.net.Uri

data class VoiceRecord(
    val id: String,
    val uri: Uri,
    val duration: Int,
    val volume: List<Float>,
)