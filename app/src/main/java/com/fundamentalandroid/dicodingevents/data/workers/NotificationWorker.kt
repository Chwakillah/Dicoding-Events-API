package com.fundamentalandroid.dicodingevents.data.workers

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.fundamentalandroid.dicodingevents.R
import com.fundamentalandroid.dicodingevents.ui.main.MainActivity
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Locale

class NotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    companion object {
        private val TAG = NotificationWorker::class.java.simpleName
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "event_reminder_channel"
        const val CHANNEL_NAME = "Event Reminder"
    }

    override fun doWork(): Result {
        return try {
            val eventData = getEventData()
            if (eventData != null) {
                showNotification(eventData)
                Result.success()
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun getEventData(): Pair<String, String>? {
        try {
            val url = URL("https://event-api.dicoding.dev/events?active=-1&limit=1")
            val connection = url.openConnection()
            val response = connection.getInputStream().bufferedReader().use { it.readText() }

            val jsonObject = JSONObject(response)
            val events = jsonObject.getJSONArray("listEvents")

            if (events.length() > 0) {
                val event = events.getJSONObject(0)
                val eventName = event.getString("name")

                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val eventTime = dateFormat.parse(event.getString("beginTime"))
                val displayFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
                val formattedTime = eventTime?.let { displayFormat.format(it) } ?: ""

                return Pair(eventName, formattedTime)
            }
        } catch (e: Exception) {
            Log.e(TAG, "getEventData: Error parsing event data", e)
        }
        return null
    }

    private fun showNotification(eventData: Pair<String, String>) {
        try {
            val intent = Intent(applicationContext, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val pendingIntent = PendingIntent.getActivity(
                applicationContext,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )

            Log.d(TAG, "showNotification: Creating notification for event: ${eventData.first}")
            val notificationManager = NotificationManagerCompat.from(applicationContext)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Channel for Event Reminders"
                    enableLights(true)
                    enableVibration(true)
                    setShowBadge(true)
                }
                val systemNotificationManager =
                    applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                systemNotificationManager.createNotificationChannel(channel)
                Log.d(TAG, "showNotification: Notification channel created")
            }

            val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Event Reminder")
                .setContentText(eventData.first)
                .setStyle(NotificationCompat.BigTextStyle()
                    .bigText("${eventData.first}\nWaktu: ${eventData.second}"))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ActivityCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    notificationManager.notify(NOTIFICATION_ID, notification)
                    Log.d(TAG, "showNotification: Notification displayed")
                } else {
                    Log.e(TAG, "showNotification: Notification permission not granted")
                }
            } else {
                notificationManager.notify(NOTIFICATION_ID, notification)
                Log.d(TAG, "showNotification: Notification displayed")
            }
        } catch (e: Exception) {
            Log.e(TAG, "showNotification: Error showing notification", e)
        }
    }
}