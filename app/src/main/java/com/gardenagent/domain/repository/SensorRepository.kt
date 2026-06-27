package com.gardenagent.domain.repository

import com.gardenagent.domain.model.SensorReading
import kotlinx.coroutines.flow.Flow

interface SensorRepository {
    fun getRecentReadings(limit: Int = 100): Flow<List<SensorReading>>
    fun getLatestReadingForDevice(address: String): Flow<SensorReading?>
    suspend fun saveReading(reading: SensorReading)
    suspend fun deleteOldReadings(cutoffMs: Long)
    suspend fun getLatestReadings(): List<SensorReading>
}
