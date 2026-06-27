package com.gardenagent.domain.repository

import com.gardenagent.domain.model.ForecastDay
import com.gardenagent.domain.model.GardenAdvice
import com.gardenagent.domain.model.JournalEntry
import com.gardenagent.domain.model.MaintenanceTask
import com.gardenagent.domain.model.Plant
import com.gardenagent.domain.model.SensorReading
import com.gardenagent.domain.model.WeatherData

interface GardenAdviceRepository {
    suspend fun getAdvice(
        weather: WeatherData?,
        sensorReadings: List<SensorReading>,
        plants: List<Plant>,
    ): Result<GardenAdvice>

    suspend fun getDailyJournalEntry(
        weather: WeatherData?,
        sensorReadings: List<SensorReading>,
        plants: List<Plant>,
    ): Result<String>

    suspend fun getMaintenanceSchedule(
        forecast: List<ForecastDay>,
        recentSensorHistory: List<SensorReading>,
        recentJournalEntries: List<JournalEntry>,
        plants: List<Plant>,
    ): Result<List<MaintenanceTask>>
}
