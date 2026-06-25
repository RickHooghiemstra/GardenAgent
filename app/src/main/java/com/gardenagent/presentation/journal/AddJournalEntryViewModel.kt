package com.gardenagent.presentation.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gardenagent.domain.model.JournalEntry
import com.gardenagent.domain.repository.WeatherRepository
import com.gardenagent.domain.usecase.journal.AddJournalEntryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEntryUiState(
    val notes: String = "",
    val photoPath: String? = null,
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class AddJournalEntryViewModel @Inject constructor(
    private val addEntryUseCase: AddJournalEntryUseCase,
    private val weatherRepository: WeatherRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEntryUiState())
    val uiState: StateFlow<AddEntryUiState> = _uiState

    fun onNotesChange(notes: String) { _uiState.value = _uiState.value.copy(notes = notes) }
    fun onPhotoTaken(path: String) { _uiState.value = _uiState.value.copy(photoPath = path) }

    fun save(plantId: Long?) {
        val state = _uiState.value
        if (state.notes.isBlank() && state.photoPath == null) {
            _uiState.value = state.copy(error = "Add a photo or notes")
            return
        }
        _uiState.value = state.copy(isSaving = true, error = null)
        viewModelScope.launch {
            val weather = weatherRepository.getLatestWeather().first()
            val entry = JournalEntry(
                plantId = plantId,
                notes = state.notes.takeIf { it.isNotBlank() },
                photoPath = state.photoPath,
                weatherCondition = if (weather != null) "T: ${weather.temperatureCelsius.toInt()}°C" else null,
                temperatureCelsius = weather?.temperatureCelsius,
            )
            addEntryUseCase(entry)
            _uiState.value = _uiState.value.copy(isSaving = false, saved = true)
        }
    }
}
