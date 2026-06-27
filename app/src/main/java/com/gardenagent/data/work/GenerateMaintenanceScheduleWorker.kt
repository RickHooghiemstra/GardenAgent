package com.gardenagent.data.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gardenagent.domain.usecase.maintenance.GenerateMaintenanceScheduleUseCase
import com.gardenagent.domain.usecase.maintenance.ScheduleMaintenanceNotificationsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class GenerateMaintenanceScheduleWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val generateSchedule: GenerateMaintenanceScheduleUseCase,
    private val scheduleNotifications: ScheduleMaintenanceNotificationsUseCase,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val tasks = generateSchedule().getOrElse { return Result.retry() }
        scheduleNotifications(tasks)
        return Result.success()
    }
}
