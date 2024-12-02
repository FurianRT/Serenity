package com.furianrt.search.internal.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.domain.repositories.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
internal class SearchViewModel @Inject constructor(
    private val notesRepository: NotesRepository,
) : ViewModel() {

    val state: StateFlow<SearchUiState> = flowOf(SearchUiState())
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SearchUiState(),
        )

    private val _effect = MutableSharedFlow<SearchEffect>(extraBufferCapacity = 10)
    val effect = _effect.asSharedFlow()

}