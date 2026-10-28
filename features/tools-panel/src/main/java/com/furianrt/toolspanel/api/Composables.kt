package com.furianrt.toolspanel.api

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.furianrt.domain.entities.NoteTextAlignment
import com.furianrt.mediaselector.api.MediaSelectorState
import com.furianrt.notelistui.composables.title.NoteTitleState
import com.furianrt.notelistui.entities.UiNoteFontColor
import com.furianrt.notelistui.entities.UiNoteFontFamily
import com.furianrt.notelistui.entities.UiNoteTheme
import com.furianrt.permissions.extensions.openAppSettingsScreen
import com.furianrt.permissions.ui.AudioRecordPermissionDialog
import com.furianrt.permissions.utils.PermissionsUtils
import com.furianrt.toolspanel.api.entities.Sticker
import com.furianrt.toolspanel.internal.ui.background.container.BackgroundContent
import com.furianrt.toolspanel.internal.ui.background.container.BackgroundTitleBar
import com.furianrt.toolspanel.internal.ui.bullet.BulletContent
import com.furianrt.toolspanel.internal.ui.bullet.BulletTitleBar
import com.furianrt.toolspanel.internal.ui.color.ColorPickerMode
import com.furianrt.toolspanel.internal.ui.color.ColorPickerPanel
import com.furianrt.toolspanel.internal.ui.font.FontContent
import com.furianrt.toolspanel.internal.ui.font.FontTitleBar
import com.furianrt.toolspanel.internal.ui.regular.RegularPanel
import com.furianrt.toolspanel.internal.ui.selected.SelectedPanel
import com.furianrt.toolspanel.internal.ui.stickers.container.StickersContent
import com.furianrt.toolspanel.internal.ui.stickers.container.StickersTitleBar
import com.furianrt.toolspanel.internal.ui.voice.VoiceButtonDone
import com.furianrt.toolspanel.internal.ui.voice.VoicePanel
import com.furianrt.uikit.extensions.applyIf
import com.furianrt.uikit.extensions.rememberKeyboardOffsetState
import com.furianrt.uikit.theme.GlassDefaults
import com.furianrt.uikit.theme.LocalSerenityPlus
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazePerformanceMode
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.glass.LocalGlassStyle
import dev.chrisbanes.haze.glass.OpticalSizeValue
import dev.chrisbanes.haze.glass.hazeGlass
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

private enum class PanelMode {
    REGULAR,
    FORMATTING,
    VOICE_RECORD,
    FONT,
    STICKERS,
    BULLET,
    BACKGROUNDS,
}

@OptIn(
    ExperimentalPermissionsApi::class,
    ExperimentalLayoutApi::class,
    ExperimentalHazeApi::class,
)
@Composable
fun ActionsPanel(
    titleState: NoteTitleState?,
    hazeState: HazeState,
    noteId: String,
    fontFamily: UiNoteFontFamily?,
    fontColor: UiNoteFontColor?,
    customFontColor: Color?,
    fontSize: Int,
    textAlignment: NoteTextAlignment,
    noteTheme: UiNoteTheme?,
    background: Color,
    voiceRecordFlow: Channel<Unit>,
    onMenuVisibilityChange: (visible: Boolean) -> Unit,
    onSelectMediaClick: () -> Unit,
    onVoiceRecordStart: () -> Unit,
    onRecordComplete: (record: VoiceRecord) -> Unit,
    onVoiceRecordCancel: () -> Unit,
    onFontFamilySelected: (family: UiNoteFontFamily?) -> Unit,
    onFontColorSelected: (color: UiNoteFontColor?) -> Unit,
    onCustomFontColorSelected: (color: Color) -> Unit,
    onFontSizeSelected: (size: Int) -> Unit,
    onFontStyleClick: () -> Unit,
    onTextAlignmentSelected: (alignment: NoteTextAlignment) -> Unit,
    onIncreaseLineSpacingClick: () -> Unit,
    onDecreaseLineSpacingClick: () -> Unit,
    onStickersClick: () -> Unit,
    onStickerSelected: (sticker: Sticker) -> Unit,
    onBulletListClick: () -> Unit,
    onNoPositionError: () -> Unit,
    onBackgroundClick: () -> Unit,
    onThemeSelected: (theme: UiNoteTheme?) -> Unit,
    openMediaSelector: (params: MediaSelectorState.Params) -> Unit,
    openBillingScreen: () -> Unit,
    requestTitleFocus: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hasSerenityPlus = LocalSerenityPlus.current
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
    var isBackgroundsPanelVisible by rememberSaveable { mutableStateOf(false) }
    var isColorPickerPanelVisible by rememberSaveable { mutableStateOf(false) }

    var colorPickerMode by remember { mutableStateOf(ColorPickerMode.FONT_COLOR) }

    val regularPanelScrollState = rememberScrollState()

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
        isBackgroundsPanelVisible -> PanelMode.BACKGROUNDS
        else -> PanelMode.REGULAR
    }

    val isExpanded = panelMode == PanelMode.FONT ||
            panelMode == PanelMode.BULLET ||
            panelMode == PanelMode.STICKERS ||
            panelMode == PanelMode.BACKGROUNDS ||
            isColorPickerPanelVisible

    val cornersAnim = remember { Animatable(1f) }

    val shape = RoundedCornerShape(
        topStart = 20.dp,
        topEnd = 20.dp,
        bottomStart = 20.dp * cornersAnim.value,
        bottomEnd = 20.dp * cornersAnim.value,
    )

    fun startVoiceRecord() {
        audioRecordPermissionsState.launchPermissionRequest()
    }

    LaunchedEffect(imeTarget, isExpanded) {
        if (imeTarget != 0 && isExpanded) {
            cornersAnim.animateTo(0f)
        }
    }

    LaunchedEffect(voiceRecordFlow) {
        voiceRecordFlow.receiveAsFlow().collect {
            startVoiceRecord()
        }
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
            isColorPickerPanelVisible = false
            cornersAnim.animateTo(1f)
        }
    }

    LaunchedEffect(isExpanded) {
        onMenuVisibilityChange(isExpanded)
    }
    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .applyIf(
                    !isFontPanelVisible &&
                            !isStickersPanelVisible &&
                            !isBulletPanelVisible &&
                            !isBackgroundsPanelVisible &&
                            !isColorPickerPanelVisible
                ) {
                    Modifier.imePadding()
                }
                .fillMaxWidth()
                .padding(bottom = 4.dp, start = 4.dp, end = 4.dp)
                .navigationBarsPadding()
                .clip(shape)
                .hazeGlass(
                    input = HazeInput.Sources(hazeState),
                    performanceMode = HazePerformanceMode.Performance,
                    style = LocalGlassStyle.current.then {
                        shape(shape)
                        tint(background.copy(alpha = 0.4f))
                        optics(
                            GlassDefaults.optics.copy(
                                depth = OpticalSizeValue.Fixed(0.9f),
                                blurRadius = OpticalSizeValue.Fixed(12.dp),
                            )
                        )
                    },
                )
                .background(MaterialTheme.colorScheme.background)
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
                        scrollState = regularPanelScrollState,
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
                        onBackgroundClick = {
                            keyboardController?.hide()
                            isBackgroundsPanelVisible = true
                            onBackgroundClick()
                        },
                        onSelectMediaClick = { onSelectMediaClick() },
                        onRecordVoiceClick = { startVoiceRecord() },
                    )

                    PanelMode.FORMATTING -> SelectedPanel(
                        titleState = titleState,
                        showKeyboardButton = isColorPickerPanelVisible,
                        requestTitleFocus = requestTitleFocus,
                        onFontCustomColorClick = {
                            keyboardController?.hide()
                            if (hasSerenityPlus) {
                                isColorPickerPanelVisible = true
                                colorPickerMode = ColorPickerMode.FONT_COLOR
                            } else {
                                openBillingScreen()
                            }
                        },
                        onFontFillCustomColorClick = {
                            keyboardController?.hide()
                            if (hasSerenityPlus) {
                                isColorPickerPanelVisible = true
                                colorPickerMode = ColorPickerMode.FONT_FILL_COLOR
                            } else {
                                openBillingScreen()
                            }
                        },
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
                        requestTitleFocus = requestTitleFocus,
                        noteId = noteId,
                        fontColor = fontColor,
                        customFontColor = customFontColor,
                        fontFamily = fontFamily,
                        fontSize = fontSize,
                        textAlignment = textAlignment,
                        onDoneClick = { isFontPanelVisible = false },
                    )

                    PanelMode.STICKERS -> StickersTitleBar(
                        noteId = noteId,
                        requestTitleFocus = requestTitleFocus,
                        onDoneClick = { isStickersPanelVisible = false },
                    )

                    PanelMode.BULLET -> BulletTitleBar(
                        requestTitleFocus = requestTitleFocus,
                        onDoneClick = { isBulletPanelVisible = false },
                    )

                    PanelMode.BACKGROUNDS -> BackgroundTitleBar(
                        noteId = noteId,
                        noteTheme = noteTheme,
                        requestTitleFocus = requestTitleFocus,
                        onDoneClick = { isBackgroundsPanelVisible = false },
                    )
                }
            }

            FontContent(
                noteId = noteId,
                fontColor = fontColor,
                customFontColor = customFontColor,
                fontFamily = fontFamily,
                fontSize = fontSize,
                textAlignment = textAlignment,
                visible = isFontPanelVisible,
                onFontFamilySelected = onFontFamilySelected,
                onFontColorSelected = onFontColorSelected,
                onCustomFontColorSelected = onCustomFontColorSelected,
                onFontSizeSelected = onFontSizeSelected,
                onTextAlignmentSelected = onTextAlignmentSelected,
                onIncreaseLineSpacingClick = onIncreaseLineSpacingClick,
                onDecreaseLineSpacingClick = onDecreaseLineSpacingClick,
                openBillingScreen = openBillingScreen,
            )

            StickersContent(
                noteId = noteId,
                visible = isStickersPanelVisible,
                onStickerSelected = onStickerSelected,
                openMediaSelector = openMediaSelector,
                openBillingScreen = openBillingScreen,
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
                openMediaSelector = openMediaSelector,
                openBillingScreen = openBillingScreen,
            )
            ColorPickerPanel(
                titleState = titleState,
                mode = colorPickerMode,
                visible = isColorPickerPanelVisible,
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
