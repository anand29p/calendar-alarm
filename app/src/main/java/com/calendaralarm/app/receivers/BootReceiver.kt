package com.calendaralarm.app.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.calendaralarm.app.workers.CalendarSyncWorker

/**
 * Broadcast receiver for device boot completed
 * Reschedules calendar sync and notifications after device reboot
 */
class BootReceiver : BroadcastReceiver() {
    
    private val TAG = "BootReceiver"
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d(TAG, "Device boot completed, rescheduling calendar sync")
            
            // Schedule an immediate calendar sync to reschedule all notifications
            CalendarSyncWorker.scheduleImmediateSync(context)
        }
    }
}
