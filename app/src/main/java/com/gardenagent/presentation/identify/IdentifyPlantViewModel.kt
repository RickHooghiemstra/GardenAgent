package com.gardenagent.presentation.identify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gardenagent.domain.model.Plant
import com.gardenagent.domain.model.PlantIdentification
import com.gardenagent.domain.usecase.identification.IdentifyPlantUseCase
import com.gardenagent.domain.usecase.plant.AddPlantUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class IdentifyUiState(
    val photoPath: String? = null,
    val results: List<PlantIdentification> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val addedPlantName: String? = null,
)

@HiltViewModel
class IdentifyPlantViewModel @Inject constructor(
    private val identifyPlantUseCase: IdentifyPlantUseCase,
    private val addPlantUseCase: AddPlantUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(IdentifyUiState())
    val uiState: StateFlow<IdentifyUiState> = _uiState

    fun onPhotoTaken(path: String) {
        _uiState.value = _uiState.value.copy(photoPath = path, results = emptyList(), error = null)
    }

    fun identify() {
        val path = _uiState.value.photoPath ?: return
        _uiState.value = _uiState.value.copy(isLoading = true, error = null, results = emptyList())
        viewModelScope.launch {
            identifyPlantUseCase(listOf(path))
                .onSuccess { results ->
                    _uiState.value = _uiState.value.copy(isLoading = false, results = results)
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                }
        }
    }

    fun addToGarden(result: PlantIdentification) {
        viewModelScope.launch {
            addPlantUseCase(Plant(
                commonName = result.commonName,
                scientificName = result.scientificName.takeIf { it.isNotBlank() },
                family = result.family.takeIf { it.isNotBlank() },
                photoPath = _uiState.value.photoPath,
            ))
            _uiState.value = _uiState.value.copy(addedPlantName = result.commonName)
        }
    }

    fun clearAddedPlant() { _uiState.value = _uiState.value.copy(addedPlantName = null) }
}
