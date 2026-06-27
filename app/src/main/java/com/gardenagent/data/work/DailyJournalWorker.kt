package com.gardenagent.data.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkerParameters
import com.gardenagent.domain.model.JournalEntry
import com.gardenagent.domain.repository.GardenAdviceRepository
import com.gardenagent.domain.repository.JournalRepository
import com.gardenagent.domain.repository.PlantRepository
import com.gardenagent.domain.repository.SensorRepository
import com.gardenagent.domain.repository.WeatherRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

@HiltWorker
class DailyJournalWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val gardenAdviceRepository: GardenAdviceRepository,
    private val sensorRepository: SensorRepository,
    private val weatherRepository: WeatherRepository,
    private val plantRepository: PlantRepository,
    private val journalRepository: JournalRepository,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val plants = plantRepository.getAllPlants().first()
        val weather = weatherRepository.getLatestWeather().first()
        val sensors = sensorRepository.getLatestReadings()

        gardenAdviceRepository.getDailyJournalEntry(weather, sensors, plants)
            .onSuccess { text ->
                journalRepository.insertEntry(JournalEntry(
                    notes = text,
                    capturedAt = System.currentTimeMillis(),
                    isAiGenerated = true,
                ))
            }
        return Result.success()
    }

    companion object {
        const val WORK_NAME = "daily_journal"

        fun buildRequest(): PeriodicWorkRequest =
            PeriodicWorkRequest.Builder(DailyJournalWorker::class.java, 24, TimeUnit.HOURS)
                .setInitialDelay(calculateDelayToEightAM(), TimeUnit.MILLISECONDS)
                .build()

        private fun calculateDelayToEightAM(): Long {
            val now = java.util.Calendar.getInstance()
            val next8am = java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.HOUR_OF_DAY, 8)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                if (before(now)) add(java.util.Calendar.DAY_OF_YEAR, 1)
            }
            return next8am.timeInMillis - now.timeInMillis
        }
    }
}
