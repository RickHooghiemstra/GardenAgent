package com.gardenagent.presentation.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gardenagent.domain.provider.CameraProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewViewModel @Inject constructor(
    private val providers: Set<@JvmSuppressWildcards CameraProvider>,
) : ViewModel() {

    private val _rtspUrl = MutableStateFlow<String?>(null)
    val rtspUrl: StateFlow<String?> = _rtspUrl

    private val _cameraName = MutableStateFlow<String?>(null)
    val cameraName: StateFlow<String?> = _cameraName

    fun loadCamera(deviceSn: String) {
        viewModelScope.launch {
            providers.forEach { provider ->
                val camera = provider.getCameras().first().find { it.deviceSn == deviceSn }
                if (camera != null) {
                    _cameraName.value = camera.deviceName
                    _rtspUrl.value = camera.rtspUrl ?: ""
                    return@launch
                }
            }
            _rtspUrl.value = ""
        }
    }
}
