package com.furianrt.toolspanel.api

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imeAnimationTarget
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.furianrt.notelistui.composables.title.NoteTitleState
import com.furianrt.notelistui.entities.UiNoteFontColor
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.notelistui.entities.UiNoteTheme
import com.furianrt.permissions.extensions.openAppSettingsScreen
import com.furianrt.permissions.ui.AudioRecordPermissionDialog
import com.furianrt.permissions.utils.PermissionsUtils
import com.furianrt.toolspanel.api.entities.Sticker
import com.furianrt.toolspanel.internal.ui.attachments.AttachmentsPanel
import com.furianrt.toolspanel.internal.ui.background.container.BackgroundContent
import com.furianrt.toolspanel.internal.ui.background.container.BackgroundTitleBar
import com.furianrt.toolspanel.internal.ui.bullet.BulletContent
import com.furianrt.toolspanel.internal.ui.bullet.BulletTitleBar
import com.furianrt.toolspanel.internal.ui.font.FontContent
import com.furianrt.toolspanel.internal.ui.font.FontTitleBar
import com.furianrt.toolspanel.internal.ui.regular.RegularPanel
import com.furianrt.toolspanel.internal.ui.selected.SelectedPanel
import com.furianrt.toolspanel.internal.ui.stickers.StickersContent
import com.furianrt.toolspanel.internal.ui.stickers.StickersTitleBar
import com.furianrt.toolspanel.internal.ui.voice.VoiceButtonDone
import com.furianrt.toolspanel.internal.ui.voice.VoicePanel
import com.furianrt.uikit.extensions.applyIf
import com.furianrt.uikit.extensions.rememberKeyboardOffsetState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect

private enum class PanelMode {
    REGULAR,
    FORMATTING,
    VOICE_RECORD,
    FONT,
    STICKERS,
    BULLET,
    ATTACHMENTS,
    BACKGROUNDS,
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalLayoutApi::class)
@Composable
fun ActionsPanel(
    titleState: NoteTitleState?,
    hazeState: HazeState,
    noteId: String,
    fontFamily: UiNoteFontFamily?,
    fontColor: UiNoteFontColor?,
    fontSize: Int,
    noteTheme: UiNoteTheme?,
    background: Color,
    onMenuVisibilityChange: (visible: Boolean) -> Unit,
    onSelectMediaClick: () -> Unit,
    onTakePictureClick: () -> Unit,
    onVoiceRecordStart: () -> Unit,
    onRecordComplete: (record: VoiceRecord) -> Unit,
    onVoiceRecordCancel: () -> Unit,
    onFontFamilySelected: (family: UiNoteFontFamily?) -> Unit,
    onFontColorSelected: (color: UiNoteFontColor?) -> Unit,
    onFontSizeSelected: (size: Int) -> Unit,
    onStickerSelected: (sticker: Sticker) -> Unit,
    onFontStyleClick: () -> Unit,
    onStickersClick: () -> Unit,
    onBulletListClick: () -> Unit,
    onNoPositionError: () -> Unit,
    onBackgroundClick: () -> Unit,
    onThemeSelected: (theme: UiNoteTheme?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val hasMultiSelection by remember(titleState) {
        derivedStateOf {
            titleState?.selection?.min != titleState?.selection?.max
        }
    }
    var isVoiceRecordingActive by rememberSaveable { mutableStateOf(false) }
    var isFontPanelVisible by rememberSaveable { mutableStateOf(false) }
    var isStickersPanelVisible by rememberSaveable { mutableStateOf(false) }
    var isBulletPanelVisible by rememberSaveable { mutableStateOf(false) }
    var showAudioRecordPermissionDialog by rememberSaveable { mutableStateOf(false) }
    var isAttachmentsPanelVisible by rememberSaveable { mutableStateOf(false) }
    var isBackgroundsPanelVisible by rememberSaveable { mutableStateOf(false) }

    val audioRecordPermissionsState = rememberPermissionState(
        permission = PermissionsUtils.getAudioRecordPermission(),
        onPermissionResult = { granted ->
            if (granted) {
                isVoiceRecordingActive = true
                onVoiceRecordStart()
            } else {
                showAudioRecordPermissionDialog = true
            }
        },
    )

    val imeTarget = WindowInsets.imeAnimationTarget.getBottom(LocalDensity.current)
    val imeBottom by rememberKeyboardOffsetState()
    val isImeVisible = imeTarget != 0 && imeBottom == imeTarget

    val panelMode = when {
        isFontPanelVisible -> PanelMode.FONT
        isBulletPanelVisible -> PanelMode.BULLET
        isStickersPanelVisible -> PanelMode.STICKERS
        isVoiceRecordingActive -> PanelMode.VOICE_RECORD
        hasMultiSelection -> PanelMode.FORMATTING
        isAttachmentsPanelVisible -> PanelMode.ATTACHMENTS
        isBackgroundsPanelVisible -> PanelMode.BACKGROUNDS
        else -> PanelMode.REGULAR
    }

    LaunchedEffect(hasMultiSelection) {
        if (hasMultiSelection) {
            keyboardController?.show()
        }
    }

    LaunchedEffect(isImeVisible) {
        if (isImeVisible) {
            isFontPanelVisible = false
            isStickersPanelVisible = false
            isBulletPanelVisible = false
            isBackgroundsPanelVisible = false
        }
    }

    LaunchedEffect(
        isFontPanelVisible,
        isStickersPanelVisible,
        isBulletPanelVisible,
        isBackgroundsPanelVisible,
    ) {
        onMenuVisibilityChange(
            isFontPanelVisible ||
                    isStickersPanelVisible ||
                    isBulletPanelVisible ||
                    isBackgroundsPanelVisible
        )
    }
    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .applyIf(
                    !isFontPanelVisible &&
                            !isStickersPanelVisible &&
                            !isBulletPanelVisible &&
                            !isBackgroundsPanelVisible
                ) {
                    Modifier.imePadding()
                }
                .fillMaxWidth()
                .hazeEffect(
                    state = hazeState,
                    style = HazeDefaults.style(
                        backgroundColor = background,
                        tint = HazeTint(background.copy(alpha = 0.7f)),
                        noiseFactor = 0f,
                        blurRadius = 12.dp,
                    )
                )
                .background(MaterialTheme.colorScheme.background)
                .navigationBarsPadding()
                .align(Alignment.BottomCenter),
        ) {
            AnimatedContent(
                modifier = Modifier.height(ToolsPanelConstants.PANEL_HEIGHT),
                targetState = panelMode,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(durationMillis = 220, delayMillis = 90)))
                        .togetherWith(fadeOut(animationSpec = tween(durationMillis = 90)))
                },
            ) { targetState ->
                when (targetState) {
                    PanelMode.REGULAR -> RegularPanel(
                        titleState = titleState,
                        onFontStyleClick = {
                            keyboardController?.hide()
                            isFontPanelVisible = true
                            onFontStyleClick()
                        },
                        onStickersClick = {
                            keyboardController?.hide()
                            isStickersPanelVisible = true
                            onStickersClick()
                        },
                        onBulletListClick = {
                            if (titleState == null) {
                                onNoPositionError()
                            } else {
                                keyboardController?.hide()
                                isBulletPanelVisible = true
                                onBulletListClick()
                            }
                        },
                        onAttachClick = { isAttachmentsPanelVisible = true },
                        onBackgroundClick = {
                            keyboardController?.hide()
                            isBackgroundsPanelVisible = true
                            onBackgroundClick()
                        }
                    )

                    PanelMode.FORMATTING -> SelectedPanel(
                        titleState = titleState,
                    )

                    PanelMode.VOICE_RECORD -> VoicePanel(
                        noteId = noteId,
                        onRecordComplete = { record ->
                            isVoiceRecordingActive = false
                            onRecordComplete(record)
                        },
                        onCancelRequest = {
                            isVoiceRecordingActive = false
                            onVoiceRecordCancel()
                        },
                    )

                    PanelMode.FONT -> FontTitleBar(
                        showKeyBoardButton = titleState != null,
                        onDoneClick = { isFontPanelVisible = false },
                    )

                    PanelMode.STICKERS -> StickersTitleBar(
                        showKeyBoardButton = titleState != null,
                        onDoneClick = { isStickersPanelVisible = false },
                    )

                    PanelMode.BULLET -> BulletTitleBar(
                        showKeyBoardButton = titleState != null,
                        onDoneClick = { isBulletPanelVisible = false },
                    )

                    PanelMode.ATTACHMENTS -> AttachmentsPanel(
                        onSelectMediaClick = {
                            isAttachmentsPanelVisible = false
                            onSelectMediaClick()
                        },
                        onTakePictureClick = {
                            isAttachmentsPanelVisible = false
                            onTakePictureClick()
                        },
                        onRecordVoiceClick = {
                            isAttachmentsPanelVisible = false
                            audioRecordPermissionsState.launchPermissionRequest()
                        },
                        onCloseClick = { isAttachmentsPanelVisible = false },
                    )

                    PanelMode.BACKGROUNDS -> BackgroundTitleBar(
                        noteId = noteId,
                        noteTheme = noteTheme,
                        showKeyBoardButton = titleState != null,
                        onDoneClick = { isBackgroundsPanelVisible = false },
                    )
                }
            }

            FontContent(
                noteId = noteId,
                fontColor = fontColor,
                fontFamily = fontFamily,
                fontSize = fontSize,
                visible = isFontPanelVisible,
                onFontFamilySelected = onFontFamilySelected,
                onFontColorSelected = onFontColorSelected,
                onFontSizeSelected = onFontSizeSelected,
            )

            StickersContent(
                visible = isStickersPanelVisible,
                onStickerSelected = onStickerSelected,
            )

            BulletContent(
                visible = isBulletPanelVisible,
                titleState = titleState,
            )

            BackgroundContent(
                noteId = noteId,
                noteTheme = noteTheme,
                visible = isBackgroundsPanelVisible,
                onThemeSelected = onThemeSelected,
            )
        }
        AnimatedVisibility(
            modifier = Modifier
                .offset(x = 20.dp, y = 17.dp)
                .imePadding()
                .navigationBarsPadding()
                .align(Alignment.CenterEnd),
            visible = panelMode == PanelMode.VOICE_RECORD,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
        ) {
            VoiceButtonDone(
                noteId = noteId,
            )
        }
    }

    if (showAudioRecordPermissionDialog) {
        AudioRecordPermissionDialog(
            hazeState = hazeState,
            onDismissRequest = { showAudioRecordPermissionDialog = false },
            onSettingsClick = context::openAppSettingsScreen,
        )
    }
}
