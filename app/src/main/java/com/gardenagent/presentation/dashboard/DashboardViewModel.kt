package com.gardenagent.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gardenagent.domain.model.GardenAdvice
import com.gardenagent.domain.model.JournalEntry
import com.gardenagent.domain.model.MaintenanceTask
import com.gardenagent.domain.model.WeatherData
import com.gardenagent.domain.repository.JournalRepository
import com.gardenagent.domain.repository.MaintenanceTaskRepository
import com.gardenagent.domain.usecase.advice.GetGardenAdviceUseCase
import com.gardenagent.domain.usecase.maintenance.GenerateMaintenanceScheduleUseCase
import com.gardenagent.domain.usecase.maintenance.ScheduleMaintenanceNotificationsUseCase
import com.gardenagent.domain.usecase.weather.GetCurrentWeatherUseCase
import com.gardenagent.domain.usecase.weather.RefreshWeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val weather: WeatherData? = null,
    val advice: GardenAdvice? = null,
    val recentEntries: List<JournalEntry> = emptyList(),
    val upcomingTasks: List<MaintenanceTask> = emptyList(),
    val isLoadingWeather: Boolean = false,
    val isLoadingAdvice: Boolean = false,
    val weatherError: String? = null,
    val adviceError: String? = null,
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val refreshWeatherUseCase: RefreshWeatherUseCase,
    private val getGardenAdviceUseCase: GetGardenAdviceUseCase,
    private val generateMaintenanceSchedule: GenerateMaintenanceScheduleUseCase,
    private val scheduleNotifications: ScheduleMaintenanceNotificationsUseCase,
    journalRepository: JournalRepository,
    maintenanceTaskRepository: MaintenanceTaskRepository,
) : ViewModel() {

    private val _advice = MutableStateFlow<GardenAdvice?>(null)
    private val _isLoadingWeather = MutableStateFlow(false)
    private val _isLoadingAdvice = MutableStateFlow(false)
    private val _weatherError = MutableStateFlow<String?>(null)
    private val _adviceError = MutableStateFlow<String?>(null)

    val uiState: StateFlow<DashboardUiState> = combine(
        getCurrentWeatherUseCase(),
        _advice,
        journalRepository.getRecentEntries(5),
        maintenanceTaskRepository.getUpcomingTasks(),
        combine(_isLoadingWeather, _isLoadingAdvice) { lw, la -> lw to la },
    ) { weather, advice, entries, tasks, (loadingWeather, loadingAdvice) ->
        DashboardUiState(
            weather = weather,
            advice = advice,
            recentEntries = entries,
            upcomingTasks = tasks,
            isLoadingWeather = loadingWeather,
            isLoadingAdvice = loadingAdvice,
            weatherError = _weatherError.value,
            adviceError = _adviceError.value,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardUiState())

    init {
        refreshWeather()
    }

    fun refreshWeather() {
        viewModelScope.launch {
            _isLoadingWeather.value = true
            _weatherError.value = null
            refreshWeatherUseCase().onFailure { _weatherError.value = it.message }
            _isLoadingWeather.value = false
        }
    }

    fun refreshAdvice() {
        viewModelScope.launch {
            _isLoadingAdvice.value = true
            _adviceError.value = null
            getGardenAdviceUseCase()
                .onSuccess { _advice.value = it }
                .onFailure { _adviceError.value = it.message }
            generateMaintenanceSchedule()
                .onSuccess { tasks -> scheduleNotifications(tasks) }
            _isLoadingAdvice.value = false
        }
    }
}
