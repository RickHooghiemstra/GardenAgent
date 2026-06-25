package com.gardenagent.presentation.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gardenagent.domain.model.WeatherData
import com.gardenagent.domain.usecase.weather.GetCurrentWeatherUseCase
import com.gardenagent.domain.usecase.weather.RefreshWeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WeatherUiState(
    val weather: WeatherData? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class WeatherViewModel @Inject constructor(
    getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val refreshWeatherUseCase: RefreshWeatherUseCase,
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    val uiState: StateFlow<WeatherUiState> = combine(
        getCurrentWeatherUseCase(),
        _isLoading,
        _error,
    ) { weather, loading, error -> WeatherUiState(weather, loading, error) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WeatherUiState())

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            refreshWeatherUseCase().onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }
}
