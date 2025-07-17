package com.furianrt.toolspanel.internal.ui.bullet

import com.furianrt.toolspanel.internal.ui.bullet.entities.BulletListItem

internal data class BulletPanelUiState(
    val items: List<BulletListItem>,
)

internal sealed interface BulletPanelEvent {
    data object OnCloseClick : BulletPanelEvent
}

internal sealed interface BulletPanelEffect {
    data object ClosePanel: BulletPanelEffect
}