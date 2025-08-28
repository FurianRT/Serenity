package com.furianrt.notepage.internal.ui.page

import android.net.Uri
import com.furianrt.core.findInstance
import com.furianrt.mediaselector.api.MediaResult
import com.furianrt.mediaselector.api.MediaViewerRoute
import com.furianrt.notelistui.entities.UiNoteBackground
import com.furianrt.notelistui.entities.UiNoteContent
import com.furianrt.notelistui.entities.UiNoteFontColor
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.notelistui.entities.UiNoteTag
import com.furianrt.notepage.internal.ui.page.entities.LocationState
import com.furianrt.notepage.internal.ui.stickers.entities.StickerItem
import com.furianrt.toolspanel.api.VoiceRecord
import com.furianrt.uikit.utils.DialogIdentifier
import kotlinx.collections.immutable.ImmutableList

internal sealed interface PageUiState {
    data object Loading : PageUiState
    data object Empty : PageUiState
    data class Success(
        val noteId: String,
        val content: ImmutableList<UiNoteContent>,
        val tags: ImmutableList<UiNoteTag>,
        val stickers: ImmutableList<StickerItem>,
        val playingVoiceId: String?,
        val fontFamily: UiNoteFontFamily?,
        val fontColor: UiNoteFontColor?,
        val fontSize: Int,
        val noteBackground: UiNoteBackground?,
        val moodId: String?,
        val defaultMoodId: String?,
        val locationState: LocationState,
        val isInEditMode: Boolean,
    ) : PageUiState {

        val showLocation: Boolean
            get() = isInEditMode || locationState !is LocationState.Empty

        val playingVoice: UiNoteContent.Voice?
            get() = content.findInstance<UiNoteContent.Voice> { it.id == playingVoiceId }
    }
}

internal sealed interface PageEvent {
    data class OnEditModeStateChange(val isEnabled: Boolean) : PageEvent
    data class OnIsSelectedChange(val isSelected: Boolean) : PageEvent
    data class OnTagRemoveClick(val tag: UiNoteTag.Regular) : PageEvent
    data class OnTagDoneEditing(val tag: UiNoteTag.Template) : PageEvent
    data object OnTagTextEntered : PageEvent
    data object OnTagTextCleared : PageEvent
    data object OnTagFocusChanged : PageEvent
    data object OnSelectMediaClick : PageEvent
    data object OnTakePictureClick : PageEvent
    data object OnMediaPermissionsSelected : PageEvent
    data object OnCameraPermissionSelected : PageEvent
    data object OnLocationPermissionSelected : PageEvent
    data class OnTakePictureResult(val isSuccess: Boolean) : PageEvent
    data class OnCameraNotFoundError(val error: Throwable) : PageEvent
    data class OnTitleFocusChange(val id: String, val focused: Boolean) : PageEvent
    data object OnFocusedTitleSelectionChange : PageEvent
    data class OnMediaClick(val media: UiNoteContent.MediaBlock.Media) : PageEvent
    data class OnMediaRemoveClick(val media: UiNoteContent.MediaBlock.Media) : PageEvent
    data class OnMediaSortingClick(val mediaBlockId: String) : PageEvent
    data class OnMediaSelected(val result: MediaResult) : PageEvent
    data object OnVoiceStarted : PageEvent
    data class OnVoiceRecorded(val record: VoiceRecord) : PageEvent
    data class OnOpenMediaViewerRequest(val route: MediaViewerRoute) : PageEvent
    data class OnTitleTextChange(val id: String) : PageEvent
    data class OnFontFamilySelected(val family: UiNoteFontFamily?) : PageEvent
    data class OnFontColorSelected(val color: UiNoteFontColor?) : PageEvent
    data class OnFontSizeSelected(val size: Int) : PageEvent
    data class OnVoiceRemoveClick(val voice: UiNoteContent.Voice) : PageEvent
    data class OnVoicePlayClick(val voice: UiNoteContent.Voice) : PageEvent
    data class OnVoiceProgressSelected(
        val voice: UiNoteContent.Voice,
        val value: Float,
    ) : PageEvent

    data class OnStickerSelected(val sticker: StickerItem) : PageEvent
    data object OnSelectStickersClick : PageEvent
    data object OnSelectBulletListClick : PageEvent
    data object OnSelectFontClick : PageEvent
    data class OnRemoveStickerClick(val sticker: StickerItem) : PageEvent
    data class OnStickerChanged(val sticker: StickerItem) : PageEvent
    data class OnStickerClick(val sticker: StickerItem) : PageEvent
    data object OnClickOutside : PageEvent
    data object OnScreenStopped : PageEvent
    data object OnNoPositionError : PageEvent
    data object OnBackgroundsClick : PageEvent
    data class OnBackgroundSelected(val item: UiNoteBackground?) : PageEvent
    data object OnMoodClick : PageEvent
    data class OnMoodSelected(val moodId: String?) : PageEvent
    data object OnAddLocationClick : PageEvent
    data object OnRemoveLocationClick : PageEvent
}

internal sealed interface PageEffect {
    data object RequestStoragePermissions : PageEffect
    data object ShowStoragePermissionsDeniedDialog : PageEffect
    data object RequestCameraPermission : PageEffect
    data object ShowCameraPermissionsDeniedDialog : PageEffect
    data object RequestLocationPermission : PageEffect
    data object ShowLocationPermissionsDeniedDialog : PageEffect
    data object OpenMediaSelector : PageEffect
    data class OpenMediaViewer(val route: MediaViewerRoute) : PageEffect
    data class OpenMediaSortingScreen(
        val noteId: String,
        val mediaBlockId: String,
        val identifier: DialogIdentifier,
    ) : PageEffect

    data class UpdateContentChangedState(val isChanged: Boolean) : PageEffect
    data class OpenMediaViewScreen(
        val noteId: String,
        val mediaId: String,
        val identifier: DialogIdentifier,
    ) : PageEffect

    data class BringContentToView(val content: UiNoteContent) : PageEffect
    data object HideKeyboard : PageEffect
    data class ShowMessage(val message: String) : PageEffect
    data class ShowToast(val message: String) : PageEffect
    data class TakePicture(val uri: Uri) : PageEffect
    data class ShowMoodDialog(
        val moodId: String?,
        val defaultMoodId: String?,
    ) : PageEffect
}
