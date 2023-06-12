package com.furianrt.noteview.internal.ui.page

import androidx.lifecycle.ViewModel
import com.furianrt.storage.api.repositories.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class PageViewModel @Inject constructor(
    private val notesRepository: NotesRepository,
) : ViewModel()
