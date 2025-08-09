package com.furianrt.toolspanel.api

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.furianrt.notelistui.composables.title.NoteTitleState
import com.furianrt.notelistui.entities.UiNoteFontColor
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.permissions.extensions.openAppSettingsScreen
import com.furianrt.permissions.ui.AudioRecordPermissionDialog
import com.furianrt.permissions.utils.PermissionsUtils
import com.furianrt.toolspanel.api.entities.Sticker
import com.furianrt.toolspanel.internal.ui.bullet.BulletContent
import com.furianrt.toolspanel.internal.ui.bullet.BulletTitleBar
import com.furianrt.toolspanel.internal.ui.voice.LineContent
import com.furianrt.toolspanel.internal.ui.regular.RegularPanel
import com.furianrt.toolspanel.internal.ui.selected.SelectedPanel
import com.furianrt.toolspanel.internal.ui.voice.VoicePanel
import com.furianrt.toolspanel.internal.ui.font.FontContent
import com.furianrt.toolspanel.internal.ui.font.FontTitleBar
import com.furianrt.toolspanel.internal.ui.stickers.StickersContent
import com.furianrt.toolspanel.internal.ui.stickers.StickersTitleBar
import com.furianrt.uikit.extensions.applyIf
import com.furianrt.uikit.extensions.clickableNoRipple
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
    onMenuVisibilityChange: (visible: Boolean) -> Unit,
    onSelectMediaClick: () -> Unit,
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

    val heightModifier = Modifier.height(ToolsPanelConstants.PANEL_HEIGHT)
    val hazeModifier = Modifier
        .background(MaterialTheme.colorScheme.background)
        .hazeEffect(
            state = hazeState,
            style = HazeDefaults.style(
                backgroundColor = MaterialTheme.colorScheme.surface,
                tint = HazeTint(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)),
                noiseFactor = 0f,
                blurRadius = 12.dp,
            )
        )
        .background(MaterialTheme.colorScheme.background)
        .background(MaterialTheme.colorScheme.background)

    val imeTarget = WindowInsets.imeAnimationTarget.getBottom(LocalDensity.current)
    val imeBottom by rememberKeyboardOffsetState()
    val isImeVisible = imeTarget != 0 && imeBottom == imeTarget

    val panelMode = when {
        isFontPanelVisible -> PanelMode.FONT
        isBulletPanelVisible -> PanelMode.BULLET
        isStickersPanelVisible -> PanelMode.STICKERS
        isVoiceRecordingActive -> PanelMode.VOICE_RECORD
        hasMultiSelection -> PanelMode.FORMATTING
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
        }
    }

    LaunchedEffect(isFontPanelVisible, isStickersPanelVisible, isBulletPanelVisible) {
        onMenuVisibilityChange(isFontPanelVisible || isStickersPanelVisible || isBulletPanelVisible)
    }

    Column(
        modifier = modifier
            .applyIf(!isFontPanelVisible && !isStickersPanelVisible && !isBulletPanelVisible) {
                Modifier.imePadding()
            }
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)),
    ) {
        AnimatedVisibility(
            visible = panelMode != PanelMode.VOICE_RECORD,
            enter = fadeIn(tween(delayMillis = 300)),
            exit = ExitTransition.None,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .offset(y = 4.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, MaterialTheme.colorScheme.surfaceDim)
                        )
                    ),
            )
        }
        Box {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(heightModifier)
                    .then(hazeModifier)
                    .align(Alignment.BottomCenter),
            )
            AnimatedContent(
                targetState = panelMode,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(durationMillis = 220, delayMillis = 90)))
                        .togetherWith(fadeOut(animationSpec = tween(durationMillis = 90)))
                },
            ) { targetState ->
                when (targetState) {
                    PanelMode.REGULAR -> RegularPanel(
                        modifier = heightModifier.clickableNoRipple {},
                        titleState = titleState,
                        onSelectMediaClick = onSelectMediaClick,
                        onRecordVoiceClick = {
                            audioRecordPermissionsState.launchPermissionRequest()
                        },
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
                        }
                    )

                    PanelMode.FORMATTING -> SelectedPanel(
                        modifier = heightModifier.clickableNoRipple {},
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
                        lineContent = {
                            LineContent(
                                modifier = heightModifier.clickableNoRipple {},
                                noteId = noteId,
                            )
                        },
                    )

                    PanelMode.FONT -> FontTitleBar(
                        modifier = heightModifier,
                        showKeyBoardButton = titleState != null,
                        onDoneClick = { isFontPanelVisible = false },
                    )

                    PanelMode.STICKERS -> StickersTitleBar(
                        modifier = heightModifier,
                        showKeyBoardButton = titleState != null,
                        onDoneClick = { isStickersPanelVisible = false },
                    )

                    PanelMode.BULLET -> BulletTitleBar(
                        modifier = heightModifier,
                        showKeyBoardButton = titleState != null,
                        onDoneClick = { isBulletPanelVisible = false },
                    )
                }
            }
        }

        FontContent(
            modifier = hazeModifier,
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
            modifier = hazeModifier,
            visible = isStickersPanelVisible,
            onStickerSelected = onStickerSelected,
        )

        BulletContent(
            modifier = hazeModifier,
            visible = isBulletPanelVisible,
            titleState = titleState,
        )
    }

    if (showAudioRecordPermissionDialog) {
        AudioRecordPermissionDialog(
            hazeState = hazeState,
            onDismissRequest = { showAudioRecordPermissionDialog = false },
            onSettingsClick = context::openAppSettingsScreen,
        )
    }
}
