package com.gardenagent.presentation.plants

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gardenagent.domain.model.Plant
import com.gardenagent.domain.usecase.plant.AddPlantUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddPlantUiState(
    val commonName: String = "",
    val scientificName: String = "",
    val location: String = "",
    val notes: String = "",
    val photoPath: String? = null,
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class AddPlantViewModel @Inject constructor(
    private val addPlantUseCase: AddPlantUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddPlantUiState())
    val uiState: StateFlow<AddPlantUiState> = _uiState

    fun onNameChange(name: String) { _uiState.value = _uiState.value.copy(commonName = name) }
    fun onScientificNameChange(name: String) { _uiState.value = _uiState.value.copy(scientificName = name) }
    fun onLocationChange(loc: String) { _uiState.value = _uiState.value.copy(location = loc) }
    fun onNotesChange(notes: String) { _uiState.value = _uiState.value.copy(notes = notes) }
    fun onPhotoTaken(path: String) { _uiState.value = _uiState.value.copy(photoPath = path) }

    fun save() {
        val state = _uiState.value
        if (state.commonName.isBlank()) {
            _uiState.value = state.copy(error = "Plant name is required")
            return
        }
        _uiState.value = state.copy(isSaving = true, error = null)
        viewModelScope.launch {
            val plant = Plant(
                commonName = state.commonName.trim(),
                scientificName = state.scientificName.takeIf { it.isNotBlank() },
                locationInGarden = state.location.takeIf { it.isNotBlank() },
                notes = state.notes.takeIf { it.isNotBlank() },
                photoPath = state.photoPath,
            )
            addPlantUseCase(plant)
            _uiState.value = _uiState.value.copy(isSaving = false, saved = true)
        }
    }
}
