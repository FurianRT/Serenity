package com.furianrt.security.internal.domain

import com.furianrt.security.internal.domain.repositories.SecurityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class GetPartiallyHiddenEmailUseCase @Inject constructor(
    private val securityRepository: SecurityRepository,
) {
    operator fun invoke(): Flow<String?> = securityRepository.getPinRecoveryEmail().map { email ->
        email
            ?.split("@")
            ?.mapIndexed { index, part ->
                if (index == 0) {
                    part.take(2) + "****" + part.takeLast(2)
                } else {
                    part
                }
            }
            ?.joinToString(separator = "@")
    }
}