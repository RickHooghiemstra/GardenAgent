package com.gardenagent.domain.usecase.maintenance

import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.gardenagent.data.work.CareReminderWorker
import com.gardenagent.domain.model.MaintenanceTask
import com.gardenagent.domain.repository.MaintenanceTaskRepository
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ScheduleMaintenanceNotificationsUseCase @Inject constructor(
    private val maintenanceTaskRepository: MaintenanceTaskRepository,
    private val workManager: WorkManager,
) {
    suspend operator fun invoke(tasks: List<MaintenanceTask>) {
        maintenanceTaskRepository.replaceTasks(tasks)

        workManager.cancelAllWorkByTag("care_reminder")

        tasks.forEach { task ->
            val delayMs = task.scheduledFor - System.currentTimeMillis()
            if (delayMs <= 0) return@forEach
            val delayHours = TimeUnit.MILLISECONDS.toHours(delayMs)
            val request = CareReminderWorker.buildRequest(
                title = task.plantName?.let { "Garden: $it" } ?: "Garden Reminder",
                message = task.description,
                delayHours = delayHours,
            )
            workManager.enqueueUniqueWork(
                "task_${task.id}",
                ExistingWorkPolicy.REPLACE,
                request,
            )
        }
    }
}
