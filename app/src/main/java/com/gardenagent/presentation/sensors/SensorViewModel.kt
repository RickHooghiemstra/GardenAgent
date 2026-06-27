package com.gardenagent.presentation.sensors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gardenagent.domain.model.SensorDevice
import com.gardenagent.domain.model.SensorReading
import com.gardenagent.domain.provider.SensorScanResult
import com.gardenagent.domain.repository.SensorDeviceRepository
import com.gardenagent.domain.repository.SensorRepository
import com.gardenagent.domain.usecase.sensor.ReadSensorDataUseCase
import com.gardenagent.domain.usecase.sensor.ScanForSensorsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SensorUiState(
    val discoveredDevices: List<SensorScanResult> = emptyList(),
    val readings: Map<String, SensorReading> = emptyMap(),
    val registeredDevices: Map<String, SensorDevice> = emptyMap(),
    val isScanning: Boolean = false,
    val readingAddress: String? = null,
    val error: String? = null,
)

@HiltViewModel
class SensorViewModel @Inject constructor(
    private val scanForSensorsUseCase: ScanForSensorsUseCase,
    private val readSensorDataUseCase: ReadSensorDataUseCase,
    private val sensorRepository: SensorRepository,
    private val sensorDeviceRepository: SensorDeviceRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(SensorUiState())
    val uiState: StateFlow<SensorUiState> = _state

    private var scanJob: Job? = null

    init {
        viewModelScope.launch {
            sensorRepository.getRecentReadings(20).collect { readings ->
                val map = readings.associateBy { it.deviceAddress }
                _state.update { it.copy(readings = map) }
            }
        }
        viewModelScope.launch {
            sensorDeviceRepository.getDevices().collect { devices ->
                val map = devices.associateBy { it.address }
                _state.update { it.copy(registeredDevices = map) }
            }
        }
    }

    fun startScan() {
        if (_state.value.isScanning) return
        _state.update { it.copy(isScanning = true, discoveredDevices = emptyList(), error = null) }
        scanJob = viewModelScope.launch {
            scanForSensorsUseCase()
                .catch { e -> _state.update { it.copy(error = e.message, isScanning = false) } }
                .collect { result ->
                    _state.update { state ->
                        if (state.discoveredDevices.none { it.address == result.address })
                            state.copy(discoveredDevices = state.discoveredDevices + result)
                        else state
                    }
                }
        }
    }

    fun stopScan() {
        scanJob?.cancel()
        _state.update { it.copy(isScanning = false) }
    }

    fun readDevice(address: String, name: String?) {
        viewModelScope.launch {
            _state.update { it.copy(readingAddress = address, error = null) }
            readSensorDataUseCase(address, name)
                .onSuccess { reading ->
                    _state.update { state ->
                        state.copy(
                            readingAddress = null,
                            readings = state.readings + (address to reading),
                        )
                    }
                }
                .onFailure { e ->
                    _state.update { it.copy(readingAddress = null, error = e.message) }
                }
        }
    }

    fun registerDevice(address: String, name: String, location: String, brand: String = "") {
        viewModelScope.launch {
            sensorDeviceRepository.registerDevice(SensorDevice(
                address = address,
                deviceName = name.trim().ifBlank { brand },
                locationDescription = location.trim().takeIf { it.isNotBlank() },
            ))
        }
    }

    fun deleteDevice(device: SensorDevice) {
        viewModelScope.launch { sensorDeviceRepository.deleteDevice(device) }
    }

    fun registerManualDevice(name: String, location: String) {
        // Use a synthetic address so it appears in the registered list but has no BLE address
        val syntheticAddress = "manual:${System.currentTimeMillis()}"
        viewModelScope.launch {
            sensorDeviceRepository.registerDevice(SensorDevice(
                address = syntheticAddress,
                deviceName = name.trim(),
                locationDescription = location.trim().takeIf { it.isNotBlank() },
            ))
        }
    }
}
