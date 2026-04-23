package com.furianrt.toolspanel.internal.ui.stickers.container

import androidx.compose.foundation.pager.PagerState
import androidx.lifecycle.ViewModel
import com.furianrt.toolspanel.R
import com.furianrt.toolspanel.internal.domain.StickersHolder
import com.furianrt.toolspanel.internal.ui.stickers.extensions.toContainerPack
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow

@HiltViewModel(assistedFactory = StickersContainerViewModel.Factory::class)
internal class StickersContainerViewModel @AssistedInject constructor(
    stickersHolder: StickersHolder,
    @Assisted private val noteId: String,
) : ViewModel() {

    private val packs = stickersHolder.getStickersPacks()
    private val pagerState = PagerState(
        pageCount = packs::size,
        currentPage = 1,
    )

    val state: StateFlow<StickersContainerUiState> = MutableStateFlow(
        StickersContainerUiState(
            noteId = noteId,
            packs = buildList {
                add(
                    StickersContainerUiState.Pack.Custom(
                        icon = R.drawable.ic_custom_sticker,
                    )
                )
                addAll(packs.map { it.toContainerPack() })
            },
            pagerState = pagerState,
        )
    )

    private val _effect = MutableSharedFlow<StickersContainerEffect>(extraBufferCapacity = 5)
    val effect = _effect.asSharedFlow()

    fun onEvent(event: StickersContainerEvent) {
        when (event) {
            is StickersContainerEvent.OnCloseClick -> {
                _effect.tryEmit(StickersContainerEffect.ClosePanel)
            }

            is StickersContainerEvent.OnTitleStickerPackClick -> {
                _effect.tryEmit(StickersContainerEffect.ScrollContentToIndex(event.index))
            }

            is StickersContainerEvent.OnKeyboardClick -> {
                _effect.tryEmit(StickersContainerEffect.ShowKeyboard)
            }

            is StickersContainerEvent.OnStickerSelected -> {
                _effect.tryEmit(StickersContainerEffect.SelectSticker(event.sticker))
            }

            is StickersContainerEvent.OnOpenMediaSelectorRequest -> {
                _effect.tryEmit(StickersContainerEffect.OpenMediaSelector(event.params))
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            noteId: String,
        ): StickersContainerViewModel
    }
}