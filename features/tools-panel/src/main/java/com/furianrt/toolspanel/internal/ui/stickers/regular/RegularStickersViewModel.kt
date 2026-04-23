package com.furianrt.toolspanel.internal.ui.stickers.regular

import androidx.lifecycle.ViewModel
import com.furianrt.toolspanel.internal.domain.StickersHolder
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow

@HiltViewModel(assistedFactory = RegularStickersViewModel.Factory::class)
internal class RegularStickersViewModel @AssistedInject constructor(
    stickersHolder: StickersHolder,
    @Assisted private val packId: String,
) : ViewModel() {

    val state: StateFlow<RegularStickersUiState> = MutableStateFlow(
        RegularStickersUiState(
            pack = stickersHolder.getStickerPack(packId),
        )
    )

    private val _effect = MutableSharedFlow<RegularStickersEffect>(extraBufferCapacity = 5)
    val effect = _effect.asSharedFlow()

    fun onEvent(event: RegularStickersEvent) {
        when (event) {
            is RegularStickersEvent.OnStickerSelected -> {
                _effect.tryEmit(RegularStickersEffect.SelectSticker(event.sticker))
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            packId: String,
        ): RegularStickersViewModel
    }
}