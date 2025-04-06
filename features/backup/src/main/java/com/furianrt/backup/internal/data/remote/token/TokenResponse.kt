package com.furianrt.backup.internal.data.remote.token

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal class TokenResponse(
    @SerialName("access_token")
    val accessToken: String? = null,

    @SerialName("expires_in")
    val expiresIn: Long? = null,

    @SerialName("refresh_token")
    val refreshToken: String? = null,

    @SerialName("token_type")
    val tokenType: String? = null,

    @SerialName("scope")
    val scope: String? = null,
)