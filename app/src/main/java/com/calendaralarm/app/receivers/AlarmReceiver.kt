package com.calendaralarm.app.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.calendaralarm.app.data.models.CalendarEvent
import com.calendaralarm.app.utils.Constants
import com.calendaralarm.app.utils.NotificationHelper
import java.util.Date

/**
 * Broadcast receiver for alarm notifications
 */
class AlarmReceiver : BroadcastReceiver() {
    
    private val TAG = "AlarmReceiver"
    
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Received alarm intent: ${intent.action}")
        
        try {
            // Get event details from intent extras
            val eventId = intent.getStringExtra(Constants.EVENT_ID_KEY) ?: return
            val eventTitle = intent.getStringExtra(Constants.EVENT_TITLE_KEY) ?: "Untitled Event"
            val eventStartTime = intent.getLongExtra(Constants.EVENT_START_TIME_KEY, 0)
            val eventLocation = intent.getStringExtra(Constants.EVENT_LOCATION_KEY)
            val notificationType = intent.getStringExtra(Constants.NOTIFICATION_TYPE_KEY)
            
            if (eventStartTime == 0L) {
                Log.e(TAG, "Invalid event start time")
                return
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
            val notificationHelper = NotificationHelper(context)
            
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
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error showing notification", e)
        }
    }
}
