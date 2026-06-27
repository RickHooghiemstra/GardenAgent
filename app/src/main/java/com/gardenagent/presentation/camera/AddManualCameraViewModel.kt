package com.gardenagent.presentation.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gardenagent.domain.model.ManualCamera
import com.gardenagent.domain.repository.ManualCameraRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddManualCameraUiState(
    val name: String = "",
    val rtspUrl: String = "",
    val locationDescription: String = "",
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class AddManualCameraViewModel @Inject constructor(
    private val repository: ManualCameraRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddManualCameraUiState())
    val uiState: StateFlow<AddManualCameraUiState> = _uiState

    fun onNameChange(name: String) { _uiState.value = _uiState.value.copy(name = name) }
    fun onRtspUrlChange(url: String) { _uiState.value = _uiState.value.copy(rtspUrl = url) }
    fun onLocationChange(location: String) { _uiState.value = _uiState.value.copy(locationDescription = location) }

    fun save() {
        val name = _uiState.value.name.trim()
        val url = _uiState.value.rtspUrl.trim()
        if (name.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Name is required")
            return
        }
        if (url.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "RTSP URL is required")
            return
        }
        _uiState.value = _uiState.value.copy(isSaving = true, error = null)
        viewModelScope.launch {
            runCatching {
                repository.addCamera(ManualCamera(
                    name = name,
                    rtspUrl = url,
                    locationDescription = _uiState.value.locationDescription.trim().takeIf { it.isNotBlank() },
                ))
            }.onSuccess {
                _uiState.value = _uiState.value.copy(isSaving = false, saved = true)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(isSaving = false, error = e.message)
            }
        }
    }
}
