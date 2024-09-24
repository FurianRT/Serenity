package com.furianrt.mediaview.internal.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.core.indexOfFirstOrNull
import com.furianrt.core.mapImmutable
import com.furianrt.mediaview.internal.domain.GetNoteMediaUseCase
import com.furianrt.mediaview.internal.ui.extensions.toMediaItem
import com.furianrt.storage.api.entities.LocalNote
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class MediaViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    getNoteMediaUseCase: GetNoteMediaUseCase,
) : ViewModel() {

    private val noteId: String by lazy(LazyThreadSafetyMode.NONE) {
        savedStateHandle["noteId"]!!
    }

    private val mediaName: String by lazy(LazyThreadSafetyMode.NONE) {
        savedStateHandle["mediaName"]!!
    }

    val state = getNoteMediaUseCase(noteId)
        .mapLatest { media ->
            MediaViewUiState.Success(
                media = media.mapImmutable(LocalNote.Content.Media::toMediaItem),
                initialMediaIndex = media.indexOfFirstOrNull { it.name == mediaName } ?: 0,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MediaViewUiState.Loading,
        )

    private val _effect = MutableSharedFlow<MediaViewEffect>(extraBufferCapacity = 10)
    val effect = _effect.asSharedFlow()

    fun onEvent(event: MediaViewEvent) {
        when (event) {
            is MediaViewEvent.OnButtonBackClick -> {
                _effect.tryEmit(MediaViewEffect.CloseScreen)
            }
        }
    }
}