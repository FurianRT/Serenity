package com.furianrt.gallery.internal.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.core.DispatchersProvider
import com.furianrt.domain.entities.NoteMedia
import com.furianrt.domain.repositories.AppearanceRepository
import com.furianrt.gallery.internal.domain.GetNoteDatesWithMediaUseCase
import com.furianrt.gallery.internal.domain.GetNoteMediaUseCase
import com.furianrt.gallery.internal.ui.entities.DateFilter
import com.furianrt.gallery.internal.ui.entities.ListItem
import com.furianrt.gallery.internal.ui.mappers.toDateString
import com.furianrt.gallery.internal.ui.mappers.toListItems
import com.furianrt.uikit.entities.UiThemeColor
import com.furianrt.uikit.extensions.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
internal class GalleryViewModel @Inject constructor(
    dispatchers: DispatchersProvider,
    getNoteMediaUseCase: GetNoteMediaUseCase,
    appearanceRepository: AppearanceRepository,
    private val getNoteDatesWithMediaUseCase: GetNoteDatesWithMediaUseCase,
) : ViewModel() {

    private val dateFilterState = MutableStateFlow<DateFilter?>(null)

    private val _effect = MutableSharedFlow<GalleryEffect>(extraBufferCapacity = 5)
    val effect: SharedFlow<GalleryEffect> = _effect.asSharedFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<GalleryState> = dateFilterState
        .flatMapLatest { filter ->
            combine(
                appearanceRepository.getAppThemeColorId(),
                getNoteMediaUseCase(
                    startDate = filter?.start,
                    endDate = filter?.end,
                ),
            ) { appThemeColorId, media ->
                buildState(
                    appThemeColorId = appThemeColorId,
                    media = media,
                    dateFilter = filter,
                )
            }
        }
        .flowOn(dispatchers.default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = GalleryState(
                theme = UiThemeColor.fromId(appearanceRepository.getAppThemeColorId().value),
                dateFilter = null,
                content = GalleryState.Content.Loading,
            ),
        )

    fun onEvent(event: GalleryEvent) {
        when (event) {
            is GalleryEvent.OnCloseClick -> onCloseClick()
            is GalleryEvent.OnCreateNoteClick -> onCreateNoteClick()
            is GalleryEvent.OnDateFiltersClick -> onDateFiltersClick()
            is GalleryEvent.OnDateFilterSelected -> onDateFilterSelected(
                start = event.start,
                end = event.end,
            )

            is GalleryEvent.OnPhotoItemClick -> onPhotoItemClick(event.item)
            is GalleryEvent.OnRemoveDateFilterClick -> onRemoveDateFilterClick()
        }
    }

    private fun onDateFiltersClick() {
        launch {
            _effect.tryEmit(
                GalleryEffect.ShowDateSelector(
                    start = dateFilterState.value?.start,
                    end = dateFilterState.value?.end,
                    datesWithPhotos = getNoteDatesWithMediaUseCase().first(),
                )
            )
        }
    }

    private fun onDateFilterSelected(
        start: LocalDate,
        end: LocalDate?,
    ) {
        dateFilterState.update {
            DateFilter(
                start = start,
                end = end,
            )
        }
    }

    private fun onRemoveDateFilterClick() {
        dateFilterState.update { null }
    }

    private fun onPhotoItemClick(item: ListItem.Photo) {

    }

    private fun onCreateNoteClick() {
        _effect.tryEmit(GalleryEffect.OpenCreateNoteScreen)
    }

    private fun onCloseClick() {
        _effect.tryEmit(GalleryEffect.CloseScreen)
    }

    private fun buildState(
        appThemeColorId: String?,
        media: List<NoteMedia>,
        dateFilter: DateFilter?,
    ) = GalleryState(
        theme = UiThemeColor.fromId(appThemeColorId),
        dateFilter = dateFilter?.toDateString(),
        content = if (media.isEmpty()) {
            GalleryState.Content.Empty
        } else {
            GalleryState.Content.Success(
                items = media.toListItems(),
            )
        }
    )
}