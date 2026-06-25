package com.gardenagent.domain.repository

import com.gardenagent.domain.model.WeatherData
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getLatestWeather(): Flow<WeatherData?>
    fun getRecentWeather(limit: Int = 48): Flow<List<WeatherData>>
    suspend fun refreshWeather(): Result<WeatherData>
}
