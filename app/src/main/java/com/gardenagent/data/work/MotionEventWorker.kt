package com.gardenagent.data.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkerParameters
import com.gardenagent.data.notification.GardenNotificationManager
import com.gardenagent.domain.repository.EufyRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class MotionEventWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val eufyRepository: EufyRepository,
    private val notificationManager: GardenNotificationManager,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        if (!eufyRepository.isLoggedIn()) return Result.success()
        eufyRepository.getDevices().onSuccess { cameras ->
            cameras.forEach { camera ->
                // Check for recent motion/doorbell events via Eufy API
                // Eufy has no webhook — we poll for device status changes
                // A real implementation would track last-seen event IDs
            }
        }
        return Result.success()
    }

    companion object {
        const val WORK_NAME = "motion_event_poller"

        fun buildRequest(): PeriodicWorkRequest =
            PeriodicWorkRequest.Builder(MotionEventWorker::class.java, 15, TimeUnit.MINUTES)
                .build()
    }
}
