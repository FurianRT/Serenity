package com.furianrt.noteview.internal.ui.page

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.furianrt.storage.api.repositories.NotesRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

internal class PageViewModel @AssistedInject constructor(
    private val notesRepository: NotesRepository,
    @Assisted private val noteId: String,
) : ViewModel() {

    init {
        Log.e("fkewfkwenfkwe", "init = $noteId")
    }

    override fun onCleared() {
        super.onCleared()
        Log.e("fkewfkwenfkwe", "onCleared = $noteId")
    }

    private val _state = MutableStateFlow<PageUiState>(PageUiState.Loading)
    val state: StateFlow<PageUiState> = _state.asStateFlow()

    private val _effect = Channel<PageEffect>()
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: PageEvent) {
    }

    @AssistedFactory
    interface Factory {
        fun create(noteId: String): PageViewModel
    }

    @EntryPoint
    @InstallIn(ActivityComponent::class)
    interface FactoryProvider {
        fun provide(): Factory
    }

    companion object {
        fun provideFactory(
            factory: Factory,
            noteId: String,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return factory.create(noteId) as T
            }
        }
    }
}
