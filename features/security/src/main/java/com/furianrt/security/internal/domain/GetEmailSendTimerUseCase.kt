package com.furianrt.security.internal.domain

import com.furianrt.security.internal.domain.repositories.SecurityRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transformLatest
import javax.inject.Inject
import kotlin.math.max

private const val SEND_EMAIL_INTERVAL = 1000L * 60L * 2L // 2 min

internal class GetEmailSendTimerUseCase @Inject constructor(
    private val securityRepository: SecurityRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<Long> = securityRepository.getLastEmailSendTime()
        .transformLatest { lastEmailSendTime ->
            do {
                val timeDiff = System.currentTimeMillis() - lastEmailSendTime
                val remaining = max(0L, SEND_EMAIL_INTERVAL - timeDiff)
                emit(remaining)
                delay(1000L)
            } while (remaining != 0L)
        }
}