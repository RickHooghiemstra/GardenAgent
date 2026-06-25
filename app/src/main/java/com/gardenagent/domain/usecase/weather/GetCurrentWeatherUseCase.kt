package com.gardenagent.domain.usecase.weather

import com.gardenagent.domain.model.WeatherData
import com.gardenagent.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentWeatherUseCase @Inject constructor(private val repository: WeatherRepository) {
    operator fun invoke(): Flow<WeatherData?> = repository.getLatestWeather()
}
