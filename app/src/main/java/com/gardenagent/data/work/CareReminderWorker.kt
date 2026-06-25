package com.gardenagent.data.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkerParameters
import com.gardenagent.data.notification.GardenNotificationManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class CareReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationManager: GardenNotificationManager,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val title = inputData.getString(KEY_TITLE) ?: "Garden Reminder"
        val message = inputData.getString(KEY_MESSAGE) ?: return Result.success()
        notificationManager.showCareReminder(title, message)
        return Result.success()
    }

    companion object {
        const val KEY_TITLE = "title"
        const val KEY_MESSAGE = "message"

        fun buildRequest(title: String, message: String, delayHours: Long = 0): OneTimeWorkRequest =
            OneTimeWorkRequest.Builder(CareReminderWorker::class.java)
                .setInitialDelay(delayHours, TimeUnit.HOURS)
                .setInputData(Data.Builder()
                    .putString(KEY_TITLE, title)
                    .putString(KEY_MESSAGE, message)
                    .build())
                .build()
    }
}
