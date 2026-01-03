package com.furianrt.serenity.domain

import com.furianrt.domain.repositories.AppInfoRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

internal class IncrementLaunchCountUseCase @Inject constructor(
    private val appInfoRepository: AppInfoRepository,
) {
    suspend operator fun invoke() {
        val launchCount = appInfoRepository.getAppLaunchCount().first()
        appInfoRepository.updateAppLaunchCount(launchCount + 1)
    }
}