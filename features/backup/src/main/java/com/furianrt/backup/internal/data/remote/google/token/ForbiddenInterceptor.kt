package com.furianrt.backup.internal.data.remote.google.token

import com.furianrt.backup.internal.domain.usecases.SignOutUseCase
import com.furianrt.common.ErrorTracker
import com.furianrt.domain.repositories.ProfileRepository
import dagger.Lazy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

private class ForbiddenException(message: String) : Exception(message)

@Singleton
internal class ForbiddenInterceptor @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val signOutUseCase: Lazy<SignOutUseCase>,
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
            try {
                runBlocking {
                    val email = profileRepository.getBackupProfile().first()?.email
                    signOutUseCase.get().invoke(email = email, accessToken = null)
                }
            } catch (e: InterruptedException) {
                errorTracker.trackNonFatalError(e)
                e.printStackTrace()
            }
        }
        return response
    }
}