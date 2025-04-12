package com.furianrt.backup.internal.data.remote.google.token

import com.furianrt.backup.internal.data.local.BackupDataStore
import com.furianrt.backup.internal.domain.usecases.AuthorizeUseCase
import com.furianrt.backup.internal.domain.entities.AuthResult
import com.furianrt.domain.repositories.ProfileRepository
import dagger.Lazy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class TokenAuthenticator @Inject constructor(
    private val authorizeUseCase: Lazy<AuthorizeUseCase>,
    private val backupDataStore: BackupDataStore,
    private val profileRepository: ProfileRepository,
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) {
            return null
        }
        return when (val authResult = tryAuthorize()) {
            is AuthResult.Failure -> null
            is AuthResult.Resolution -> {
                deleteBackupProfile()
                null
            }

            is AuthResult.Success -> {
                saveAccessToken(authResult.accessToken)
                response.request.newBuilder()
                    .header("Authorization", "Bearer ${authResult.accessToken}")
                    .build()
            }
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

    private fun tryAuthorize(): AuthResult = try {
        runBlocking { authorizeUseCase.get().invoke() }
    } catch (e: InterruptedException) {
        e.printStackTrace()
        AuthResult.Failure(e)
    }

    private fun saveAccessToken(token: String) {
        try {
            runBlocking { backupDataStore.updateGoogleAccessToken(token) }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun deleteBackupProfile() {
        try {
            runBlocking {
                profileRepository.getBackupProfile().first()?.let { profile ->
                    profileRepository.deleteBackupProfile(profile.email)
                }
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}
