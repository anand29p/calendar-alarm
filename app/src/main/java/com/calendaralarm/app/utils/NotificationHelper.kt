package com.calendaralarm.app.utils

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.calendaralarm.app.MainActivity
import com.calendaralarm.app.R
import com.calendaralarm.app.data.models.CalendarEvent

/**
 * Helper class for creating and showing notifications
 */
class NotificationHelper(private val context: Context) {
    
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    /**
     * Show a notification for an upcoming event (10 minutes before)
     */
    fun showUpcomingEventNotification(event: CalendarEvent) {
        val notificationId = generateNotificationId(event.id, Constants.EVENT_NOTIFICATION_10MIN_SUFFIX)
        val title = context.getString(R.string.event_in_10_min, event.title)
        val content = event.location ?: ""
        
        val notification = createEventNotification(title, content, event, true)
        notificationManager.notify(notificationId, notification)
    }
    
    /**
     * Show a notification for an event that is starting now
     */
    fun showEventStartingNotification(event: CalendarEvent) {
        val notificationId = generateNotificationId(event.id, Constants.EVENT_NOTIFICATION_NOW_SUFFIX)
        val title = context.getString(R.string.event_starting_now, event.title)
        val content = event.location ?: ""
        
        val notification = createEventNotification(title, content, event, true)
        notificationManager.notify(notificationId, notification)
    }
    
    /**
     * Create a notification for a calendar event
     */
    private fun createEventNotification(
        title: String,
        content: String,
        event: CalendarEvent,
        useAlarmSound: Boolean
    ): Notification {
        // Create an intent to open the app when the notification is tapped
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Build the notification
        val builder = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setColor(ContextCompat.getColor(context, R.color.purple_500))
        
        // Set sound and vibration
        if (useAlarmSound) {
            val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            builder.setSound(
                alarmSound,
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            
            // Trigger vibration
            triggerVibration()
        }
        
        return builder.build()
    }
    
    /**
     * Trigger device vibration
     */
    private fun triggerVibration() {
        // Vibration pattern: 0ms delay, 500ms on, 500ms off, 500ms on
        val pattern = longArrayOf(0, 500, 500, 500)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            val vibrator = vibratorManager.defaultVibrator
            
            val effect = VibrationEffect.createWaveform(pattern, -1)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createWaveform(pattern, -1)
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(pattern, -1)
            }
        }
    }
    
    /**
     * Generate a unique notification ID for an event
     */
    private fun generateNotificationId(eventId: String, suffix: Int): Int {
        // Use the event ID's hashcode to generate a base notification ID
        val baseId = eventId.hashCode() and 0xfffffff
        return Constants.EVENT_NOTIFICATION_ID_PREFIX + baseId + suffix
    }
}
