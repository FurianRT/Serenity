package com.furianrt.toolspanel.internal.ui.bullet

import com.furianrt.notelistui.composables.title.NoteTitleState

internal data class BulletPanelUiState(
    val items: List<NoteTitleState.BulletListType>,
)

internal sealed interface BulletPanelEvent {
    data object OnCloseClick : BulletPanelEvent
}

internal sealed interface BulletPanelEffect {
    data object ClosePanel: BulletPanelEffect
}