package com.furianrt.statistics.internal.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furianrt.domain.repositories.AppearanceRepository
import com.furianrt.statistics.internal.domain.entities.MediaData
import com.furianrt.statistics.internal.domain.entities.MoodData
import com.furianrt.statistics.internal.domain.entities.NotesData
import com.furianrt.statistics.internal.domain.entities.StreakData
import com.furianrt.statistics.internal.domain.entities.TimePeriod
import com.furianrt.statistics.internal.domain.usecase.GetMediaDataUseCase
import com.furianrt.statistics.internal.domain.usecase.GetMoodDataUseCase
import com.furianrt.statistics.internal.domain.usecase.GetNotesDataUseCase
import com.furianrt.statistics.internal.domain.usecase.GetStreakDataUseCase
import com.furianrt.statistics.internal.ui.entities.GeneralStats
import com.furianrt.statistics.internal.ui.entities.StreakDay
import com.furianrt.statistics.internal.ui.entities.StreakStats
import com.furianrt.statistics.internal.ui.entities.toUI
import com.furianrt.uikit.entities.UiThemeColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
internal class StatsViewModel @Inject constructor(
    appearanceRepository: AppearanceRepository,
    private val getMoodDataUseCase: GetMoodDataUseCase,
    private val getMediaDataUseCase: GetMediaDataUseCase,
    private val getNotesDataUseCase: GetNotesDataUseCase,
    private val getStreakDataUseCase: GetStreakDataUseCase,
) : ViewModel() {

    private val periods = listOf(
        TimePeriod.SEVEN_DAYS,
        TimePeriod.ONE_MONTH,
        TimePeriod.SIX_MONTH,
        TimePeriod.ONE_YEAR,
        TimePeriod.ALL_TIME,
    )

    private val selectedPeriodState = MutableStateFlow(TimePeriod.SEVEN_DAYS)

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<StatsState> = selectedPeriodState.flatMapLatest { period ->
        combine(
            getNotesDataUseCase(period),
            getMediaDataUseCase(period),
            getMoodDataUseCase(period),
            getStreakDataUseCase(),
            appearanceRepository.getAppThemeColorId(),
        ) { notesData, mediaData, moodData, streakData, appThemeId ->
            buildState(
                selectedPeriod = period,
                notesData = notesData,
                mediaData = mediaData,
                moodData = moodData,
                streakData = streakData,
                appThemeColorId = appThemeId,
            )
        }
    }.flowOn(
        context = Dispatchers.Default,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = StatsState(
            theme = UiThemeColor.fromId(appearanceRepository.getAppThemeColorId().value),
            content = StatsState.Content.Loading,
        )
    )

    private val _effect = MutableSharedFlow<StatsEffect>(extraBufferCapacity = 5)
    val effect: SharedFlow<StatsEffect> = _effect.asSharedFlow()

    fun onEvent(event: StatsEvent) {
        when (event) {
            is StatsEvent.OnPeriodSelected -> onPeriodSelected(event.period)
            is StatsEvent.OnButtonBackClick -> onButtonBackClick()
            is StatsEvent.OnStreakDayClick -> onStreakDayClick(event.day)
            is StatsEvent.OnGalleryStatClick -> onGalleryStatClick()
            is StatsEvent.OnMoodStatClick -> onMoodStatClick()
            is StatsEvent.OnNotesStatClick -> onNotesStatClick()
        }
    }

    private fun onPeriodSelected(period: TimePeriod) {
        selectedPeriodState.update { period }
    }

    private fun onButtonBackClick() {
        _effect.tryEmit(StatsEffect.CloseScreen)
    }

    private fun onStreakDayClick(day: StreakDay) {

    }

    private fun onGalleryStatClick() {

    }

    private fun onMoodStatClick() {

    }

    private fun onNotesStatClick() {

    }

    private fun buildState(
        selectedPeriod: TimePeriod,
        notesData: NotesData,
        mediaData: MediaData,
        moodData: MoodData,
        streakData: StreakData,
        appThemeColorId: String?,
    ) = StatsState(
        theme = UiThemeColor.fromId(appThemeColorId),
        content = StatsState.Content.Success(
            periods = periods,
            selectedPeriod = selectedPeriod,
            generalStats = GeneralStats(
                notesCount = notesData.totalNotes,
                notesChange = notesData.change,
                mediaCount = mediaData.totalMedia,
                mediaChange = mediaData.change,
                averageMood = moodData.averageMood,
                moodChange = moodData.change,
            ),
            streakStats = StreakStats(
                currentStreak = streakData.currentStreak,
                longestStreak = streakData.longestStreak,
                streakDays = streakData.streakDays.map(StreakData.StreakDay::toUI),
            )
        ),
    )
}