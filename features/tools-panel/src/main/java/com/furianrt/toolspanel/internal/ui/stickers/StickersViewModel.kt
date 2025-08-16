package com.furianrt.toolspanel.internal.ui.stickers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.toolspanel.internal.domain.StickersHolder
import com.furianrt.toolspanel.internal.entities.StickerPack
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
internal class StickersViewModel @Inject constructor(
    private val stickersHolder: StickersHolder,
) : ViewModel() {

    private val selectedPageIndex = MutableStateFlow(0)

    val state: StateFlow<StickersPanelUiState> = combine(
        flow { emit(stickersHolder.getStickersPacks()) },
        selectedPageIndex,
    ) { packs, pageIndex ->
        buildState(
            packs = packs,
            pageIndex = pageIndex,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = StickersPanelUiState(),
    )

    private val _effect = MutableSharedFlow<StickersPanelEffect>(extraBufferCapacity = 5)
    val effect = _effect.asSharedFlow()

    fun onEvent(event: StickersPanelEvent) {
        when (event) {
            is StickersPanelEvent.OnCloseClick -> _effect.tryEmit(StickersPanelEffect.ClosePanel)
            is StickersPanelEvent.OnStickerSelected -> {
                _effect.tryEmit(StickersPanelEffect.SelectSticker(event.sticker))
            }

            is StickersPanelEvent.OnStickersPageChange -> selectedPageIndex.update { event.index }
            is StickersPanelEvent.OnTitleStickerPackClick -> {
                selectedPageIndex.update { event.index }
                _effect.tryEmit(StickersPanelEffect.ScrollContentToIndex(event.index))
            }

            is StickersPanelEvent.OnKeyboardClick -> {
                _effect.tryEmit(StickersPanelEffect.ShowKeyboard)
            }
        }
    }

    private fun buildState(
        packs: ImmutableList<StickerPack>,
        pageIndex: Int,
    ) = StickersPanelUiState(
        packs = packs,
        selectedPackIndex = pageIndex,
    )
}