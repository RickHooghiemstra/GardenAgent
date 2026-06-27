package com.gardenagent

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.gardenagent.data.work.DailyJournalWorker
import com.gardenagent.data.work.MotionEventWorker
import com.gardenagent.data.work.WeatherRefreshWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class GardenAgentApp : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
        scheduleBackgroundWork()
    }

    private fun scheduleBackgroundWork() {
        val workManager = WorkManager.getInstance(this)
        workManager.enqueueUniquePeriodicWork(
            WeatherRefreshWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            WeatherRefreshWorker.buildRequest(),
        )
        workManager.enqueueUniquePeriodicWork(
            MotionEventWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            MotionEventWorker.buildRequest(),
        )
        workManager.enqueueUniquePeriodicWork(
            DailyJournalWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            DailyJournalWorker.buildRequest(),
        )
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = getSystemService(NotificationManager::class.java)
        listOf(
            NotificationChannel("motion_events", "Motion Events", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Eufy camera motion detection alerts"
            },
            NotificationChannel("doorbell_alerts", "Doorbell", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Eufy doorbell ring notifications"
            },
            NotificationChannel("care_reminders", "Care Reminders", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Garden watering and fertilizing reminders"
            },
            NotificationChannel("watering_reminders", "Watering", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Watering reminders"
            },
            NotificationChannel("fertilizing_reminders", "Fertilizing", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Fertilizing reminders"
            },
            NotificationChannel("mowing_reminders", "Mowing", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Mowing reminders"
            },
            NotificationChannel("compost_reminders", "Compost", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Compost turning reminders"
            },
            NotificationChannel("pesticides_reminders", "Pesticides", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Pesticide application reminders"
            },
            NotificationChannel("daily_journal", "Daily Journal", NotificationManager.IMPORTANCE_LOW).apply {
                description = "Daily AI-generated garden journal entries"
            },
        ).forEach { manager.createNotificationChannel(it) }
    }
}
