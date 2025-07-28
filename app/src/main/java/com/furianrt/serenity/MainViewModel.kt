package com.furianrt.serenity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.domain.entities.NoteFontFamily
import com.furianrt.domain.repositories.AppearanceRepository
import com.furianrt.security.api.LockAuthorizer
import com.furianrt.serenity.extensions.toNoteFont
import com.furianrt.uikit.entities.UiThemeColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
internal class MainViewModel @Inject constructor(
    appearanceRepository: AppearanceRepository,
    private val lockAuthorizer: LockAuthorizer,
) : ViewModel() {

    val state = combine(
        appearanceRepository.getAppThemeColorId(),
        appearanceRepository.getAppFont(),
        lockAuthorizer.isAuthorized(),
        ::buildState,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MainState(),
    )

    fun onEvent(event: MainEvent) {
        when (event) {
            is MainEvent.OnUnlockScreenRequest -> lockAuthorizer.authorize()
        }
    }

    private fun buildState(
        appColorId: String?,
        appFont: NoteFontFamily,
        isAuthorized: Boolean,
    ) = MainState(
        appColor = UiThemeColor.fromId(appColorId),
        appFont = appFont.toNoteFont(),
        isScreenLocked = !isAuthorized,
    )
}