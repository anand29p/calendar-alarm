package com.calendaralarm.app.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.calendaralarm.app.data.models.CalendarEvent
import com.calendaralarm.app.utils.Constants
import com.calendaralarm.app.utils.NotificationHelper
import java.util.Date

/**
 * Worker class for showing event notifications
 */
class NotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    private val TAG = "NotificationWorker"
    private val notificationHelper = NotificationHelper(context)
    
    override suspend fun doWork(): Result {
        try {
            // Get event details from input data
            val eventId = inputData.getString(Constants.EVENT_ID_KEY) ?: return Result.failure()
            val eventTitle = inputData.getString(Constants.EVENT_TITLE_KEY) ?: "Untitled Event"
            val eventStartTime = inputData.getLong(Constants.EVENT_START_TIME_KEY, 0)
            val eventLocation = inputData.getString(Constants.EVENT_LOCATION_KEY)
            val notificationType = inputData.getString(Constants.NOTIFICATION_TYPE_KEY)
            
            if (eventStartTime == 0L) {
                Log.e(TAG, "Invalid event start time")
                return Result.failure()
            }
            
            // Create a CalendarEvent object
            val event = CalendarEvent(
                id = eventId,
                title = eventTitle,
                startTime = Date(eventStartTime),
                endTime = Date(eventStartTime + 3600000), // Add 1 hour as default duration
                location = eventLocation,
                calendarId = "",
                calendarName = ""
            )
            
            // Show the appropriate notification based on the type
            when (notificationType) {
                Constants.NOTIFICATION_TYPE_10MIN -> {
                    notificationHelper.showUpcomingEventNotification(event)
                    Log.d(TAG, "Showing 10-minute notification for event: $eventTitle")
                }
                Constants.NOTIFICATION_TYPE_NOW -> {
                    notificationHelper.showEventStartingNotification(event)
                    Log.d(TAG, "Showing start-time notification for event: $eventTitle")
                }
                else -> {
                    Log.e(TAG, "Unknown notification type: $notificationType")
                    return Result.failure()
                }
            }
            
            return Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error showing notification", e)
            return Result.failure()
        }
    }
}
