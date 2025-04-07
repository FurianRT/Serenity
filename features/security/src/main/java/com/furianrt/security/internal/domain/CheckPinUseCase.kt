package com.furianrt.security.internal.domain

import com.furianrt.security.internal.domain.repositories.SecurityRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

internal class CheckPinUseCase @Inject constructor(
    private val securityRepository: SecurityRepository
) {
    suspend operator fun invoke(pin: String): Boolean {
        return securityRepository.getPin().first() == pin
    }
}