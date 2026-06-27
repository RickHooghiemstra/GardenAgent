package com.gardenagent.presentation.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gardenagent.domain.provider.CameraProvider
import com.gardenagent.domain.repository.ManualCameraRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CameraListItem(
    val id: String,
    val name: String,
    val location: String?,
    val isManual: Boolean,
    val rtspUrl: String?,
    val isOnline: Boolean = true,
    val isDoorbell: Boolean = false,
    val model: String? = null,
)

data class CameraListUiState(
    val cameras: List<CameraListItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class CameraListViewModel @Inject constructor(
    private val providers: Set<@JvmSuppressWildcards CameraProvider>,
    private val manualCameraRepository: ManualCameraRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CameraListUiState(isLoading = true))
    val uiState: StateFlow<CameraListUiState> = _state

    private val _eufyCameras = MutableStateFlow<List<CameraListItem>>(emptyList())

    init {
        // Collect Eufy cameras from providers
        providers.forEach { provider ->
            viewModelScope.launch {
                provider.getCameras().collect { cameras ->
                    val items = cameras.map { cam ->
                        CameraListItem(
                            id = cam.deviceSn,
                            name = cam.deviceName,
                            location = null,
                            isManual = false,
                            rtspUrl = null,
                            isOnline = cam.isOnline,
                            isDoorbell = cam.isDoorbell,
                            model = cam.deviceModel,
                        )
                    }
                    _eufyCameras.update { existing ->
                        (existing.filter { e -> cameras.none { c -> c.deviceSn == e.id } } + items)
                            .distinctBy { it.id }
                    }
                }
            }
        }

        // Combine Eufy + manual cameras
        viewModelScope.launch {
            combine(
                _eufyCameras,
                manualCameraRepository.getCameras(),
            ) { eufy, manual ->
                val manualItems = manual.map { cam ->
                    CameraListItem(
                        id = "manual_${cam.id}",
                        name = cam.name,
                        location = cam.locationDescription,
                        isManual = true,
                        rtspUrl = cam.rtspUrl,
                        isOnline = true,
                    )
                }
                eufy + manualItems
            }.collect { combined ->
                _state.update { it.copy(cameras = combined) }
            }
        }

        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            providers.forEach { provider ->
                provider.refreshCameras().onFailure { e ->
                    _state.update { it.copy(error = e.message) }
                }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }
}
