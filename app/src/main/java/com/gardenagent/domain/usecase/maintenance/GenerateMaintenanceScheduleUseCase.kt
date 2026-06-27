package com.gardenagent.domain.usecase.maintenance

import com.gardenagent.domain.model.MaintenanceTask
import com.gardenagent.domain.repository.GardenAdviceRepository
import com.gardenagent.domain.repository.JournalRepository
import com.gardenagent.domain.repository.PlantRepository
import com.gardenagent.domain.repository.SensorRepository
import com.gardenagent.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

private const val FOUR_WEEKS_MS = 28L * 24 * 60 * 60 * 1000

class GenerateMaintenanceScheduleUseCase @Inject constructor(
    private val adviceRepository: GardenAdviceRepository,
    private val weatherRepository: WeatherRepository,
    private val sensorRepository: SensorRepository,
    private val journalRepository: JournalRepository,
    private val plantRepository: PlantRepository,
) {
    suspend operator fun invoke(): Result<List<MaintenanceTask>> {
        val forecast = weatherRepository.getForecastDays()
        val sinceMs = System.currentTimeMillis() - FOUR_WEEKS_MS
        val sensorHistory = sensorRepository.getReadingsSince(sinceMs)
        val journalEntries = journalRepository.getEntriesSince(sinceMs, limit = 30)
        val plants = plantRepository.getAllPlants().first()
        return adviceRepository.getMaintenanceSchedule(forecast, sensorHistory, journalEntries, plants)
    }
}
