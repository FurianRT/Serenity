package com.furianrt.backup.internal.data.remote.token

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal class TokenRequest(
    @SerialName("client_id")
    val clientId: String,

    @SerialName("refresh_token")
    val refreshToken: String,

    @SerialName("grant_type")
    val grantType: GrandType,
) {
    @Serializable
    enum class GrandType {
        @SerialName("refresh_token")
        REFRESH_TOKEN,
    }
}