package com.furianrt.backup.internal.data.remote.token

import com.furianrt.backup.BuildConfig
import com.furianrt.backup.internal.domain.repositories.BackupRepository
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

@ViewModelScoped
internal class TokenAuthenticator @Inject constructor(
    private val backupRepository: BackupRepository,
    private val tokenApiService: TokenApiService,
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) return null

        val oldAccessToken = try {
            runBlocking { backupRepository.getGoogleAccessToken().first() }
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        }

        if (oldAccessToken == null) {
            return null
        }

        val newAccessToken = try {
            runBlocking {
                tokenApiService.refreshAccessToken(
                    TokenRequest(
                        clientId = BuildConfig.OAUTH_CLIENT_ID,
                        refreshToken = oldAccessToken,
                        grantType = TokenRequest.GrandType.REFRESH_TOKEN,
                    )
                ).accessToken?.also { token ->
                    backupRepository.updateGoogleAccessToken(token)
                }
            }
        } catch (e: Exception) {
            null
        }

        return newAccessToken?.let { token ->
            response.request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var current = response.priorResponse
        while (current != null) {
            count++
            current = current.priorResponse
        }
        return count
    }
}
