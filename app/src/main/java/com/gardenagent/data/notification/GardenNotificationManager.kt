package com.gardenagent.data.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.gardenagent.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GardenNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val manager = context.getSystemService(NotificationManager::class.java)
    private var notificationId = 1000

    private fun mainPendingIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    fun showMotionDetected(cameraName: String) {
        val notification = NotificationCompat.Builder(context, "motion_events")
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .setContentTitle("Motion Detected")
            .setContentText("Movement detected by $cameraName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(mainPendingIntent())
            .setAutoCancel(true)
            .build()
        manager.notify(notificationId++, notification)
    }

    fun showDoorbellRing(cameraName: String) {
        val notification = NotificationCompat.Builder(context, "doorbell_alerts")
            .setSmallIcon(android.R.drawable.ic_menu_call)
            .setContentTitle("Doorbell")
            .setContentText("$cameraName is ringing")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(mainPendingIntent())
            .setAutoCancel(true)
            .build()
        manager.notify(notificationId++, notification)
    }

    fun showCareReminder(title: String, message: String) {
        val notification = NotificationCompat.Builder(context, "care_reminders")
            .setSmallIcon(android.R.drawable.ic_menu_today)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(mainPendingIntent())
            .setAutoCancel(true)
            .build()
        manager.notify(notificationId++, notification)
    }
}
