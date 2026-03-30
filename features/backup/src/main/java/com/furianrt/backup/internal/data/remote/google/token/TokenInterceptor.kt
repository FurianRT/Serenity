package com.furianrt.backup.internal.data.remote.google.token

import com.furianrt.backup.internal.data.local.BackupDataStore
import com.furianrt.common.ErrorTracker
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class TokenInterceptor @Inject constructor(
    private val backupDataStore: BackupDataStore,
    private val errorTracker: ErrorTracker,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = try {
            runBlocking { backupDataStore.getGoogleAccessToken().first() }
        } catch (e: InterruptedException) {
            e.printStackTrace()
            errorTracker.trackNonFatalError(e)
            null
        }
        val requestBuilder = chain.request().newBuilder()
        if (accessToken != null) {
            requestBuilder.header("Authorization", "Bearer $accessToken")
        }

        return chain.proceed(requestBuilder.build())
    }
}