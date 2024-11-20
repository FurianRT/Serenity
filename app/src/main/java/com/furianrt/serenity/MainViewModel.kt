package com.furianrt.serenity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.domain.managers.LockManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
internal class MainViewModel @Inject constructor(
    private val lockManager: LockManager,
) : ViewModel() {

    val state = lockManager.isAuthorized().map { isAuthorized ->
        MainState(isScreenLocked = !isAuthorized)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MainState(),
    )

    fun onEvent(event: MainEvent) {
        when (event) {
            is MainEvent.OnUnlockScreenRequest -> lockManager.authorize()
        }
    }
}