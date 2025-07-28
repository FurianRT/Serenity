package com.furianrt.mediaview.internal.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.furianrt.core.indexOfFirstOrNull
import com.furianrt.core.mapImmutable
import com.furianrt.domain.entities.LocalNote
import com.furianrt.domain.managers.ResourcesManager
import com.furianrt.domain.managers.SyncManager
import com.furianrt.domain.repositories.AppearanceRepository
import com.furianrt.domain.repositories.MediaRepository
import com.furianrt.mediaview.api.MediaViewRoute
import com.furianrt.mediaview.internal.domain.GetNoteMediaUseCase
import com.furianrt.mediaview.internal.ui.extensions.toLocalMedia
import com.furianrt.mediaview.internal.ui.extensions.toMediaItem
import com.furianrt.uikit.entities.UiThemeColor
import com.furianrt.uikit.R as uiR
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
internal class MediaViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getNoteMediaUseCase: GetNoteMediaUseCase,
    private val mediaRepository: MediaRepository,
    private val syncManager: SyncManager,
    private val resourcesManager: ResourcesManager,
    private val appearanceRepository: AppearanceRepository,
    private val dialogResultCoordinator: DialogResultCoordinator,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<MediaViewRoute>()

    private val _state = MutableStateFlow(buildInitialState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MediaViewEffect>(extraBufferCapacity = 10)
    val effect = _effect.asSharedFlow()

    private val deletedMediaIds = mutableSetOf<String>()

    init {
        launch {
            val themeColorId = appearanceRepository.getAppThemeColorId().first()
            val themeColor = UiThemeColor.fromId(themeColorId)
            _state.update { it.copy(isLightTheme = themeColor.isLight) }
        }
    }

    override fun onCleared() {
        if (deletedMediaIds.isNotEmpty()) {
            dialogResultCoordinator.onDialogResult(
                dialogIdentifier = DialogIdentifier(
                    requestId = route.requestId,
                    dialogId = route.dialogId,
                ),
                code = DialogResult.Ok(data = deletedMediaIds),
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
                val media = _state.value.media.getOrNull(event.mediaIndex) ?: return
                launch {
                    if (mediaRepository.saveToGallery(media.toLocalMedia())) {
                        _effect.tryEmit(MediaViewEffect.ShowMediaSavedMessage)
                    } else {
                        _effect.tryEmit(MediaViewEffect.ShowMediaSaveErrorMessage)
                    }
                }
            }

            is MediaViewEvent.OnButtonShareClick -> {
                val media = _state.value.media.getOrNull(event.mediaIndex) ?: return
                _effect.tryEmit(MediaViewEffect.ShareMedia(media))
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

    private fun deleteMedia(index: Int) {
        val media = _state.value.media.getOrNull(index) ?: return
        deletedMediaIds.add(media.id)
        val resultMedia = _state.value.media.toPersistentList().removeAt(index)
        if (resultMedia.isEmpty()) {
            _effect.tryEmit(MediaViewEffect.CloseScreen)
        } else {
            _state.update { it.copy(media = resultMedia) }
        }
    }

    private fun buildInitialState(): MediaViewUiState {
        val media = getNoteMediaUseCase(route.noteId, route.mediaBlockId)
        return MediaViewUiState(
            media = media.mapImmutable(LocalNote.Content.Media::toMediaItem),
            initialMediaIndex = media.indexOfFirstOrNull { it.id == route.mediaId } ?: 0,
            isLightTheme = false,
        )
    }
}