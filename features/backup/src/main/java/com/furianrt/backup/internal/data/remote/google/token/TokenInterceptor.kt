package com.furianrt.backup.internal.data.remote.google.token

import com.furianrt.backup.internal.data.local.BackupDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class TokenInterceptor @Inject constructor(
    private val backupDataStore: BackupDataStore,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = try {
            runBlocking { backupDataStore.getGoogleAccessToken().first() }
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }
        val requestBuilder = chain.request().newBuilder()
        if (accessToken != null) {
            requestBuilder.header("Authorization", "Bearer $accessToken")
        }

        return chain.proceed(requestBuilder.build())
    }
}