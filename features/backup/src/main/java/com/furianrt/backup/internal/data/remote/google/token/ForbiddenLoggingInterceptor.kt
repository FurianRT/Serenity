package com.furianrt.backup.internal.data.remote.google.token

import com.furianrt.common.ErrorTracker
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

private class ForbiddenException(message: String) : Exception(message)

@Singleton
internal class ForbiddenLoggingInterceptor @Inject constructor(
    private val errorTracker: ErrorTracker,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        if (response.code == 403) {
            val message = buildString {
                appendLine("HTTP 403")
                appendLine("URL: ${request.url}")
                appendLine("Body: ${response.peekBody(1024 * 100).string()}")
            }
            errorTracker.trackNonFatalError(ForbiddenException(message))
        }
        return response
    }
}