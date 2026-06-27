package com.gardenagent.presentation.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gardenagent.domain.provider.CameraProvider
import com.gardenagent.domain.repository.ManualCameraRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewViewModel @Inject constructor(
    private val providers: Set<@JvmSuppressWildcards CameraProvider>,
    private val manualCameraRepository: ManualCameraRepository,
) : ViewModel() {

    private val _rtspUrl = MutableStateFlow<String?>(null)
    val rtspUrl: StateFlow<String?> = _rtspUrl

    private val _cameraName = MutableStateFlow<String?>(null)
    val cameraName: StateFlow<String?> = _cameraName

    private var currentSn: String = ""

    fun loadCamera(deviceSn: String) {
        currentSn = deviceSn
        viewModelScope.launch {
            providers.forEach { provider ->
                val camera = provider.getCameras().first().find { it.deviceSn == deviceSn }
                if (camera != null) {
                    _cameraName.value = camera.deviceName
                    _rtspUrl.value = camera.rtspUrl ?: ""
                    return@launch
                }
            }
            if (deviceSn.startsWith("manual_")) {
                val id = deviceSn.removePrefix("manual_").toLongOrNull()
                if (id != null) {
                    val cameras = manualCameraRepository.getCameras().first()
                    val cam = cameras.find { it.id == id }
                    if (cam != null) {
                        _cameraName.value = cam.name
                        _rtspUrl.value = cam.rtspUrl
                        return@launch
                    }
                }
            }
            _rtspUrl.value = ""
        }
    }

    fun retry() {
        _rtspUrl.value = null
        loadCamera(currentSn)
    }
}
