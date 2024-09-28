package com.furianrt.mediaview.internal.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.furianrt.core.indexOfFirstOrNull
import com.furianrt.core.mapImmutable
import com.furianrt.domain.entities.LocalNote
import com.furianrt.domain.repositories.MediaRepository
import com.furianrt.mediaview.api.MediaViewRoute
import com.furianrt.mediaview.internal.domain.GetNoteMediaUseCase
import com.furianrt.mediaview.internal.ui.extensions.toLocalNoteMedia
import com.furianrt.mediaview.internal.ui.extensions.toMediaItem
import com.furianrt.uikit.extensions.launch
import com.furianrt.uikit.utils.DialogIdentifier
import com.furianrt.uikit.utils.DialogResult
import com.furianrt.uikit.utils.DialogResultCoordinator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
internal class MediaViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getNoteMediaUseCase: GetNoteMediaUseCase,
    private val mediaRepository: MediaRepository,
    private val dialogResultCoordinator: DialogResultCoordinator,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<MediaViewRoute>()

    private val _state = MutableStateFlow(buildInitialState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MediaViewEffect>(extraBufferCapacity = 10)
    val effect = _effect.asSharedFlow()

    private val deletedMediaNames = mutableSetOf<String>()

    override fun onCleared() {
        if (deletedMediaNames.isNotEmpty()) {
            dialogResultCoordinator.onDialogResult(
                dialogIdentifier = DialogIdentifier(
                    requestId = route.requestId,
                    dialogId = route.dialogId,
                ),
                code = DialogResult.Ok(data = deletedMediaNames),
            )
        }
    }

    fun onEvent(event: MediaViewEvent) {
        when (event) {
            is MediaViewEvent.OnButtonBackClick -> {
                _effect.tryEmit(MediaViewEffect.CloseScreen)
            }

            is MediaViewEvent.OnButtonDeleteClick -> {
                val media = _state.value.media.getOrNull(event.mediaIndex) ?: return
                deletedMediaNames.add(media.name)
                val resultMedia = _state.value.media.toPersistentList().removeAt(event.mediaIndex)
                if (resultMedia.isEmpty()) {
                    _effect.tryEmit(MediaViewEffect.CloseScreen)
                } else {
                    _state.update { it.copy(media = resultMedia) }
                }
            }

            is MediaViewEvent.OnButtonSaveToGalleryClick -> {
                val media = _state.value.media.getOrNull(event.mediaIndex) ?: return
                launch {
                    if (mediaRepository.saveToGallery(media.toLocalNoteMedia())) {
                        _effect.tryEmit(MediaViewEffect.ShowMediaSavedMessage)
                    } else {
                        _effect.tryEmit(MediaViewEffect.ShowMediaSaveErrorMessage)
                    }
                }
            }
        }
    }

    private fun buildInitialState(): MediaViewUiState {
        val media = getNoteMediaUseCase(route.noteId)
        return MediaViewUiState(
            media = media.mapImmutable(LocalNote.Content.Media::toMediaItem),
            initialMediaIndex = media.indexOfFirstOrNull { it.name == route.mediaName } ?: 0,
        )
    }
}