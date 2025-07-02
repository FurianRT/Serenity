package com.furianrt.mediasorting.internal.ui

import androidx.compose.foundation.lazy.grid.LazyGridItemInfo
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.furianrt.core.deepEqualTo
import com.furianrt.domain.entities.LocalNote
import com.furianrt.domain.entities.MediaSortingResult
import com.furianrt.domain.repositories.NotesRepository
import com.furianrt.mediaselector.api.MediaResult
import com.furianrt.mediasorting.api.MediaSortingRoute
import com.furianrt.mediasorting.internal.domain.GetNoteMediaUseCase
import com.furianrt.mediasorting.internal.extensions.toLocalNoteMedia
import com.furianrt.mediasorting.internal.extensions.toMediaItem
import com.furianrt.mediasorting.internal.extensions.toMediaItems
import com.furianrt.mediasorting.internal.ui.entities.MediaItem
import com.furianrt.permissions.utils.PermissionsUtils
import com.furianrt.uikit.extensions.launch
import com.furianrt.uikit.utils.DialogIdentifier
import com.furianrt.uikit.utils.DialogResult
import com.furianrt.uikit.utils.DialogResultCoordinator
import com.furianrt.uikit.utils.DialogResultListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import javax.inject.Inject

private const val MEDIA_VIEW_DIALOG_ID = 0

@HiltViewModel
internal class MediaSortingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getNoteMediaUseCase: GetNoteMediaUseCase,
    private val permissionsUtils: PermissionsUtils,
    private val dialogResultCoordinator: DialogResultCoordinator,
    private val notesRepository: NotesRepository,
) : ViewModel(), DialogResultListener {

    private val route = savedStateHandle.toRoute<MediaSortingRoute>()

    private val _state = MutableStateFlow(buildInitialState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MediaSortingEffect>(extraBufferCapacity = 10)
    val effect = _effect.asSharedFlow()

    init {
        dialogResultCoordinator.addDialogResultListener(requestId = route.noteId, listener = this)
    }

    override fun onCleared() {
        dialogResultCoordinator.removeDialogResultListener(
            requestId = route.noteId,
            listener = this
        )
    }

    fun onEvent(event: MediaSortingEvent) {
        when (event) {
            is MediaSortingEvent.OnAddMediaClick -> tryRequestMediaPermissions()
            is MediaSortingEvent.OnMediaPermissionsSelected -> tryOpenMediaSelector()
            is MediaSortingEvent.OnButtonBackClick -> checkCloseScreen()
            is MediaSortingEvent.OnButtonDoneClick -> {
                sendResult()
                _effect.tryEmit(MediaSortingEffect.CloseScreen)
            }

            is MediaSortingEvent.OnConfirmCloseClick -> {
                _effect.tryEmit(MediaSortingEffect.CloseScreen)
            }

            is MediaSortingEvent.OnMediaClick -> launch {
                openMediaViewScreen(event.media.id)
            }

            is MediaSortingEvent.OnMediaItemMoved -> changeMediaOrder(event.from, event.to)
            is MediaSortingEvent.OnMediaSelected -> addMedia(event.result)
            is MediaSortingEvent.OnOpenMediaViewerRequest -> {
                _effect.tryEmit(MediaSortingEffect.OpenMediaViewer(event.route))
            }

            is MediaSortingEvent.OnRemoveMediaClick -> removeMedia(setOf(event.media.id))
        }
    }

    override fun onDialogResult(dialogId: Int, result: DialogResult) {
        when (dialogId) {
            MEDIA_VIEW_DIALOG_ID -> if (result is DialogResult.Ok<*>) {
                @Suppress("UNCHECKED_CAST")
                removeMedia(result.data as Set<String>)
            }
        }
    }

    private fun sendResult() {
        if (isMediaListChanged()) {
            dialogResultCoordinator.onDialogResult(
                dialogIdentifier = DialogIdentifier(
                    requestId = route.requestId,
                    dialogId = route.dialogId,
                ),
                code = DialogResult.Ok(
                    data = MediaSortingResult(
                        noteId = route.noteId,
                        mediaBlockId = route.mediaBlockId,
                        media = state.value.media.map(MediaItem::toLocalNoteMedia),
                    )
                ),
            )
        }
    }

    private fun checkCloseScreen() {
        if (isMediaListChanged()) {
            _effect.tryEmit(MediaSortingEffect.ShowConfirmCloseDialog)
        } else {
            _effect.tryEmit(MediaSortingEffect.CloseScreen)
        }
    }

    private fun isMediaListChanged(): Boolean {
        val existingItems = getNoteMediaUseCase(route.noteId, route.mediaBlockId)
            .map(LocalNote.Content.Media::toMediaItem)
        val newItems = state.value.media
        return !existingItems.deepEqualTo(newItems)
    }

    private fun changeMediaOrder(from: LazyGridItemInfo, to: LazyGridItemInfo) {
        _state.update { currentState ->
            currentState.copy(
                media = currentState.media.toMutableList().apply {
                    add(to.index, removeAt(from.index))
                },
            )
        }
    }

    private fun addMedia(result: MediaResult) {
        _state.update { currentState ->
            val newItems = result.toMediaItems()
            currentState.copy(
                media = currentState.media.toMutableList().apply { addAll(newItems) },
            )
        }
    }

    private fun removeMedia(mediaIds: Set<String>) {
        _state.update { currentState ->
            currentState.copy(
                media = currentState.media.toMutableList().apply {
                    removeAll { mediaIds.contains(it.id) }
                },
            )
        }
    }

    private fun tryRequestMediaPermissions() {
        if (permissionsUtils.mediaAccessDenied()) {
            _effect.tryEmit(MediaSortingEffect.RequestStoragePermissions)
        } else {
            _effect.tryEmit(MediaSortingEffect.OpenMediaSelector)
        }
    }

    private fun tryOpenMediaSelector() {
        if (permissionsUtils.mediaAccessDenied()) {
            _effect.tryEmit(MediaSortingEffect.ShowPermissionsDeniedDialog)
        } else {
            _effect.tryEmit(MediaSortingEffect.OpenMediaSelector)
        }
    }

    private suspend fun openMediaViewScreen(mediaId: String) {
        val cachedNoteContent = notesRepository.getNote(route.noteId).first()?.content ?: return
        notesRepository.cacheNoteContent(
            noteId = route.noteId,
            content = cachedNoteContent.map { content ->
                if (content is LocalNote.Content.MediaBlock && content.id == route.mediaBlockId) {
                    content.copy(media = state.value.media.map(MediaItem::toLocalNoteMedia))
                } else {
                    content
                }
            },
        )
        _effect.tryEmit(
            MediaSortingEffect.OpenMediaViewScreen(
                noteId = route.noteId,
                mediaBlockId = route.mediaBlockId,
                mediaId = mediaId,
                identifier = DialogIdentifier(
                    dialogId = MEDIA_VIEW_DIALOG_ID,
                    requestId = route.noteId,
                ),
            ),
        )
    }

    private fun buildInitialState() = MediaSortingUiState(
        media = getNoteMediaUseCase(route.noteId, route.mediaBlockId)
            .map(LocalNote.Content.Media::toMediaItem),
    )
}