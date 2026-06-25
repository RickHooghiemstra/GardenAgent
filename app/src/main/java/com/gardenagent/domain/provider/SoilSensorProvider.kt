package com.gardenagent.domain.provider

import com.gardenagent.domain.model.SensorReading
import kotlinx.coroutines.flow.Flow

data class SensorScanResult(
    val address: String,
    val name: String?,
    val rssi: Int,
)

interface SoilSensorProvider {
    fun scan(): Flow<SensorScanResult>
    suspend fun readSensor(address: String, name: String?): Result<SensorReading>
    fun stopScan()
}
