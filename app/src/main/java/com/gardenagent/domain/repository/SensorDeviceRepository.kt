package com.gardenagent.domain.repository

import com.gardenagent.domain.model.SensorDevice
import kotlinx.coroutines.flow.Flow

interface SensorDeviceRepository {
    fun getDevices(): Flow<List<SensorDevice>>
    suspend fun getDeviceByAddress(address: String): SensorDevice?
    suspend fun registerDevice(device: SensorDevice)
    suspend fun deleteDevice(device: SensorDevice)
}
