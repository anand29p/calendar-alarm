package com.calendaralarm.app.workers

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.calendaralarm.app.data.models.CalendarEvent
import com.calendaralarm.app.data.repository.CalendarRepository
import com.calendaralarm.app.utils.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date
import java.util.concurrent.TimeUnit

/**
 * Worker class for syncing calendar events and scheduling notifications
 */
class CalendarSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    private val TAG = "CalendarSyncWorker"
    private val repository = CalendarRepository(context)
    private val workManager = WorkManager.getInstance(context)
    private val prefs: SharedPreferences = context.getSharedPreferences(
        Constants.PREFS_NAME,
        Context.MODE_PRIVATE
    )
    
    override suspend fun doWork(): Result {
        try {
            Log.d(TAG, "Starting calendar sync")
            
            // Check if we have calendar permission
            if (!repository.hasCalendarPermission()) {
                Log.e(TAG, "Calendar permission not granted")
                return Result.failure()
            }
            
            // Get selected calendar IDs from preferences
            val selectedCalendarIds = getSelectedCalendarIds()
            
            // Fetch events from device calendar
            val events = repository.getUpcomingEvents(selectedCalendarIds)
            
            // Fetch events from Google Calendar if signed in
            val googleAccount = repository.getSignedInAccount()
            if (googleAccount != null) {
                try {
                    val googleEvents = repository.getGoogleCalendarEvents(googleAccount)
                    events.addAll(googleEvents)
                } catch (e: Exception) {
                    Log.e(TAG, "Error fetching Google Calendar events", e)
                }
            }
            
            // Schedule notifications for all events
            scheduleEventNotifications(events)
            
            // Update last sync time
            prefs.edit().putLong(Constants.PREF_LAST_SYNC, System.currentTimeMillis()).apply()
            
            Log.d(TAG, "Calendar sync completed, found ${events.size} events")
            
            // Schedule the next sync based on the configured frequency
            scheduleNextSync()
            
            return Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error during calendar sync", e)
            return Result.failure()
        }
    }
    
    /**
     * Get the list of selected calendar IDs from preferences
     */
    private fun getSelectedCalendarIds(): List<String> {
        val json = prefs.getString(Constants.PREF_SELECTED_CALENDARS, null) ?: return emptyList()
        val type = object : TypeToken<List<String>>() {}.type
        return try {
            Gson().fromJson(json, type)
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing selected calendars", e)
            emptyList()
        }
    }
    
    /**
     * Schedule notifications for all events
     */
    private fun scheduleEventNotifications(events: List<CalendarEvent>) {
        val now = Date()
        
        for (event in events) {
            // Skip events that have already passed
            if (event.startTime.before(now)) {
                continue
            }
            
            // Calculate time until event and 10 minutes before
            val timeUntilEvent = event.startTime.time - now.time
            val timeUntil10MinBefore = timeUntilEvent - Constants.TEN_MINUTES_IN_MILLIS
            
            // Schedule notification for 10 minutes before the event
            if (timeUntil10MinBefore > 0) {
                scheduleNotification(
                    event,
                    timeUntil10MinBefore,
                    Constants.NOTIFICATION_TYPE_10MIN
                )
            }
            
            // Schedule notification for the event start time
            if (timeUntilEvent > 0) {
                scheduleNotification(
                    event,
                    timeUntilEvent,
                    Constants.NOTIFICATION_TYPE_NOW
                )
            }
        }
    }
    
    /**
     * Schedule a notification for an event
     */
    private fun scheduleNotification(
        event: CalendarEvent,
        delayMillis: Long,
        notificationType: String
    ) {
        // Create input data for the worker
        val inputData = Data.Builder()
            .putString(Constants.EVENT_ID_KEY, event.id)
            .putString(Constants.EVENT_TITLE_KEY, event.title)
            .putLong(Constants.EVENT_START_TIME_KEY, event.startTime.time)
            .putString(Constants.EVENT_LOCATION_KEY, event.location)
            .putString(Constants.NOTIFICATION_TYPE_KEY, notificationType)
            .build()
        
        // Create a unique work name for this notification
        val workName = "${Constants.EVENT_WORK_NAME_PREFIX}${event.id}_${notificationType}"
        
        // Create the work request
        val notificationWork = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInputData(inputData)
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .build()
        
        // Enqueue the work request
        workManager.enqueueUniqueWork(
            workName,
            ExistingWorkPolicy.REPLACE,
            notificationWork
        )
        
        Log.d(TAG, "Scheduled $notificationType notification for event: ${event.title}")
    }
    
    /**
     * Schedule the next calendar sync
     */
    private fun scheduleNextSync() {
        // Get the sync frequency from preferences (default to 1 hour)
        val syncFrequencyMinutes = prefs.getInt(
            Constants.PREF_SYNC_FREQUENCY,
            Constants.SYNC_FREQUENCY_1HOUR
        )
        
        // Create the work request
        val syncWork = OneTimeWorkRequestBuilder<CalendarSyncWorker>()
            .setInitialDelay(syncFrequencyMinutes.toLong(), TimeUnit.MINUTES)
            .build()
        
        // Enqueue the work request
        workManager.enqueueUniqueWork(
            Constants.SYNC_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            syncWork
        )
        
        Log.d(TAG, "Scheduled next sync in $syncFrequencyMinutes minutes")
    }
    
    companion object {
        /**
         * Schedule an immediate calendar sync
         */
        fun scheduleImmediateSync(context: Context) {
            val workManager = WorkManager.getInstance(context)
            
            // Create the work request
            val syncWork = OneTimeWorkRequestBuilder<CalendarSyncWorker>()
                .build()
            
            // Enqueue the work request
            workManager.enqueueUniqueWork(
                Constants.SYNC_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                syncWork
            )
            
            Log.d("CalendarSyncWorker", "Scheduled immediate sync")
        }
    }
}
