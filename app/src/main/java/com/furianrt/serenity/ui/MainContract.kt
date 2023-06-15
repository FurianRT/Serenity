package com.furianrt.serenity.ui

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.furianrt.notecontent.entities.UiNoteTag
import com.furianrt.serenity.ui.entities.MainScreenNote
import kotlinx.collections.immutable.ImmutableList

@Stable
internal sealed class MainUiState(
    open val hint: AssistantHint?,
) {
    @Immutable
    data class AssistantHint(val message: String, val forceShow: Boolean = false)

    @Immutable
    data class Loading(
        override val hint: AssistantHint? = null,
    ) : MainUiState(hint)

    @Immutable
    data class Empty(
        override val hint: AssistantHint? = null,
    ) : MainUiState(hint)

    @Immutable
    data class Success(
        val notes: ImmutableList<MainScreenNote>,
        override val hint: AssistantHint? = null,
    ) : MainUiState(hint)
}

internal val MainUiState.hasNotes
    get() = this is MainUiState.Success && notes.isNotEmpty()

@Stable
internal sealed interface MainEvent {
    @Immutable
    data class OnNoteClick(val note: MainScreenNote) : MainEvent

    @Immutable
    data class OnNoteTagClick(val tag: UiNoteTag) : MainEvent

    @Immutable
    object OnScrollToTopClick : MainEvent

    @Immutable
    object OnSettingsClick : MainEvent

    @Immutable
    object OnSearchClick : MainEvent

    @Immutable
    object OnAddNoteClick : MainEvent

    @Immutable
    object OnAssistantHintClick : MainEvent
}

@Stable
internal sealed interface MainEffect {
    @Immutable
    object ScrollToTop : MainEffect
}
