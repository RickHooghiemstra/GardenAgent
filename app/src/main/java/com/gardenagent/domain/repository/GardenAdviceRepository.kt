package com.gardenagent.domain.repository

import com.gardenagent.domain.model.GardenAdvice
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
}
