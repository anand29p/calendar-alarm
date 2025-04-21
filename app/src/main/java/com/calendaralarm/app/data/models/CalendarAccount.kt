package com.calendaralarm.app.data.models

/**
 * Data class representing a Google Calendar account
 */
data class CalendarAccount(
    val id: String,
    val accountName: String,
    val displayName: String,
    val ownerName: String,
    val color: Int,
    var selected: Boolean = true
)
