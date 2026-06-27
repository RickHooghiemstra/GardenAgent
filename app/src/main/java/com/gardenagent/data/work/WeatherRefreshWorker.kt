package com.gardenagent.data.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.gardenagent.domain.repository.WeatherRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class WeatherRefreshWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val weatherRepository: WeatherRepository,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        weatherRepository.refreshWeather()
        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            "generate_schedule",
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequest.from(GenerateMaintenanceScheduleWorker::class.java),
        )
        return Result.success()
    }

    companion object {
        const val WORK_NAME = "weather_refresh"

        fun buildRequest(): PeriodicWorkRequest =
            PeriodicWorkRequest.Builder(WeatherRefreshWorker::class.java, 30, TimeUnit.MINUTES)
                .build()
    }
}
