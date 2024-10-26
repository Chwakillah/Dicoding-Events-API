package com.fundamentalandroid.dicodingevents.data.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.fundamentalandroid.dicodingevents.R
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
        Log.d(TAG, "doWork: Worker Started")
        return try {
            Log.d(TAG, "doWork: Fetching event data...")
            val eventData = getEventData()
            if (eventData != null) {
                Log.d(TAG, "doWork: Event data fetched successfully: ${eventData.first}")
                showNotification(eventData)
                Log.d(TAG, "doWork: Notification shown successfully")
                Result.success()
            } else {
                Log.e(TAG, "doWork: Failed to fetch event data")
                Result.failure()
            }
        } catch (e: Exception) {
            Log.e(TAG, "doWork: Error executing worker", e)
            Result.failure()
        }
    }

    private fun getEventData(): Pair<String, String>? {
        try {
            Log.d(TAG, "getEventData: Starting API call")
            val url = URL("https://event-api.dicoding.dev/events?active=-1&limit=1")
            val connection = url.openConnection()
            val response = connection.getInputStream().bufferedReader().use { it.readText() }
            Log.d(TAG, "getEventData: API Response received: $response")

            val jsonObject = JSONObject(response)
            val events = jsonObject.getJSONArray("listEvents")

            if (events.length() > 0) {
                val event = events.getJSONObject(0)
                val eventName = event.getString("name")

                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val eventTime = dateFormat.parse(event.getString("beginTime"))
                val displayFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
                val formattedTime = eventTime?.let { displayFormat.format(it) } ?: ""

                Log.d(TAG, "getEventData: Parsed event - Name: $eventName, Time: $formattedTime")
                return Pair(eventName, formattedTime)
            }
        } catch (e: Exception) {
            Log.e(TAG, "getEventData: Error parsing event data", e)
        }
        return null
    }

    private fun showNotification(eventData: Pair<String, String>) {
        Log.d(TAG, "showNotification: Creating notification for event: ${eventData.first}")
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for Event Reminders"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "showNotification: Notification channel created")
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Event Reminder")
            .setContentText("${eventData.first} pada ${eventData.second}")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
        Log.d(TAG, "showNotification: Notification displayed")
    }
}