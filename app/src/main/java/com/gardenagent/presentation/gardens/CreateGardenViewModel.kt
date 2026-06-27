package com.gardenagent.presentation.gardens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gardenagent.domain.model.Garden
import com.gardenagent.domain.model.GardenType
import com.gardenagent.domain.usecase.garden.CreateGardenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CreateGardenUiState(
    val name: String = "",
    val type: GardenType = GardenType.OUTDOOR,
    val description: String = "",
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class CreateGardenViewModel @Inject constructor(
    private val createGardenUseCase: CreateGardenUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateGardenUiState())
    val uiState: StateFlow<CreateGardenUiState> = _uiState

    fun onNameChange(name: String) { _uiState.value = _uiState.value.copy(name = name) }
    fun onTypeChange(type: GardenType) { _uiState.value = _uiState.value.copy(type = type) }
    fun onDescriptionChange(desc: String) { _uiState.value = _uiState.value.copy(description = desc) }

    fun save() {
        val name = _uiState.value.name.trim()
        if (name.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Name is required")
            return
        }
        _uiState.value = _uiState.value.copy(isSaving = true, error = null)
        viewModelScope.launch {
            runCatching {
                createGardenUseCase(Garden(
                    name = name,
                    type = _uiState.value.type,
                    description = _uiState.value.description.trim().takeIf { it.isNotBlank() },
                ))
            }.onSuccess {
                _uiState.value = _uiState.value.copy(isSaving = false, saved = true)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(isSaving = false, error = e.message)
            }
        }
    }
}
