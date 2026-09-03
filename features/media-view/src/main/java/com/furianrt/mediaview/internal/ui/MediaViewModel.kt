package com.furianrt.mediaview.internal.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.furianrt.core.DispatchersProvider
import com.furianrt.core.indexOfFirstOrNull
import com.furianrt.domain.entities.LocalNote
import com.furianrt.domain.entities.NoteFontFamily
import com.furianrt.domain.managers.ResourcesManager
import com.furianrt.domain.managers.SyncManager
import com.furianrt.domain.repositories.AppearanceRepository
import com.furianrt.domain.repositories.MediaRepository
import com.furianrt.mediaview.api.MediaViewRoute
import com.furianrt.mediaview.internal.domain.GetNoteMediaUseCase
import com.furianrt.mediaview.internal.ui.entities.MediaItem
import com.furianrt.mediaview.internal.ui.extensions.toLocalMedia
import com.furianrt.mediaview.internal.ui.extensions.toMediaItem
import com.furianrt.notelistui.extensions.toNoteFont
import com.furianrt.uikit.extensions.launch
import com.furianrt.uikit.utils.DialogIdentifier
import com.furianrt.uikit.utils.DialogResult
import com.furianrt.uikit.utils.DialogResultCoordinator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import com.furianrt.uikit.R as uiR

@HiltViewModel
internal class MediaViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    dispatchers: DispatchersProvider,
    getNoteMediaUseCase: GetNoteMediaUseCase,
    appearanceRepository: AppearanceRepository,
    private val mediaRepository: MediaRepository,
    private val syncManager: SyncManager,
    private val resourcesManager: ResourcesManager,
    private val dialogResultCoordinator: DialogResultCoordinator,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<MediaViewRoute>()

    private val deletedMediaIdsState = MutableStateFlow(emptySet<String>())

    val state: StateFlow<MediaViewUiState> = combine(
        getNoteMediaUseCase(
            noteId = route.noteId,
            blockId = route.mediaBlockId
        ),
        deletedMediaIdsState,
        appearanceRepository.getAppFont(),
        ::buildState,
    ).flowOn(
        context = dispatchers.default,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MediaViewUiState.Loading,
    )

    private val _effect = MutableSharedFlow<MediaViewEffect>(extraBufferCapacity = 5)
    val effect = _effect.asSharedFlow()

    override fun onCleared() {
        val requestId = route.requestId
        val dialogId = route.dialogId
        if (requestId != null && dialogId != null && deletedMediaIdsState.value.isNotEmpty()) {
            dialogResultCoordinator.onDialogResult(
                dialogIdentifier = DialogIdentifier(
                    requestId = requestId,
                    dialogId = dialogId,
                ),
                code = DialogResult.Ok(data = deletedMediaIdsState.value),
            )
        }
    }

    fun onEvent(event: MediaViewEvent) {
        when (event) {
            is MediaViewEvent.OnButtonBackClick -> {
                _effect.tryEmit(MediaViewEffect.CloseScreen)
            }

            is MediaViewEvent.OnButtonDeleteClick -> onButtonDeleteClick(event.mediaIndex)
            is MediaViewEvent.OnButtonSaveToGalleryClick -> {
                onButtonSaveToGalleryClick(event.mediaIndex)
            }

            is MediaViewEvent.OnButtonShareClick -> onButtonShareClick(event.mediaIndex)
            is MediaViewEvent.OnButtonGoToNoteClick -> onButtonGoToNoteClick(event.mediaIndex)
        }
    }

    private fun onButtonGoToNoteClick(mediaIndex: Int) {
        val media = getMediaItem(mediaIndex) ?: return
        launch {
            val noteId = mediaRepository.getNoteId(media.id)
            if (noteId != null) {
                _effect.tryEmit(MediaViewEffect.OpenNoteViewScreen(noteId))
            }
        }
    }

    private fun onButtonDeleteClick(mediaIndex: Int) {
        when {
            syncManager.isBackupInProgress() -> _effect.tryEmit(
                MediaViewEffect.ShowSyncProgressMessage(
                    message = resourcesManager.getString(uiR.string.backup_in_progress),
                ),
            )

            syncManager.isRestoreInProgress() -> _effect.tryEmit(
                MediaViewEffect.ShowSyncProgressMessage(
                    message = resourcesManager.getString(uiR.string.restore_in_progress),
                ),
            )

            else -> {
                deleteMedia(mediaIndex)
            }
        }
    }

    private fun onButtonSaveToGalleryClick(mediaIndex: Int) {
        val media = getMediaItem(mediaIndex) ?: return
        launch {
            if (mediaRepository.saveToGallery(media.toLocalMedia())) {
                _effect.tryEmit(MediaViewEffect.ShowMediaSavedMessage)
            } else {
                _effect.tryEmit(MediaViewEffect.ShowMediaSaveErrorMessage)
            }
        }
    }

    private fun onButtonShareClick(mediaIndex: Int) {
        val media = getMediaItem(mediaIndex) ?: return
        _effect.tryEmit(MediaViewEffect.ShareMedia(media))
    }

    private fun deleteMedia(index: Int) {
        val successState = (state.value as? MediaViewUiState.Success) ?: return
        val media = successState.media
        val mediaToDelete = media.getOrNull(index) ?: return
        deletedMediaIdsState.update { it + mediaToDelete.id }
        if (media.size == 1 && media.first().id == mediaToDelete.id) {
            _effect.tryEmit(MediaViewEffect.CloseScreen)
        }
    }

    private fun getMediaItem(index: Int): MediaItem? = (state.value as? MediaViewUiState.Success)
        ?.media?.getOrNull(index)

    private fun buildState(
        media: List<LocalNote.Content.Media>,
        deletedMediaIds: Set<String>,
        font: NoteFontFamily,
    ): MediaViewUiState {
        val filteredMedia = media.filter { deletedMediaIds.none { id -> id == it.id } }
        return MediaViewUiState.Success(
            media = filteredMedia.map(LocalNote.Content.Media::toMediaItem),
            font = font.toNoteFont(),
            initialPage = filteredMedia.indexOfFirstOrNull { it.id == route.initialMediaId } ?: 0,
            showDeleteButton = route.allowDelete,
            showGoToNoteButton = route.allowGoToNote,
        )
    }
}