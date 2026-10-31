package com.furianrt.apptheme.internal.ui

import androidx.compose.runtime.Immutable
import com.furianrt.apptheme.internal.ui.entities.ThemeListItem
import com.furianrt.mediaselector.api.MediaResult
import com.furianrt.mediaselector.api.MediaViewerRoute
import com.furianrt.uikit.entities.UiAppTheme

internal data class AppThemeState(
    val theme: UiAppTheme,
    val content: Content,
) {
    @Immutable
    sealed interface Content {
        data object Loading : Content
        data class Success(
            val items: List<ThemeListItem>,
            val selectedId: String,
        ) : Content
    }
}

internal sealed interface AppThemeEvent {
    data class OnThemeClick(val theme: UiAppTheme) : AppThemeEvent
    data object OnBackClick : AppThemeEvent
    data object OnAddClick : AppThemeEvent
    data class OnOpenMediaViewerRequest(val route: MediaViewerRoute) : AppThemeEvent
    data object OnMediaPermissionsSelected : AppThemeEvent
    data class OnDeleteBackgroundClick(val id: String) : AppThemeEvent
}

internal sealed interface AppThemeEffect {
    data object CloseScreen : AppThemeEffect
    data object RequestStoragePermissions : AppThemeEffect
    data class OpenMediaSelector(
        val onMediaSelected: (result: MediaResult) -> Unit,
    ) : AppThemeEffect

    data object ShowPermissionsDeniedDialog : AppThemeEffect
    data class OpenMediaViewer(val route: MediaViewerRoute) : AppThemeEffect
    data object OpenBillingScreen : AppThemeEffect
}