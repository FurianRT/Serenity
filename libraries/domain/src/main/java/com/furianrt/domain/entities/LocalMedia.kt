package com.furianrt.domain.entities

import android.net.Uri

class LocalMedia(
    val uri: Uri,
    val name: String,
    val type: Type,
) {
    enum class Type {
        IMAGE, VIDEO
    }
}