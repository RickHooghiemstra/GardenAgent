package com.gardenagent.domain.usecase.weather

import com.gardenagent.domain.model.WeatherData
import com.gardenagent.domain.repository.WeatherRepository
import javax.inject.Inject

class RefreshWeatherUseCase @Inject constructor(private val repository: WeatherRepository) {
    suspend operator fun invoke(): Result<WeatherData> = repository.refreshWeather()
}
