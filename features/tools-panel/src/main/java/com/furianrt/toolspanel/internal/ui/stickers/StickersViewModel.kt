package com.furianrt.toolspanel.internal.ui.stickers

import androidx.compose.foundation.pager.PagerState
import androidx.lifecycle.ViewModel
import com.furianrt.toolspanel.internal.domain.StickersHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
internal class StickersViewModel @Inject constructor(
    stickersHolder: StickersHolder,
) : ViewModel() {

    private val packs = stickersHolder.getStickersPacks()
    private val pagerState = PagerState(pageCount = packs::size)

    val state: StateFlow<StickersPanelUiState> = MutableStateFlow(
        StickersPanelUiState(packs, pagerState)
    )

    private val _effect = MutableSharedFlow<StickersPanelEffect>(extraBufferCapacity = 5)
    val effect = _effect.asSharedFlow()

    fun onEvent(event: StickersPanelEvent) {
        when (event) {
            is StickersPanelEvent.OnCloseClick -> _effect.tryEmit(StickersPanelEffect.ClosePanel)
            is StickersPanelEvent.OnStickerSelected -> {
                _effect.tryEmit(StickersPanelEffect.SelectSticker(event.sticker))
            }

            is StickersPanelEvent.OnTitleStickerPackClick -> {
                _effect.tryEmit(StickersPanelEffect.ScrollContentToIndex(event.index))
            }

            is StickersPanelEvent.OnKeyboardClick -> {
                _effect.tryEmit(StickersPanelEffect.ShowKeyboard)
            }
        }
    }
}