package com.furianrt.toolspanel.internal.ui.bullet

import androidx.lifecycle.ViewModel
import com.furianrt.toolspanel.internal.domain.BulletListHolder
import com.furianrt.toolspanel.internal.entities.BulletEntry
import com.furianrt.toolspanel.internal.ui.bullet.extensions.toBulletListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
internal class BulletViewModel @Inject constructor(
    private val bulletListHolder: BulletListHolder,
) : ViewModel() {

    val state: StateFlow<BulletPanelUiState> = MutableStateFlow(buildInitialState())

    private val _effect = MutableSharedFlow<BulletPanelEffect>(extraBufferCapacity = 5)
    val effect = _effect.asSharedFlow()

    fun onEvent(evet: BulletPanelEvent) {
        when (evet) {
            is BulletPanelEvent.OnCloseClick -> _effect.tryEmit(BulletPanelEffect.ClosePanel)
        }
    }

    private fun buildInitialState() = BulletPanelUiState(
        items = bulletListHolder.getBulletListEntries()
            .map(BulletEntry::toBulletListItem),
    )
}