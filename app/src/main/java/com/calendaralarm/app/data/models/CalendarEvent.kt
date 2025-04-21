package com.calendaralarm.app.data.models

import java.util.Date

/**
 * Data class representing a calendar event
 */
data class CalendarEvent(
    val id: String,
    val title: String,
    val startTime: Date,
    val endTime: Date,
    val location: String?,
    val calendarId: String,
    val calendarName: String,
    val notificationsScheduled: Boolean = false
) {
    /**
     * Check if the event is happening now
     */
    fun isHappeningNow(): Boolean {
        val now = Date()
        return now.after(startTime) && now.before(endTime)
    }
    
    /**
     * Check if the event is upcoming (in the future)
     */
    fun isUpcoming(): Boolean {
        return startTime.after(Date())
    }
    
    /**
     * Check if the event is within the next 10 minutes
     */
    fun isWithinNext10Minutes(): Boolean {
        val now = Date()
        val tenMinutesFromNow = Date(now.time + 10 * 60 * 1000)
        return startTime.after(now) && startTime.before(tenMinutesFromNow)
    }
    
    /**
     * Get a formatted string representation of the event time
     */
    fun getFormattedTimeString(): String {
        val dateFormat = android.text.format.DateFormat.getDateFormat(null)
        val timeFormat = android.text.format.DateFormat.getTimeFormat(null)
        
        val startDate = dateFormat.format(startTime)
        val startTimeStr = timeFormat.format(startTime)
        val endTimeStr = timeFormat.format(endTime)
        
        return "$startDate - $startTimeStr to $endTimeStr"
    }
}
