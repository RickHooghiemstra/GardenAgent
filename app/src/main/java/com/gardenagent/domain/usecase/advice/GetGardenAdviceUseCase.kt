package com.gardenagent.domain.usecase.advice

import com.gardenagent.domain.model.GardenAdvice
import com.gardenagent.domain.repository.GardenAdviceRepository
import com.gardenagent.domain.repository.PlantRepository
import com.gardenagent.domain.repository.SensorRepository
import com.gardenagent.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetGardenAdviceUseCase @Inject constructor(
    private val adviceRepository: GardenAdviceRepository,
    private val weatherRepository: WeatherRepository,
    private val sensorRepository: SensorRepository,
    private val plantRepository: PlantRepository,
) {
    suspend operator fun invoke(): Result<GardenAdvice> {
        val weather = weatherRepository.getLatestWeather().first()
        val sensors = sensorRepository.getRecentReadings(limit = 20).first()
        val plants = plantRepository.getAllPlants().first()
        return adviceRepository.getAdvice(weather, sensors, plants)
    }
}
