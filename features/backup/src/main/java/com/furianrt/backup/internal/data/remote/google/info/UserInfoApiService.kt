package com.furianrt.backup.internal.data.remote.google.info

import retrofit2.http.GET
import retrofit2.http.Query

private const val GET_MY_INFO_METHOD = "/v1/people/me"

internal interface UserInfoApiService {
    @GET(GET_MY_INFO_METHOD)
    suspend fun getUserProfile(
        @Query("personFields") personFields: String = "emailAddresses",
    ): GetUserInfoResponse
}