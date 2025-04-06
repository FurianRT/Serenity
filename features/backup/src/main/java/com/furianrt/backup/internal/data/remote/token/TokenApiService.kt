package com.furianrt.backup.internal.data.remote.token

import retrofit2.http.Body
import retrofit2.http.POST

private const val GET_ACCESS_TOKEN_METHOD = "/token"

internal interface TokenApiService {
    @POST(GET_ACCESS_TOKEN_METHOD)
    suspend fun refreshAccessToken(@Body request: TokenRequest): TokenResponse
}