package com.gardenagent.presentation.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gardenagent.domain.model.EufyCamera
import com.gardenagent.domain.provider.CameraProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CameraListUiState(
    val cameras: List<EufyCamera> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class CameraListViewModel @Inject constructor(
    private val providers: Set<@JvmSuppressWildcards CameraProvider>,
) : ViewModel() {

    private val _state = MutableStateFlow(CameraListUiState(isLoading = true))
    val uiState: StateFlow<CameraListUiState> = _state

    init {
        providers.forEach { provider ->
            viewModelScope.launch {
                provider.getCameras().collect { cameras ->
                    _state.update { state ->
                        state.copy(cameras = (state.cameras + cameras).distinctBy { it.deviceSn })
                    }
                }
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
