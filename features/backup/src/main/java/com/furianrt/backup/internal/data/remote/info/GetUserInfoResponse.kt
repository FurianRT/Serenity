package com.furianrt.backup.internal.data.remote.info

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal class GetUserInfoResponse(
    @SerialName("emailAddresses")
    val emailAddresses: List<EmailAddress>?,
) {
    @Serializable
    class EmailAddress(
        @SerialName("value")
        val value: String,

        @SerialName("metadata")
        val metadata: Metadata? = null,
    )

    @Serializable
    class Metadata(
        @SerialName("primary")
        val isPrimary: Boolean? = null,
    )
}

internal val GetUserInfoResponse.primaryEmail: String?
    get() {
        val primaryAddress = emailAddresses?.find { it.metadata?.isPrimary == true }?.value
        val firstAddress = emailAddresses?.firstOrNull()?.value
        return primaryAddress ?: firstAddress
    }
