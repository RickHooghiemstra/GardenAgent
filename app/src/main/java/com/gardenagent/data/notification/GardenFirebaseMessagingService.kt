package com.gardenagent.data.notification

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GardenFirebaseMessagingService : FirebaseMessagingService() {

    @Inject lateinit var notificationManager: GardenNotificationManager

    override fun onMessageReceived(message: RemoteMessage) {
        val type = message.data["type"]
        val camera = message.data["camera"]?.take(100) ?: "Camera"
        when (type) {
            "motion" -> notificationManager.showMotionDetected(camera)
            "doorbell" -> notificationManager.showDoorbellRing(camera)
            "reminder" -> {
                val title = message.data["title"]?.take(100) ?: "Garden Reminder"
                val body = (message.notification?.body ?: message.data["body"] ?: "").take(500)
                notificationManager.showCareReminder(title, body)
            }
        }
    }

    override fun onNewToken(token: String) {
        // Token would be sent to backend if server-side push is implemented
    }
}
